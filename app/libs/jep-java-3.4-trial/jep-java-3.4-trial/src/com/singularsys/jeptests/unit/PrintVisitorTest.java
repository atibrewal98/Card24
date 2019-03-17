package com.singularsys.jeptests.unit;

import org.junit.*;
import com.singularsys.jep.*;

public class PrintVisitorTest {
    
    Jep jep;
    PrintVisitor pv;
    
    @Before
    public void before()
    {
        jep = new Jep();
        pv = jep.getPrintVisitor();
    }

    @Test
    public void testDefaults() {
        Assert.assertFalse("Wrong default FULL_BRACKET mode", 
                pv.getMode(PrintVisitor.FULL_BRACKET));
        Assert.assertFalse("Wrong default COMPLEX_I mode", 
                pv.getMode(PrintVisitor.COMPLEX_I));
    }
    
    @Test
    public void testSetGetMode() {
        pv.setMode(PrintVisitor.FULL_BRACKET, false);
        Assert.assertFalse(pv.getMode(PrintVisitor.FULL_BRACKET));
        pv.setMode(PrintVisitor.FULL_BRACKET, true);
        Assert.assertTrue(pv.getMode(PrintVisitor.FULL_BRACKET));

        pv.setMode(PrintVisitor.COMPLEX_I, false);
        Assert.assertFalse(pv.getMode(PrintVisitor.COMPLEX_I));
        pv.setMode(PrintVisitor.COMPLEX_I, true);
        Assert.assertTrue(pv.getMode(PrintVisitor.COMPLEX_I));
    }
    
    
    @Test
    public void testExpectedOutput() throws JepException {
        // ensure FULL_BRACKET is off
        pv.setMode(PrintVisitor.FULL_BRACKET, false);
        
        // no operators
        checkExpression("a", "a");
        checkExpression("1", "1.0");
        checkExpression("(a)", "a");
        checkExpression("((a))", "a");
        
        // addition only
        checkExpression("a+b+c",   "a+b+c");
        checkExpression("a+(b+c)", "a+b+c");
        checkExpression("(a+b)+c", "a+b+c");

        // subtraction only
        checkExpression("(a-b)-c", "a-b-c");
        checkExpression("a-(b-c)", "a-(b-c)");
        
        // addition and subtraction
        checkExpression("a-(b+c)", "a-(b+c)");
        
        // addition and multiplication
        checkExpression("a*(b+c)", "a*(b+c)");
        checkExpression("a+b*c",   "a+b*c");
        checkExpression("(a+b)*c", "(a+b)*c");
        checkExpression("a*b+c",   "a*b+c");
    }
    
    private void checkExpression(String orig, String expected) throws JepException {
        jep.parse(orig);
        Assert.assertEquals(expected, pv.toString(jep.getLastRootNode()));
    }
/*
    @Test
    public void testInit() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintNodePrintStream() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintWrap() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintlnNodePrintStream() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintlnNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testToStringNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testAppend() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddSpecialRuleOperatorPrintRulesI() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddSpecialRuleStringPrintRulesI() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddSpecialVarName() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintNoBrackets() {
        fail("Not yet implemented");
    }

    @Test
    public void testPrintBrackets() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitASTFunNodeObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitASTOpNodeObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitASTVarNodeObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testVisitASTConstantObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testFormatValueObjectStringBuffer() {
        fail("Not yet implemented");
    }

    @Test
    public void testFormatValueObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetMode() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetModeInt() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetMode() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetNumberFormat() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetMaxLen() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetMaxLen() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLightWeightInstance() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetLBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetRBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFunLBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetFunLBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFunRBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetFunRBracket() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFunArgSep() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetFunArgSep() {
        fail("Not yet implemented");
    }*/

}
