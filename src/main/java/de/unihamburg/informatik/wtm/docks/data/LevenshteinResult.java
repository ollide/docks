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

/**
 * This class is a utility class to be able to compare and sort Levenshtein distances of a Collection
 *
 * @author 7twiefel
 */

public class LevenshteinResult implements Comparable {

    private int distance;
    private int index;

    /**
     * @param distance Levenshtein distance
     * @param index    index of reference in a given list
     */
    public LevenshteinResult(int distance, int index) {
        this.distance = distance;
        this.index = index;
    }

    /**
     * @return Levenshtein distance between input and reference
     */
    public int getDistance() {
        return distance;
    }

    /**
     * @return reference index
     */
    public int getIndex() {
        return index;
    }

    @Override
    public int compareTo(Object o) {
        LevenshteinResult otherResult = (LevenshteinResult) o;
        if (this.distance < otherResult.distance) {
            return -1;
        } else if (this.distance == otherResult.distance) {
            return 0;
        } else {
            return 1;
        }
    }

}
