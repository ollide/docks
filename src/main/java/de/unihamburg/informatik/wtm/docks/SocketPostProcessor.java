/**
 * DOCKS is a framework for post-processing results of Cloud-based speech
 * recognition systems.
 * Copyright (C) 2014 Johannes Twiefel
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact:
 * 7twiefel@informatik.uni-hamburg.de
 */
package de.unihamburg.informatik.wtm.docks;

import de.unihamburg.informatik.wtm.docks.Data.Result;
import de.unihamburg.informatik.wtm.docks.PostProcessor.SentencelistPostProcessor;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SocketPostProcessor {

    public static final int PORT = 54015;

    public static void main(String[] args) {
        int port = PORT;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("invalid socket port, falling back to default.");
            }
        }

        ServerSocket listener = null;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            System.out.println("starting server socket on port " + port);
            listener = new ServerSocket(port);
            while (true) {
                System.out.println("waiting for socket connection");
                socket = listener.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String requestString = in.readLine();
                System.out.println("request: " + requestString);

                List<String> expectedResults = parseExpectedSentences(requestString);
                Result googleResult = parseGoogleResult(requestString);

                SentencelistPostProcessor sp = new SentencelistPostProcessor(expectedResults, 1);
                Result r = sp.recognizeFromResult(googleResult);

                String bestResult = r.getBestResult();
                System.out.println("returning best result: " + bestResult);
                out.println(bestResult);
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(listener);
            IOUtils.closeQuietly(socket);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private static LinkedList<String> parseExpectedSentences(String socketRequest) {
        LinkedList<String> expectedSentences = new LinkedList<String>();

        String expected = socketRequest.split("===")[0];
        for (String e : expected.split("\\|")) {
            expectedSentences.add(e);
        }
        return expectedSentences;
    }

    private static Result parseGoogleResult(String socketRequest) {
        Result googleResult = new Result();

        String google = socketRequest.split("===")[1];
        for (String g : google.split("\\|")) {
            googleResult.addResult(g);
        }
        return googleResult;
    }

}