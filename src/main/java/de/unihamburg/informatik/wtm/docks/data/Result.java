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
package de.unihamburg.informatik.wtm.docks.data;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a result of speech recognition.
 * Can contain n-best list, phonemes.
 * Can also be serialized and loaded.
 *
 * @author 7twiefel
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1650789290776731090L;

    private static final Logger LOG = LoggerFactory.getLogger(Result.class);

    private List<String> resultList = new ArrayList<String>();
    private float confidence;

    private String hypPhoneme;
    private String refPhoneme;

    /**
     * @return phonemes of hypothesis
     */
    public String getHypPhoneme() {
        return hypPhoneme;
    }

    /**
     * @param hypPhoneme phonemes of hypothesis
     */
    public void setHypPhoneme(String hypPhoneme) {
        this.hypPhoneme = hypPhoneme;
    }

    /**
     * @return phonemes of reference
     */
    public String getRefPhoneme() {
        return refPhoneme;
    }

    /**
     * Sets phonemes of reference
     */
    public void setRefPhoneme(String refPhoneme) {
        this.refPhoneme = refPhoneme;
    }

    /**
     * @return n-best list
     */
    public List<String> getResultList() {
        return resultList;
    }

    /**
     * @param resultList sets n-best list
     */
    public void setResultList(List<String> resultList) {
        this.resultList = resultList;
    }

    /**
     * @param s adds result string to n-best list
     */
    public void addResult(String s) {
        resultList.add(s);
    }

    /**
     * @return best result of n-best list
     */
    public String getBestResult() {
        return resultList.get(0);
    }

    /**
     * @param f sets confidence for best result
     */
    public void setConfidence(float f) {
        confidence = f;
    }

    /**
     * @return n-best list as array
     */
    public String[] getResult() {
        String[] result = new String[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    /**
     * @return printable representation
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("= = = = = = = = =\nResults\nConfidence: ");
        sb.append(confidence);
        sb.append("\nN-Best List:\n");
        for (String s : resultList) {
            sb.append(s);
        }
        sb.append("= = = = = = = = =");
        return sb.toString();
    }

    /**
     * writes n-best list to a words.txt
     */
    public void writeToFile() {
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            // Create file
            fstream = new FileWriter("words.txt");
            out = new BufferedWriter(fstream);
            for (String s : resultList) {
                out.write(s + "\n");
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(fstream);
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * serializes result to a file
     *
     * @param file filename
     */
    public void save(String file) {
        OutputStream fos = null;
        ObjectOutputStream o = null;
        try {
            fos = new FileOutputStream(file);
            o = new ObjectOutputStream(fos);
            o.writeObject(this);

        } catch (IOException e) {
            LOG.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(o);
        }
    }

    /**
     * loads result from file
     *
     * @param file filename
     * @return loaded result
     */
    public static Result load(String file) {
        InputStream fis = null;
        ObjectInputStream o = null;
        Result r = null;

        try {
            fis = new FileInputStream(file);
            o = new ObjectInputStream(fis);
            r = (Result) o.readObject();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage());
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(o);
        }

        return r;
    }
}
