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
        sentenceList.add("Maybe");

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
    }
}
