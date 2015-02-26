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

import de.unihamburg.informatik.wtm.docks.data.Result;

/**
 * word list postprocessor using a list of words to postprocess a result word by word
 *
 * @author 7twiefel
 */
public class WordlistPostProcessor implements PostProcessor {

    private static final String NAME = "LexiconLookupRecognizer";

    private SentencelistPostProcessor lr;

    /**
     * create a new wordlist postprocessor
     *
     * @param wordFile path to word list
     */
    public WordlistPostProcessor(String wordFile) {
        // use a Sentencelist postprocessor internally
        this.lr = new SentencelistPostProcessor(wordFile, 1);
    }

    /**
     * postprocess a result from another ASR
     *
     * @param r the result
     */
    @Override
    public Result recognizeFromResult(Result r) {
        // split the best result into words
        Result result = new Result();
        String hyp = r.getBestResult();
        String[] words = hyp.split(" ");
        String res = null;

        // match each word against the list of words
        for (String s : words) {
            Result rTemp = new Result();
            rTemp.addResult(s);
            rTemp = lr.recognizeFromResult(rTemp);
            if (res == null) {
                res = rTemp.getBestResult();
            } else {
                res = res + " " + rTemp.getBestResult();
            }
        }
        result.addResult(res);

        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
