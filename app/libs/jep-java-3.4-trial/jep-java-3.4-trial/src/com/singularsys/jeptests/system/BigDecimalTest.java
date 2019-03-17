package com.singularsys.jeptests.system;


import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;
import static org.junit.Assert.*;

import com.singularsys.jep.*;
import com.singularsys.jep.bigdecimal.BigDecComponents;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.singularsys.jep.functions.strings.StringFunctionSet;
import com.singularsys.jep.parser.Node;

/**
 * Tests the BigDecComponents through a set of test expressions that are evaluated.
 * @author singularsys
 */
public class BigDecimalTest {
    Jep jep;
    //	PrefixTreeDumper dumper;
    int testCount = 0;

    //	public BigDecimalTest() {
    //		dumper = new PrefixTreeDumper();
    //	}

    @Test
    public void unlimitedPrecisionTest() {
        jep = new Jep(new BigDecComponents());
        //		BigDecimal bd, bd2;
        //		bd = new BigDecimal("1.000000001");
        //		System.out.println(bd);
        //		bd = new BigDecimal("1.000000000000001");
        //		System.out.println(bd);
        //		bd = new BigDecimal("1.0000000000000000000000000000001");
        //		System.out.println(bd);
        //		bd = new BigDecimal("1.000000000000000000000000000000000000000000000001");
        //		System.out.println(bd);
        //		bd = new BigDecimal("1.00000000000000000000000000000000000000000000000000000000000000001");
        //		System.out.println(bd);
        //		bd = new BigDecimal("1.000000000000000000000000000000000000000000000000000000000000000000000000000000001");
        //		System.out.println(bd);
        //		bd2 = new BigDecimal("1e-300");
        //		System.out.println(bd.add(bd2));

        //		BigDecimal x = new BigDecimal(1);
        //		BigDecimal y = new BigDecimal(3);
        //		BigDecimal result = x.divide(y,MathContext.DECIMAL128);
        //		System.out.println(result);
        // init testCount
        testCount = 0;
        // addition
        test("1+1", "2");
        test("2+2", "4");
        test("1/2+1/2", "1.0");

        // subtraction
        test("1-1", "0");

        // unary minus
        test("-1", "-1");
        test("1+(-1)", "0");

        // multiplication
        test("10 * 0.09", "0.90");
        test("0.1 * 0.1", "0.01");

        // division
        test("1/2", "0.5");
        test("1/10", "0.1");
        test("3/3", "1");

        // power
        test("1^1", "1");
        test("1^2", "1");
        test("2^2", "4");
        test("2.01^1", "2.01");
        test(".1^2", "0.01");

        // equal
        test("1==1", true);
        test("1==2", false);
        test("1==-1", false);
        test("1.333333333333333333333333==1.333333333333333333333333", true);
        test("0 == 1-1.000000000000000000000000000000000000000000000000000000000000000001", false);
        test("1 == 1+1e-300", false);

        // not equal
        test("1 != 1", false);
        test("1 != 2", true);
        test("1 != -1", true);
        test("1.333 != 1.333", false);
        test("0 != 1-1.000000000000000000000000000000000000000000000000000000000000000001", true);
        test("1 != 1+1e-300", true);

        // less or equal
        test("1 <= 1", true);
        test("1 <= 2", true);
        test("1 <= -1", false);
        test("1.333333333333333333333333 <= 1.333333333333333333333333", true);
        test("1-1.000000000000000000000000000000000000000000000000000000000000000001 <= 0", true);
        test("1 <= 1+1e-300", true);
        test("1+1e-300 <= 1", false);

        // less than
        test("1 < 1", false);
        test("1 < 2", true);
        test("1 < -1", false);
        test("1.333 < 1.333", false);
        test("0 < 1-1.000000000000000000000000000000000000000000000000000000000000000001", false);
        test("1 < 1+1e-300", true);

        // greater or equal
        test("1 >= 1", 1 >= 1);
        test("1 >= 2", 1 >= 2);
        test("1 >= -1", 1 >= -1);
        test("1.333333333333333333333333 >= 1.333333333333333333333333", true);
        test("1-1.000000000000000000000000000000000000000000000000000000000000000001 >= 0", false);
        test("1 >= 1+1e-300", false);
        test("1+1e-300 >= 1", true);

        // greater than
        test("1 > 1", false);
        test("1 > 2", false);
        test("1 > -1", true);
        test("1.333 > 1.333", false);
        test("0 > 1-1.000000000000000000000000000000000000000000000000000000000000000001", true);
        test("1 > 1+1e-300", false);

        // and
        test("(1 > 1) && (1==1)", false);
        test("1 && 0", false);
        test("0 && 1", false);
        test("0 && 0", false);
        test("1 && 1", true);
        test("3 && 4", true);
        test("1<3 && 3<4", true);
        test("(1+1e-300)-1 && 1", true);

        // or
        test("(1 > 1) || (1==1)", (1 > 1) || (1==1));
        test("1 || 0", true);
        test("0 || 1", true);
        test("0 || 0", false);
        test("1 || 1", true);
        test("3 || 0", true);
        test("1<3 || 3<1", 1<3 || 3<1);
        test("(1+1e-300)-1 || 0", true);

        // not
        test("!1", false);
        test("!0", true);
        test("!(1>0)", false);
        test("!(1<0)", true);


        // print summary
        System.out.println("\n------------------------------------------------");
        System.out.println(testCount + " tests performed successfully.");
    }

    @Test
    public void limitedPrecisionTest() {
        jep = new Jep(new BigDecComponents(MathContext.DECIMAL64));
        // init testCount
        testCount = 0;
        System.out.println("\n-- Limited precision test ----------------------------------------------");
        test("10^-1", "0.1");
        test("10^-2", "0.01");
        test("1/3", "0.3333333333333333");
        test("2/3", "0.6666666666666667");

        // print summary
        System.out.println("\n------------------------------------------------");
        System.out.println(testCount + " tests performed successfully.");
    }

    /**
     * Test expression against boolean
     * @param str
     * @param value
     */
    public void test(String str, boolean value) {
        boolean bresult;
        Object result = eval(str);

        if (result instanceof Boolean) {
            bresult = (Boolean)result;
        } else if (result instanceof Double) {
            bresult = ((Double)result).doubleValue() != 0;
        } else {
            fail("Result isn't Boolean or Double");
            bresult = false;
        }
        assertEquals(bresult, value);
        testCount++;		
    }

    public void test(String str, String value) {
        BigDecimal trueVal = new BigDecimal(value);
        Object result = eval(str);
        // fail if result isn't a BigDecimal
        if (!(result instanceof BigDecimal)) fail("Result isn't BigDecimal");
        BigDecimal bdresult = (BigDecimal)result;
        assertEquals(bdresult, trueVal);
        testCount++;
    }

    public Object eval(String str) {
        System.out.print("\"" + str + "\"  ->  ");
        // try parsing
        try {
            jep.parse(str);
        } catch (ParseException e) {
            // parsing failed
            fail("Parsing failed");
            return null;
        }

        // try evaluating
        try {
            // evaluate
            Object result = jep.evaluate();
            System.out.println(result);
            //			// dump the tree
            //			try {
            //				dumper.walk(jep.getLastRootNode());
            //			} catch (Exception e) {
            //				e.printStackTrace();
            //			}
            return result;
        } catch (EvaluationException e) {
            // evaluation failed, so fail
            fail("Evaluation failed");
            return null;
        }
    }

    @Test
    public void testCP() throws Exception
    {
        jep = new Jep(new BigDecComponents(MathContext.DECIMAL64));
        jep.setComponent(new StandardConfigurableParser());
        Node n = jep.parse("+1");
        Object val = jep.evaluate(n);
        System.out.println(val);
    }

    public void exprEquals(String expr,Object expected) throws JepException {
        Node eqn = jep.parse(expr);
        Object res = jep.evaluate(eqn);
        if(expected.equals(res)) {
            System.out.println("\""+expr+"\" -> "+res.toString());
        }
        else
            System.out.println("ERROR \""+expr+"\" -> "+res.toString() + " expected "+expected.toString());
        
        assertEquals(expr,expected,res); 
    }
    
    public void evalExceptionTest(String expr) throws JepException {
        Node eqn = jep.parse(expr);
        try 
        {
            Object res = jep.evaluate(eqn);
            System.out.println("ERROR: \""+expr+"\" an evaluation should have been thrown. Result "+res);
            fail("ERROR: \""+expr+"\" an evaluation should have been thrown. Result "+res);
        }
        catch(EvaluationException e) {
            System.out.println("\""+expr+"\" raised expected exception "+e.toString());
            
        }
    }
    
    @Test
    public void testSetAllowStrings() throws Exception
    {
        BigDecComponents compSet = new BigDecComponents(MathContext.DECIMAL64);
        jep = new Jep(compSet);
        // test default (should not allow)
        evalExceptionTest("\"ABCD\"==\"ABCD\"");
        
        // try setting to allow
        compSet.setAllowStrings(true);
        exprEquals("\"ABCD\"==\"ABCD\"",Boolean.TRUE);
    }
    
    @Test
    public void testBoolean() throws Exception
    {
        BigDecComponents compSet = new BigDecComponents(MathContext.DECIMAL64,true);
        jep = new Jep(compSet);
        jep.addConstant("true",Boolean.TRUE);
        jep.addConstant("false",Boolean.FALSE);

        exprEquals("!true",Boolean.FALSE);
        exprEquals("!false",Boolean.TRUE);

        exprEquals("true==true",Boolean.TRUE);
        exprEquals("true!=true",Boolean.FALSE);
        exprEquals("true<true",Boolean.FALSE);
        exprEquals("true<=true",Boolean.TRUE);
        exprEquals("true>true",Boolean.FALSE);
        exprEquals("true>=true",Boolean.TRUE);
      
        exprEquals("false==false",Boolean.TRUE);
        exprEquals("false!=false",Boolean.FALSE);
        exprEquals("false<false",Boolean.FALSE);
        exprEquals("false<=false",Boolean.TRUE);
        exprEquals("false>false",Boolean.FALSE);
        exprEquals("false>=false",Boolean.TRUE);
      
        exprEquals("true == false",Boolean.FALSE);
        exprEquals("true != false",Boolean.TRUE);
        exprEquals("true <  false",Boolean.FALSE);
        exprEquals("true <= false",Boolean.FALSE);
        exprEquals("true >  false",Boolean.TRUE);
        exprEquals("true >= false",Boolean.TRUE);

        exprEquals("false == true",Boolean.FALSE);
        exprEquals("false != true",Boolean.TRUE);
        exprEquals("false <  true",Boolean.TRUE);
        exprEquals("false <= true",Boolean.TRUE);
        exprEquals("false >  true",Boolean.FALSE);
        exprEquals("false >= true",Boolean.FALSE);

        evalExceptionTest("123==false");
        evalExceptionTest("false==123");
        evalExceptionTest("123+false");
        evalExceptionTest("false+123");

    }
    /**
     * Tests inter-operability of big decimals and strings.
     * @throws Exception
     */
    @Test
    public void testBDString() throws Exception
    {
        BigDecComponents compSet = new BigDecComponents(MathContext.DECIMAL64, true);
        jep = new Jep(compSet);
        jep.setComponent(new StringFunctionSet());
        jep.addConstant("true",Boolean.TRUE);
        jep.addConstant("false",Boolean.FALSE);

        test("1+1","2");
        test("1+1==2",true);
        test("1+1==3",false);
        
        String expr = "left(\"abcdef\",3)";
        exprEquals(expr,"abc");
        
        exprEquals("\"ABCD\"==\"ABCD\"",Boolean.TRUE);
        exprEquals("\"ABCD\"==\"ABCDE\"",Boolean.FALSE);
        exprEquals("left(\"abcdef\",3) == \"abc\"",Boolean.TRUE);
        
        evalExceptionTest("\"ABCD\"==123");
        evalExceptionTest("123==\"ABCD\"");
        exprEquals("123==123",Boolean.TRUE);
        exprEquals("123==423",Boolean.FALSE);
        

        exprEquals("\"ABCD\"!=\"ABCD\"",Boolean.FALSE);
        exprEquals("\"ABCD\"!=\"ABCDE\"",Boolean.TRUE);
        evalExceptionTest("\"ABCD\"!=123");
        evalExceptionTest("123!=\"ABCD\"");
        exprEquals("123!=123",Boolean.FALSE);
        exprEquals("123!=423",Boolean.TRUE);

        exprEquals("\"ABCD\"+\"ABCD\"","ABCDABCD");
        evalExceptionTest("\"ABCD\"+123");
        evalExceptionTest("123+\"ABCD\"");
        
        exprEquals("\"ABC\" < \"DEF\"",Boolean.TRUE);
        exprEquals("\"ABC\" <= \"DEF\"",Boolean.TRUE);
        exprEquals("\"ABC\" > \"DEF\"",Boolean.FALSE);
        exprEquals("\"ABC\" >= \"DEF\"",Boolean.FALSE);
        evalExceptionTest("123<\"ABCD\"");
        evalExceptionTest("\"ABCD\"<123");
        evalExceptionTest("false<\"ABCD\"");
        evalExceptionTest("\"ABCD\"<false");
    }
    

}
