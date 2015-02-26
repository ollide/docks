package de.unihamburg.informatik.wtm.docks.phoneme;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhonemeCreatorTest {

    @Test
    public void testGetInstance() {
        PhonemeCreator instance1 = PhonemeCreator.getInstance();
        PhonemeCreator instance2 = PhonemeCreator.getInstance();

        Assert.assertTrue("getInstance() should return the same instance.", instance1 == instance2);
    }

    @Test
    public void testGetPhonemesWithRawList() {
        PhonemeCreator pc = PhonemeCreator.getInstance();

        List<String> words = new LinkedList<String>();
        words.add("Hello");
        words.add("my name");
        words.add("is");
        words.add("test");

        ArrayList<PhonemeContainer> phonemes = pc.getPhonemes(words);
        Assert.assertEquals("Size of phoneme list should match the word list size.", words.size(), phonemes.size());

        for (int i = 0; i < words.size(); i++) {
            String phonemeWord = phonemes.get(i).getResult();
            String word = words.get(i);

            Assert.assertEquals("Phoneme's words should match the initial word.", phonemeWord.toLowerCase(), word.toLowerCase());
        }
    }
}
