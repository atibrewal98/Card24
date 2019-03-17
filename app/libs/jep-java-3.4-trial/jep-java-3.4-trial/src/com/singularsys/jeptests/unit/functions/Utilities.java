package com.singularsys.jeptests.unit.functions;

import junit.framework.Assert;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.functions.PostfixMathCommand;

public class Utilities {
    /**
     * Utility method for testing unary methods
     * @param pfmc
     * @param arg
     * @param expected
     */
    public static void testUnary(PostfixMathCommand pfmc, double arg, double expected)
    {
        java.util.Stack<Object> stack = new java.util.Stack<Object>();
        stack.push(new Double(arg));
        pfmc.setCurNumberOfParameters(1);
        try {
            pfmc.run(stack);
        } catch (EvaluationException e) {
            Assert.fail();
        }
        Object returnValue = stack.pop();

        if (returnValue instanceof Double) {
            Assert.assertEquals(expected, ((Double)returnValue).doubleValue());
        } else {
            Assert.fail();
        }
    }
}
