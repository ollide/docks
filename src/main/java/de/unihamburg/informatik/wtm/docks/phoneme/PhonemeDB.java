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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * containes the stored phonemes (used by phoneme creator)
 *
 * @author 7twiefel
 */
public class PhonemeDB implements Serializable {

    private static final long serialVersionUID = 6246046410395137574L;

    // TODO: hashContent is never used (?)
    private HashMap<String, String[]> hashContent = new HashMap<String, String[]>();
    private List<PhonemeContainer> arrayContent = new ArrayList<PhonemeContainer>();

    public List<PhonemeContainer> getPhonemes() {
        return arrayContent;
    }

    public void setPhonemes(List<PhonemeContainer> phonemes) {
        arrayContent = phonemes;
    }

    public void addHashContent(String words, String[] phonemes) {
        hashContent.put(words, phonemes);
    }

}
