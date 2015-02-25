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

import de.unihamburg.informatik.wtm.docks.data.Result;
import de.unihamburg.informatik.wtm.docks.postprocessor.SentencelistPostProcessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class SocketPostProcessor {

    public static final int PORT = 54015;

    private static final Logger LOG = LoggerFactory.getLogger(SocketPostProcessor.class);

    public static void main(String[] args) {
        int port = PORT;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                LOG.warn("invalid socket port, falling back to default ({}).", PORT);
            }
        }

        ServerSocket listener = null;
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            LOG.info("starting server socket on port {}", port);
            listener = new ServerSocket(port);
            while (true) {
                LOG.debug("waiting for socket connection");
                socket = listener.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String requestString = in.readLine();
                LOG.debug("request: {}", requestString);

                List<String> expectedResults = parseExpectedSentences(requestString);
                Result googleResult = parseGoogleResult(requestString);

                SentencelistPostProcessor sp = new SentencelistPostProcessor(expectedResults, 1);
                Result r = sp.recognizeFromResult(googleResult);

                String bestResult = r.getBestResult();
                LOG.debug("returning best result: {}", bestResult);
                out.println(bestResult);
            }
        } catch (Exception e) {
            LOG.error("error: ", e.getMessage());
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
