package com.singularsys.jeptests.unit;

import org.junit.Test;

import com.singularsys.jep.standard.Complex;
import static org.junit.Assert.*;

public class ComplexTest {

    /**
     * Tests the power method
     */
    @Test
    public void testPower() {
        Complex one = new Complex(1, 0);
        Complex negOne = new Complex(-1, 0);
        Complex i = new Complex(0, 1);
        Complex two = new Complex(2, 0);

        // power
        assertTrue((one.power(one)).equals(one,0));
        assertTrue((one.power(-1)).equals(one,0));
        assertTrue((one.power(negOne)).equals(one,0));
        assertTrue((negOne.power(two)).equals(one,0));
        assertTrue((i.power(two)).equals(negOne, 0));
        //assertTrue((negEight.power(1.0/3)).equals(negTwo,0));
    }

    /**
     * Tests the mul method
     */
    @Test
    public void testMul() {
        Complex one = new Complex(1, 0);
        Complex negOne = new Complex(-1, 0);
        Complex i = new Complex(0, 1);

        // multiplication
        assertTrue((one.mul(one)).equals(one,0));
        assertTrue((one.mul(negOne)).equals(negOne,0));
        assertTrue((negOne.mul(one)).equals(negOne,0));
        assertTrue((i.mul(i)).equals(negOne,0));
    }
}
