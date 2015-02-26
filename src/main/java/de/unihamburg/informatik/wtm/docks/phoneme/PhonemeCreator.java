/**
 * DOCKS is a framework for post-processing results of Cloud-based speech 
 * recognition systems.
 * Copyright (C) 2014 Johannes Twiefel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact:
 * 7twiefel@informatik.uni-hamburg.de
 */
package de.unihamburg.informatik.wtm.docks.phoneme;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;

import edu.cmu.sphinx.linguist.g2p.G2PConverter;
import edu.cmu.sphinx.linguist.g2p.Path;

import de.unihamburg.informatik.wtm.docks.data.Result;
import de.unihamburg.informatik.wtm.docks.utils.Printer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class used to convert graphemes to phonemes.
 * calls SequiturG2P internally
 *
 * @author 7twiefel
 */
public class PhonemeCreator {

    private static final Logger LOG = LoggerFactory.getLogger(PhonemeCreator.class);

    private static final String TAG = "PhonemeCreator";
    private static PhonemeCreator instance;

    private G2PConverter g2pDecoder;
    /**
     * the phoneme data base created for a predefined list of sentences or words
     */
    private PhonemeDB pdb;

    /**
     * creates a new phoneme creator and caches the results for the list of sentences
     *
     * @param sentenceFile
     */
    public PhonemeCreator(String sentenceFile) {
        this();
        InputStream fis = null;
        ObjectInputStream o1 = null;

        try {
            // try to read the cached phonemes
            fis = new FileInputStream(sentenceFile + ".ser");
            o1 = new ObjectInputStream(fis);

            pdb = new PhonemeDB();
            pdb = (PhonemeDB) o1.readObject();

            LOG.info("successfully loaded phoneme db {}.ser", sentenceFile);
        } catch (Exception e) {
            LOG.info("failed to read cached phonemes from {}.ser, creating new database.", sentenceFile);

            // if no cached phonemes are available cache ones
            fillDatabase(sentenceFile);

        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(o1);
        }
    }

    /**
     * creates a new phoneme creator. used when no precached results of a list of sentences should be loaded
     */
    private PhonemeCreator() {
        ClassLoader cl = this.getClass().getClassLoader();
        URL sequiturSphinxModel = cl.getResource("g2p/sequitur/cmudict_sequitur.fst.ser");
        try {
            g2pDecoder = new G2PConverter(sequiturSphinxModel);
        } catch (IOException e) {
            throw new RuntimeException("failed to create G2PConverter with given SphinxModel");
        }
    }

    /**
     * creates an instance of a phoneme creator. used when no precached results of a list of sentences should be loaded
     */
    public static synchronized PhonemeCreator getInstance() {
        if (instance == null) {
            instance = new PhonemeCreator();
        }
        return instance;
    }

    /**
     * creates a list of phonemes corresponding to the list of results contained in r
     *
     * @param r Result received from a speech recognizer or postprocessor. needs to contain 1 result as string as a minimum
     * @return list of phonemes wrapped into a PhonemeContainer
     */
    public List<PhonemeContainer> getPhonemes(Result r) {
        return getPhonemes(r.getResultList());
    }

    /**
     * Creates a list of phonemes corresponding to the list of raw results.
     *
     * @param rawResults Result list received from a speech recognizer or postprocessor. needs to contain 1 entry as string as a minimum
     * @return list of phonemes wrapped into a PhonemeContainer
     */
    public List<PhonemeContainer> getPhonemes(List<String> rawResults) {
        Printer.printWithTimeF(TAG, "getting Phonemes");

        List<PhonemeContainer> resultsWithPhonemes = new ArrayList<PhonemeContainer>();

        if (rawResults == null) {
            return resultsWithPhonemes;
        }

        Printer.printWithTimeF(TAG, "formatting raw results");
        // convert all results to lowercase and remove special characters
        for (String s : rawResults) {
            Printer.printWithTimeF(TAG, "raw result: " + s);

            s = s.replaceAll("[^a-zA-Z 0-9]", "");
            s = s.replaceAll(" +", " ");
            if ("".equals(s)) {
                rawResults.remove(s);
                continue;
            }
            if (s.charAt(0) == ' ') {
                s = s.substring(1);
            }

            // split the sentences to words and add theses to the args for SequiturG2P
            String[] words = s.toLowerCase().split(" ");

            PhonemeContainer pc = new PhonemeContainer(words);

            ArrayList<Path> paths = g2pDecoder.phoneticize(s, 1);
            Path p = paths.get(0);

            String[] phonemes = new String[p.getPath().size()];
            p.getPath().toArray(phonemes);
            pc.addPhonemesNoJep(phonemes);

            resultsWithPhonemes.add(pc);
        }

        return resultsWithPhonemes;
    }

    public PhonemeDB getPhonemeDb() {
        return pdb;
    }

    private void fillDatabase(String sentenceFile) {
        pdb = new PhonemeDB();

        Scanner in = null;
        try {
            // reads sentence file
            in = new Scanner(new FileReader(sentenceFile + ".txt"));
            in.useDelimiter("\n");

            Result r = new Result();
            String temp;

            // separate words and add the to the input arg
            while (in.hasNext()) {
                temp = in.next();
                if (temp.contains("\r")) {
                    temp = temp.substring(0, temp.length() - 1);
                } else temp = temp.substring(0, temp.length());

                r.addResult(temp);
            }
            LOG.debug("getting results");

            // get the phonemes
            List<PhonemeContainer> phonemes = getPhonemes(r);

            LOG.debug("phoneme creation successful!");

            // set the public data base to the phonemes
            pdb.setPhonemes(getPhonemes(r));

            // add the phonemes to the database
            for (PhonemeContainer res : phonemes) {
                pdb.addHashContent(res.getResult(), res.getPhonemes());
            }

            OutputStream fos = null;
            ObjectOutputStream o = null;

            // serialize the database
            try {
                fos = new FileOutputStream(sentenceFile + ".ser");
                o = new ObjectOutputStream(fos);
                o.writeObject(pdb);

            } catch (IOException e) {
                LOG.error("failed to serialize phoneme database {}.ser, error: {}",
                        sentenceFile, e.getMessage());
            } finally {
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(o);
            }

        } catch (FileNotFoundException e) {
            LOG.error("failed to load sentence file {}. PhonemeDB is still empty.", sentenceFile);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}
