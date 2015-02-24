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
package de.unihamburg.informatik.wtm.docks;

import org.apache.commons.cli.*;

public class DocksMain {

    private static final String OPTION_HELP = "help";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(OPTION_HELP, false, "print this message");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.getOptions().length == 0) {
                printHelp(options);
            }

            if (cmd.hasOption(OPTION_HELP)) {
                printHelp(options);
            }

        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("docks", options);
    }
}
