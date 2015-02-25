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
package de.unihamburg.informatik.wtm.docks.postprocessor2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unihamburg.informatik.wtm.docks.data2.LevenshteinResult;
import de.unihamburg.informatik.wtm.docks.data2.Result;
import de.unihamburg.informatik.wtm.docks.phoneme2.PhonemeContainer;
import de.unihamburg.informatik.wtm.docks.phoneme2.PhonemeCreator;
import de.unihamburg.informatik.wtm.docks.postprocessor2.levenshteinbased.Levenshtein;
import de.unihamburg.informatik.wtm.docks.utils2.Printer;

/**
 * Sentencelist heuristic. Used to match a given result containing n-best list against a list of sentences
 *
 * @author 7twiefel
 */
public class SentencelistPostProcessor implements PostProcessor {

    private String TAG = "LevenshteinRecognizer";
    private PhonemeCreator pc;
    private ArrayList<PhonemeContainer> phonemesGrammar;
    private Levenshtein ls;
    private int numberOfResults;
    private int referenceRecognizer;
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
        Printer.printWithTime(TAG, "loading phoneme database");
        pc = new PhonemeCreator(sentenceFile);
        Printer.printWithTime(TAG, "getting phonemes for speech result");
        ls = new Levenshtein();
        phonemesGrammar = pc.pdb.arrayContent;
        this.numberOfResults = numberOfResults;
        referenceRecognizer = -1;
        Printer.printWithTime(TAG, "SentencelistPostProcessor created");
    }

    public SentencelistPostProcessor(List<String> sentences, int numberOfResults) {
        pc = PhonemeCreator.getInstance();
        ls = new Levenshtein();
        phonemesGrammar = pc.getPhonemes(sentences);
        this.numberOfResults = numberOfResults;
        referenceRecognizer = -1;
    }

    /**
     * postprocess a result given by e.g. Google ASR
     *
     * @param r the result
     */
    @Override
    @SuppressWarnings("unchecked")
    public Result recognizeFromResult(Result r) {

        //get phonemes for r
        ArrayList<PhonemeContainer> phonemesSpeech = pc.getPhonemes(r);


        if (phonemesSpeech != null) {
            Printer.printWithTime(TAG, "calculating levenshtein distances");

            int minDist = 10000;

            int result = -1;

            Printer.printWithTime(TAG, "phonemesGrammar.size: " + phonemesGrammar.size());

            //if one result is preferred
            if (numberOfResults == 1) {
                //calculate Levenshtein distance for n-best list vs sentence list
                //take the minimal distance
                for (int i = 0; i < phonemesSpeech.size(); i++) {
                    for (int j = 0; j < phonemesGrammar.size(); j++) {
                        int diff = ls.diff(phonemesSpeech.get(i).getPhonemes(),
                                phonemesGrammar.get(j).getPhonemes());
                        if (diff <= minDist) {
                            if (diff < minDist) {
                                minDist = diff;
                                result = j;

                            }
                        }

                    }
                }

                Printer.printWithTime(TAG, "result is : " + result);
                //return sentence with the minimal distance
                //phonemesGrammar.get(result).print();
                r = new Result();
                r.addResult(phonemesGrammar.get(result).getResult());

            } else {
                //do the same if more results are preferred
                ArrayList<LevenshteinResult> resultList = new ArrayList<LevenshteinResult>();
                for (int i = 0; i < phonemesSpeech.size(); i++) {
                    for (int j = 0; j < phonemesGrammar.size(); j++) {
                        int diff = ls.diff(phonemesSpeech.get(i).getPhonemes(),
                                phonemesGrammar.get(j).getPhonemes());
                        resultList.add(new LevenshteinResult(diff, j, i));
                    }
                }

                //sort the list of results by smallest distance
                Collections.sort(resultList);
                r = new Result();

                for (int i = 0; (i < numberOfResults) && (i < resultList.size()); i++) {
                    r.addResult(phonemesGrammar.get(resultList.get(i).getId()).getResult());
                }

            }

            Printer.printWithTimeF(TAG, "levenshtein distances calculated");

        }
        return r;
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
        for (String s : array)
            r.addResult(s);
        //get phonemes
        ArrayList<PhonemeContainer> phonemesSpeech = pc.getPhonemes(r);
        //calculate distances
        for (int i = 1; i < phonemesSpeech.size(); i++) {
            int diff = ls.diff(phonemesSpeech.get(i).getPhonemes(),
                    phonemesSpeech.get(0).getPhonemes());
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
