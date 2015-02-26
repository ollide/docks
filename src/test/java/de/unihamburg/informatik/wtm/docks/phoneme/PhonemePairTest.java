package de.unihamburg.informatik.wtm.docks.phoneme;

import org.junit.Assert;
import org.junit.Test;

public class PhonemePairTest {

    @Test
    public void testEquals() {
        PhonemePair p1 = new PhonemePair("", "");
        PhonemePair p2 = new PhonemePair("", "");
        Assert.assertTrue("PhonemePairs should be equal.", p1.equals(p2));
        Assert.assertTrue("PhonemePairs should be equal.", p2.equals(p1));

        PhonemePair p3 = new PhonemePair("Test String", "1");
        PhonemePair p4 = new PhonemePair("Test String", "1");
        Assert.assertTrue("PhonemePairs should be equal.", p3.equals(p4));
        Assert.assertFalse("PhonemePairs shouldn't be equal.", p3.equals(p2));
        Assert.assertFalse("PhonemePairs shouldn't be equal.", p3.equals(p1));
    }

    @Test
    public void testHashCode() {
        PhonemePair p1 = new PhonemePair("", "");
        PhonemePair p2 = new PhonemePair("", "");
        Assert.assertEquals("PhonemePair hash codes should be equal.", p1.hashCode(), p2.hashCode());

        PhonemePair p3 = new PhonemePair("Test String", "1");
        PhonemePair p4 = new PhonemePair("Test String", "1");
        Assert.assertEquals("PhonemePair hash codes should be equal.", p3.hashCode(), p4.hashCode());
        Assert.assertNotEquals("PhonemePair hash codes shouldn't be equal.", p3.hashCode(), p2.hashCode());
        Assert.assertNotEquals("PhonemePair hash codes shouldn't be equal.", p3.hashCode(), p1.hashCode());
    }
}
