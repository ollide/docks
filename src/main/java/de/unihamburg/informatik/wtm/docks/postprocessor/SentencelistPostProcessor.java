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
package de.unihamburg.informatik.wtm.docks.postprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unihamburg.informatik.wtm.docks.data.LevenshteinResult;
import de.unihamburg.informatik.wtm.docks.data.Result;
import de.unihamburg.informatik.wtm.docks.phoneme.PhonemeContainer;
import de.unihamburg.informatik.wtm.docks.phoneme.PhonemeCreator;
import de.unihamburg.informatik.wtm.docks.postprocessor.levenshteinbased.Levenshtein;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sentencelist heuristic. Used to match a given result containing n-best list against a list of sentences
 *
 * @author 7twiefel
 */
public class SentencelistPostProcessor implements PostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SentencelistPostProcessor.class);

    private PhonemeCreator pc;
    private List<PhonemeContainer> phonemesGrammar;
    private int numberOfResults;
    private int referenceRecognizer = -1;
    private String name = "LevenshteinRecognizer";

    /**
     * Creates a new Sentencelist postprocessor
     *
     * @param sentenceFile        path to list of sentences
     * @param numberOfResults     number of results to be returned (1 is fastest)
     * @param referenceRecognizer recognizer the result is postprocessed from
     * @param name                of the recognizer
     */
    public SentencelistPostProcessor(String sentenceFile, int numberOfResults, int referenceRecognizer, String name) {
        this(sentenceFile, numberOfResults);
        this.referenceRecognizer = referenceRecognizer;
        this.name = name;
    }

    public SentencelistPostProcessor(String sentenceFile, int numberOfResults) {
        LOG.debug("loading phoneme database");
        pc = new PhonemeCreator(sentenceFile);

        LOG.debug("getting phonemes for speech result");
        phonemesGrammar = pc.getPhonemeDb().getPhonemes();
        this.numberOfResults = numberOfResults;

        LOG.debug("SentencelistPostProcessor created");
    }

    public SentencelistPostProcessor(List<String> sentences, int numberOfResults) {
        pc = PhonemeCreator.getInstance();
        phonemesGrammar = pc.getPhonemes(sentences);
        this.numberOfResults = numberOfResults;
    }

    /**
     * postprocess a result given by e.g. Google ASR
     *
     * @param r the result
     */
    @Override
    public Result recognizeFromResult(Result r) {
        LOG.debug("recognize from result");
        List<PhonemeContainer> phonemesSpeech = pc.getPhonemes(r);
        Result result = new Result();

        if (phonemesSpeech != null) {

            LOG.debug("calculating levenshtein distances");
            LOG.debug("phonemesGrammar.size: {}", phonemesGrammar.size());

            List<LevenshteinResult> resultList = new ArrayList<LevenshteinResult>();

            for (PhonemeContainer pSpeech : phonemesSpeech) {
                for (int i = 0; i < phonemesGrammar.size(); i++) {
                    int diff = Levenshtein.diff(pSpeech.getPhonemes(), phonemesGrammar.get(i).getPhonemes());
                    resultList.add(new LevenshteinResult(diff, i));
                }
            }

            // sort the list of results by smallest distance
            Collections.sort(resultList);

            for (int i = 0; (i < numberOfResults) && (i < resultList.size()); i++) {
                LevenshteinResult lr = resultList.get(i);
                PhonemeContainer pc = phonemesGrammar.get(lr.getIndex());

                result.addResult(pc.getResult());
            }
        }
        LOG.debug("levenshtein distances calculated");
        return result;
    }

    /**
     * calculate distances of an input vs an array of strings
     *
     * @param input input sentence
     * @param array of reference sentences
     * @return array of distances to the reference sentences
     */
    public double[] calculateAgainstArray(String input, String[] array) {
        double[] res = new double[array.length];

        Result r = new Result();
        r.addResult(input);
        for (String s : array) {
            r.addResult(s);
        }
        // get phonemes
        List<PhonemeContainer> phonemesSpeech = pc.getPhonemes(r);

        final String[] inputPhonemes = phonemesSpeech.get(0).getPhonemes();

        // calculate distances
        for (int i = 1; i < phonemesSpeech.size(); i++) {
            int diff = Levenshtein.diff(phonemesSpeech.get(i).getPhonemes(), inputPhonemes);
            res[i - 1] = diff;
        }
        return res;
    }

    @Override
    public int getReferenceRecognizer() {
        return referenceRecognizer;
    }

    @Override
    public String getName() {
        return name;
    }

}
