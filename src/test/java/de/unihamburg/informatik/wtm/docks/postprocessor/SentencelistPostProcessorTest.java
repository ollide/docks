package de.unihamburg.informatik.wtm.docks.postprocessor;

import de.unihamburg.informatik.wtm.docks.data.Result;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

public class SentencelistPostProcessorTest {

    @Test
    public void testSentenceListPostProcessor() {
        LinkedList<String> sentenceList = new LinkedList<String>();
        sentenceList.add("No");
        sentenceList.add("Yes");
        sentenceList.add("Done");
        sentenceList.add("Maybe");

        // first example

        Result googleResult = new Result();
        googleResult.addResult("yesss");
        googleResult.addResult("yes");
        googleResult.addResult("yes sir");
        googleResult.addResult("yes I");
        googleResult.addResult("I");
        googleResult.addResult("yesss I");

        SentencelistPostProcessor spp = new SentencelistPostProcessor(sentenceList, 1);
        Result result = spp.recognizeFromResult(googleResult);

        Assert.assertEquals("Best result should be 'yes'.", "yes", result.getBestResult());
        Assert.assertEquals("Confidence should be 1.0 (exact match).", 1.0f, result.getConfidence(), 0.001f);

        // second example

        Result googleResult2 = new Result();
        googleResult2.addResult("oh yes definitely");
        googleResult2.addResult("I'm done");

        result = spp.recognizeFromResult(googleResult2);
        Assert.assertEquals("Best result should be 'done'.", "done", result.getBestResult());
        Assert.assertNotEquals("Confidence should be lower than 1.0 (partial match).", 1.0f, result.getConfidence(), 0.01f);
        Assert.assertNotEquals("Confidence should be greater than 0.0 (partial match).", 1.0f, result.getConfidence(), 0.01f);
    }

    @Test
    public void testSpecialCharacters() {
        LinkedList<String> sentenceList = new LinkedList<String>();
        sentenceList.add("I'm done");

        Result googleResult = new Result();
        googleResult.addResult("I'm gone");

        SentencelistPostProcessor spp = new SentencelistPostProcessor(sentenceList, 1);
        Result result = spp.recognizeFromResult(googleResult);

        Assert.assertEquals("Special characters should be preserved in raw result.", "I'm done", result.getRawResult());

        Assert.assertNotEquals("Special characters aren't preserved in regular result.", "I'm done", result.getBestResult());
        Assert.assertEquals("Special characters aren't preserved in regular result.", "im done", result.getBestResult());
    }
}
