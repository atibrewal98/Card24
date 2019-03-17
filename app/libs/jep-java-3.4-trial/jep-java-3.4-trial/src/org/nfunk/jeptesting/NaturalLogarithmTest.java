package org.nfunk.jeptesting;

import org.junit.Test;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.functions.NaturalLogarithm;

import junit.framework.Assert;

public class NaturalLogarithmTest {

    /**
     * Test method for 'org.nfunk.jep.function.Logarithm.run(Stack)'
     * Tests the return value of log(NaN). This is a test for bug #1177557
     */
    @Test
    public void testNaturalLogarithm() {
        NaturalLogarithm logFunction = new NaturalLogarithm();
        java.util.Stack<Object> stack = new java.util.Stack<Object>();
        stack.push(new Double(Double.NaN));
        try {
            logFunction.run(stack);
        } catch (EvaluationException e) {
            Assert.fail();
        }
        Object returnValue = stack.pop();

        if (returnValue instanceof Double) {
            Assert.assertTrue(Double.isNaN(((Double)returnValue).doubleValue()));
        } else {
            Assert.fail();
        }
    }

}
