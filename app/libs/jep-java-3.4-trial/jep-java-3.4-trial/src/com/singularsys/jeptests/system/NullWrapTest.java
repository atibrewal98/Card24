/*
Created 10 Jul 2009 - Richard Morris
*/
package com.singularsys.jeptests.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.OperatorTable2;
import com.singularsys.jep.Variable;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.singularsys.jep.misc.nullwrapper.NullWrapperFunctionTable;
import com.singularsys.jep.misc.nullwrapper.NullWrappingOperatorTable;
import com.singularsys.jep.parser.Node;
import com.singularsys.jep.standard.FastEvaluator;

public class NullWrapTest {
    public static final boolean PRINT_RESULTS = true;
    protected Jep jep;

    /**
     * Sets up the parser.
     */
    @Before
    public void setUp() {
        System.out.println("setUp");
        // Set up the parser
        jep = new Jep();
        jep.setImplicitMul(true);
        //jep.addStandardFunctions();
        jep.addStandardConstants();
        //jep.addComplex();
        //jep.setTraverse(false);
        jep.setComponent(new NullWrappingOperatorTable((OperatorTable2) jep.getOperatorTable(),true));
        jep.setComponent(new NullWrapperFunctionTable(jep.getFunctionTable()));
        jep.setComponent(new StandardConfigurableParser());
        //Evaluator eval = jep.getEvaluator();
        //Method meth = eval.getClass().getDeclaredMethod("setTrapNullValues", Boolean.TYPE);
        //meth.invoke(eval, false);
        ((FastEvaluator) jep.getEvaluator()).setTrapNullValues(false);
    }

    protected void myAssertNull(String msg,Object actual)
    {
        if(actual == null) {
            System.out.println("Success: Value of \""+msg+"\" is "+actual+"");
        }
        else {
            System.out.println("Error: '"+msg+"' is '"+actual+"' should be 'Null'");
            fail("<"+msg+"> is "+actual+" should be null");
        }
    }

    protected void myAssertEquals(String msg, Object expected, Object actual) {
        if(PRINT_RESULTS && !expected.equals(actual))
            System.out.println("Error: '"+msg+"' is '"+actual+"' should be '"+expected+"'");
        assertEquals("<"+msg+">",expected,actual);
        if(PRINT_RESULTS)
            System.out.println("Success: value of \""+msg+"\" is "+actual+"");
    }

    @Test 
    public void testNullWrap() throws Exception
    {
        System.out.println("testNullWrap");
        
        Variable var = jep.addVariable("null",null);
        var.setValidValue(true);
        var.setIsConstant(true);
        
        String eqn = "null * 2";
        Node n = jep.parse(eqn);
        Object val = jep.evaluate(n);
        myAssertNull(eqn,val);
        
        eqn = "2 * 2";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertEquals(eqn,4.0,val);

        eqn = "2 * null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "null * null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "2 + 2";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertEquals(eqn,4.0,val);

        eqn = "null + 2";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "2 + null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "null + null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "x = null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);
        val = jep.getVariableValue("x");
        myAssertNull(eqn,val);
        
        eqn = "sin(null)";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "sin(0.0)";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertEquals(eqn,val, 0.0);

        eqn = "atan2(null,1.0)";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "atan2(1.0,null)";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);
        
        eqn = "if(null,1.0,2.0)";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);


    }

    @Test
    public void testNullSafeEquals() throws JepException
    {
        String eqn = "null == null";
        Node n = jep.parse(eqn);
        Object val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "null == 5";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "5 == null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        myAssertNull(eqn,val);

        eqn = "5 == 5";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.TRUE, val);

        eqn = "5 == 6";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.FALSE, val);

        eqn = "null <=> null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.TRUE, val);

        eqn = "null <=> 5";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.FALSE, val);

        eqn = "5 <=> null";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.FALSE, val);

        eqn = "5 <=> 5";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.TRUE, val);

        eqn = "5 <=> 6";
        n = jep.parse(eqn);
        val = jep.evaluate(n);
        this.myAssertEquals(eqn, Boolean.FALSE, val);

    }
}
