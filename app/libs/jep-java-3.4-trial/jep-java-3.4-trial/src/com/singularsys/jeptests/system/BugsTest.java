package com.singularsys.jeptests.system;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.singularsys.jep.EmptyOperatorTable;
import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.FunctionTable;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.OperatorTable2;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.PostfixMathCommandI;
import com.singularsys.jep.VariableTable;
//import com.singularsys.jep.OperatorTable2.OperatorKey;
import com.singularsys.jep.EmptyOperatorTable.OperatorKey;
import com.singularsys.jep.configurableparser.ConfigurableParser;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;
import com.singularsys.jep.configurableparser.TernaryOperator;
import com.singularsys.jep.configurableparser.matchers.IdentifierTokenMatcher;
import com.singularsys.jep.functions.BinaryFunction;
import com.singularsys.jep.functions.Logical;
import com.singularsys.jep.functions.PostfixMathCommand;
import com.singularsys.jep.functions.UnaryFunction;
import com.singularsys.jep.functions.VSum;
import com.singularsys.jep.misc.CaseInsensitiveFunctionTable;
import com.singularsys.jep.misc.functions.IsNull;
import com.singularsys.jep.misc.functions.Switch;
import com.singularsys.jep.misc.functions.SwitchDefault;
import com.singularsys.jep.misc.functions.ToBase;
import com.singularsys.jep.misc.javaops.TernaryConditional;
import com.singularsys.jep.parser.ASTConstant;
import com.singularsys.jep.parser.Node;
import com.singularsys.jep.parser.Node.HookKey;
import com.singularsys.jep.standard.Complex;
import com.singularsys.jep.standard.FastEvaluator;
import com.singularsys.jep.walkers.SubstitutionVisitor;
import com.singularsys.jep.Operator;

/**
 * This class is intended to contain all tests related to reported bugs.
 * 
 * @author Nathan Funk
 */
public class BugsTest {
    private Jep jep;


    @Before
    public void setUp() {
        // Set up the parser
        jep = new Jep();
        jep.setImplicitMul(true);
    }

    /**
     * Tests a bug that lead the FractalCanvas example to fail.
     * (09/04/2007)
     */
    @Test
    public void testFractalBug()  throws Exception {
        System.out.println("Testing FractalCanvas bug...");
        Jep jep;
        Complex c;

        //Init Parser
        jep = new Jep();

        //Add and initialize x to (0,0)
        jep.addVariable("x", new Complex(0, 0));

        //Parse the new expression
        try {
            jep.parse("x");
        } catch (ParseException e) {
            fail("Error while parsing: "+e.getMessage());
        }
        //Reset the values
        jep.addVariable("x", new Complex(1, 1));
        //z.set(0,0);
        //System.out.println("x= " + jep.getVarValue("x"));

        Object value;

        try {
            value = jep.evaluate();
        } catch (EvaluationException e) {
            fail("Error during evaluation: "+e.getMessage());
            return;
        }

        System.out.println("result = " + value);
        assertTrue(value instanceof Complex);
        c = (Complex)value;
        assertTrue(c.re() == 1);
        assertTrue(c.im() == 1);
    }


    /**
     * Tests bug [ 1585128 ] setAllowUndeclared does not work!!!
     * setAllowedUndeclared should add variables to the symbol table.
     * 
     * This test parses the expression "x" and checks whether only the
     * variable x is in the symbol table (no more no less)
     */
    @Test
    public void testSetAllowUndeclared() {
        System.out.println("Testing setAllowUndeclared...");
        jep.getVariableTable().clear();				// clear the variable table
        jep.setAllowUndeclared(true);
        try {
            jep.parse("x");
        } catch (ParseException e) {
            fail();
        }
        VariableTable vt = jep.getVariableTable();

        // should only contain a single variable x
        assertTrue(vt.size()==1);
        assertTrue(vt.getVariable("x") != null);
    }

    /**
     * Tests [ 1589277 ] Power function and "third root".
     * 
     * Simple test for (-8)^(1/3) == -2.
     *
	public void testComplexPower() {
		jep.initSymTab();
		jep.parseExpression("(-8)^(1/3)");
		Complex result = jep.getComplexValue();
		assertTrue(result.equals(new Complex(-2, 0)));
	}*/

    /**
     * Tests [ 1563324 ] getValueAsObject always return null after an error
     * 
     * Jep 2.4.0 checks the <code>errorList</code> variable before evaluating 
     * an expression if there is an error in the list, null is returned. This
     * behaviour is bad because errors are added to the list by
     * getValueAsObject. If the first evaluation fails (after a successful parse)
     * then an error is added to the list. Subsequent calls to getValueAsObject
     * fail because there is an error in the list.
     */
    //	@Test
    //	public void testBug1563324() {
    //		jep.initSymTab();
    //		jep.setAllowUndeclared(true);
    //		// parse a valid expression
    //		jep.parseExpression("abs(x)");
    //		// add a variable with a value that causes evaluation to fail
    //		// (the Random type is not supported by the abs function)
    //		jep.addVariable("x", new java.util.Random()); 
    //		Object result = jep.getValueAsObject();
    //		// evaluation should have failed
    //		assertTrue(jep.hasError());
    //		
    //		// change the variable value to a value that should be evaluated
    //		jep.addVariable("x", -1);
    //		// ensure that it is evaluated correctly
    //		result = jep.getValueAsObject();
    //		assertTrue((result instanceof Double) && ((Double)result).doubleValue() == 1.0);
    //	}

    /**
     * Tests bug 49. Adding an operator such as "AND" does not work. Instead
     * of being interpreted as and operator it is parsed as a variable.
     */
    @Test
    public void testBug49() {
        System.out.println("Testing bug 49...");
        //set configurable parser
        ConfigurableParser cp = new StandardConfigurableParser();
        Jep j = new Jep(cp);

        // alter operator table
        OperatorTable2 ot = (OperatorTable2) j.getOperatorTable();
        Operator andOp = new Operator("AND", new Logical(0), Operator.BINARY+Operator.LEFT+Operator.ASSOCIATIVE);
        ot.replaceOperator(ot.getAnd(),andOp);
        j.reinitializeComponents();

        try {
            // parse a simple expression
            j.parse("1 AND 1");
            Node n = j.getLastRootNode();
            System.out.println(n.getClass().toString());

            // should be a single operator node with two children
            JepTest.nodeTest(n, andOp);
            assertEquals(2, n.jjtGetNumChildren());
            // children should be constants with no children
            JepTest.nodeTest(n.jjtGetChild(0), new Double(1));
            JepTest.nodeTest(n.jjtGetChild(1), new Double(1));

            // try evaluating the expression
            Object result = j.evaluate();
            assertTrue(result instanceof Boolean);
        } catch (Exception e) {
            // some other exception was thrown
            System.out.println(e.getMessage());
            //e.printStackTrace();
            fail();
        }
    }

    /**
     * Test bug #72 Handling of Constants. Sets a constant and then tries to
     * set the value again. The bug was that addVariable failed silently, and
     * the fix was to add exception throwing to addVariable.
     */
    @Test
    public void testBug72() {
        System.out.println("Testing bug 72 Handling of Constants...");
        Jep j = new Jep();

        // try adding a constant
        try {
            j.addConstant("x", 1.0);
        } catch (JepException e) {
            fail("addConstant failed though it shouldn't.");
        }

        try {
            j.addVariable("x", 1.0); //should fail
            fail("addVariable call didn't throw an exception as expected.");
        } catch (JepException e) {
            // this is ok, since we want an exception to be thrown in this case
        }
    }

    /**
     * Fails as the minus sign is actually  unicode U+2013
     * TODO maybe should allow alternative symbols
     * @throws Exception
     */
    @Test
    public void test2008_08_13() throws Exception {
        jep.setComponent(new StandardConfigurableParser());
        jep.getOperatorTable().getSubtract().addAltSymbol("\u2013");
        jep.getOperatorTable().getUMinus().addAltSymbol("\u2013");
        jep.getOperatorTable().getSubtract().addAltSymbol("\u2212");
        jep.getOperatorTable().getUMinus().addAltSymbol("\u2212");
        jep.reinitializeComponents();
        //	   String formule = "S = if(C == 1, (1 + a_n_e) * (1 + mi) * (1 + p_f_c) – 1), if(C == 2, a_n_e + m + p_f_c, if(C == 3, p_b + mi + p_f_c, 0)))";
        String formule = "S = \u2212 1";
        //	   String substr = formule.substring(50);
        //	   System.out.println(substr);
       jep.parse(formule);
//            jep.addVariable("S_a_e", 0);
//          jep.addVariable("C", 1);
//            jep.addVariable("a_n_e", 1);
//            jep.addVariable("mi", 1);
//            jep.addVariable("p_f_c", 1);
       Object result = jep.evaluate();
       System.out.println("S_a_e = " + result);
    }

    /*  This test was based on a user forum question. The expression supplied is
     *  invalid due to 0,5 not having a "." instead of a "," and some if statements
     *  only having two arguments. This test can likely be removed.
	@Test
	public void test2008_08_14() throws Exception {
	    jep = new Jep(new StandardConfigurableParser());
	String eqn = "A_N_E =\n"+
	"	if (CT == 1,\n"+
	"		(L_c / L_u) -1,\n"+
	"	if (CT == 2,\n"+
	"		if (TP == 1,\n"+
	"			0.46,\n"+
	"		if (TP == 2,\n"+
	"			(84/(L * PE(81/(L + 0.5)))) -1,\n"+
	"		if (TP == 3,\n"+
	"			(81/(L * PE(78/(L + 0.5)))) -1,\n"+
	"	if (CT == 3,\n"+
	"		if (TP == 1 || 2,\n"+
	"			0.46,\n"+
	"		if (TP == 3,\n"+
	"			1- L_c/ (PE(L_B/(L + 0,5)) * L ),\n"+
	"	0))))))))";

	jep.parse(eqn);
	}
     */

    @Test
    public void test2008_08_14_switch() throws Exception {
        jep = new Jep(new StandardConfigurableParser());
        jep.addFunction("switchd",new SwitchDefault());
        jep.addFunction("switch",new Switch());
        String eqn = 
            "A_N_E =\n"+
            " switchd(CT,\n"+
            "  (L_c / L_u) -1,\n"+
            "  switch(TP,\n"+
            "   0.46,\n"+
            "   (84/(L * PE(81/(L + 0.5)))) -1,\n"+
            "	(81/(L * PE(78/(L + 0.5)))) -1\n"+
            "  ),\n"+
            "  switch(TP,\n"+
            "   0.46,\n"+
            "   0.46,\n"+
            "   1- L_c/ (PE(L_B/(L + 0.5)) * L )\n"+
            "  ),\n"+
            "  0)";

        jep.parse(eqn);
    }

    @Test
    public void testToBase() throws Exception {
        jep.addFunction("toBin", new ToBase(2));
        jep.addVariable("x",Integer.valueOf(-9));
        String eqn = "toBin(x)";
        String res = (String) jep.evaluate(jep.parse(eqn));
        assertEquals(eqn,"-1001",res);
    }

    @Test
    public void testBug2008_09_12() throws ParseException {
        jep.setAllowUndeclared(false);
        try {
            jep.parse( "v0 / 1000000000" );
            fail("Should have a parse exception with undeclared variable");
        } catch (ParseException e) {
            System.out.println("expected exception caught "+e.getMessage());
        }
    }

    @Test
    public void testBug84() throws Exception {
        String eqn = "if(stringVariable==\"someString\",1.0,0.0)";
        Node testNode = jep.parse(eqn); 
        String out = jep.toString(testNode);
        assertEquals(eqn,out);
    }

    @Test
    public void testBug28() throws Exception {
        String eqn = "x+y";
        String rep = "z^2";
        Node n1 = jep.parse(eqn);
        Node n2 = jep.parse(rep);
        SubstitutionVisitor sv = new SubstitutionVisitor(jep);
        Node n3 = sv.substitute(n1,"x",n2);
        String res = jep.toString(n3);
        assertEquals("z^2.0+y",res);
    }
    
    @Test
    public void test2008_12_13() throws Exception {
        Jep jep = new Jep();
        HookKey key1 = new HookKey() {};
        HookKey key2 = new HookKey() {};
        ASTConstant astConstant = jep.getNodeFactory().buildConstantNode("value");
        astConstant.setHook(key1, "value1");

        //The call to setHook second time throws ArrayIndexOutOfBoundsException
        //NOTE: IBJepHooks implements HookKey
        astConstant.setHook(key2, "value2");
        
        assertEquals("value1",astConstant.getHook(key1));
        assertEquals("value2",astConstant.getHook(key2));
    }

    @Test
    public void test2009_02_02() throws Exception {
        jep.addFunction("contains",new BinaryFunction() {

            @Override
            public Object eval(Object l, Object r) throws EvaluationException {
                String ls = (String) l;
                String rs = (String) r;
                System.out.println(ls);
                System.out.println(rs);
                return ls.contains(rs);
            }} );
        jep.setComponent(new StandardConfigurableParser());
        String s = "contains (\"AB01,AB02,AB03\", \"AB02,AB03\")";
       Node n = jep.parse(s);
       Object res = jep.evaluate(n);
       System.out.println(res);
    }
    
    @Test
    public void test2009_02_16() throws Exception {
        jep.setComponent(new StandardConfigurableParser());
        OperatorTable2 ot = ((OperatorTable2)jep.getOperatorTable());
        TernaryOperator op = new TernaryOperator("cond", "?", ":", 
                new TernaryConditional(), 
                Operator.TERNARY+Operator.NARY+Operator.LEFT);
        ot.insertOperator(new OperatorKey(){}, 
                op,ot.getAssign());
    }

    @Test
    public void testBug116() throws Exception {
        Jep jep = new Jep();
        jep.setComponent(new FastEvaluator());
        //jep.setComponent(new StandardEvaluator());
        String expr = "[[1],[2]]";
        jep.parse(expr);
        try
        {
            jep.evaluate();
        }
        catch (Exception e)
        {
            Assert.fail("Evaluation of \""+expr+"\" failed.");
        }
    }
    
    static class OperatorUMinus extends PostfixMathCommand {
        public void run(Stack<Object> aStack) throws EvaluationException {
         }
    }
    static class OperatorAdd extends PostfixMathCommand {
        public void run(Stack<Object> aStack) throws EvaluationException {
         }
    }
    static class OperatorSubtract extends PostfixMathCommand {
        public void run(Stack<Object> aStack) throws EvaluationException {
         }
    }
    static class OperatorMultiply extends PostfixMathCommand {
        public void run(Stack<Object> aStack) throws EvaluationException {
         }
    }
    static class OperatorDivide extends PostfixMathCommand {
        public void run(Stack<Object> aStack) throws EvaluationException {
         }
    }

    /*
     * Code which uses the old OperatorTable which will always break. see #121 
    static class JepExpressionOperatorTable extends OperatorTable
    {
        private static final long serialVersionUID = 1L;

        JepExpressionOperatorTable()
        {
            setNumOps(5);
            
            addOperator(OP_NEGATE,new Operator("UMinus","-",new OperatorUMinus(),Operator.UNARY+Operator.RIGHT+Operator.PREFIX+Operator.SELF_INVERSE));
            addOperator(OP_ADD,new Operator("+",new OperatorAdd(),Operator.BINARY+Operator.LEFT+Operator.COMMUTATIVE+Operator.ASSOCIATIVE));
            addOperator(OP_SUBTRACT,new Operator("-",new OperatorSubtract(),Operator.BINARY+Operator.LEFT+Operator.COMPOSITE+Operator.USE_BINDING_FOR_PRINT));
            addOperator(OP_MULTIPLY,new Operator("*",new OperatorMultiply(),Operator.BINARY+Operator.LEFT+Operator.COMMUTATIVE+Operator.ASSOCIATIVE));
            addOperator(OP_DIVIDE,new Operator("/",new OperatorDivide(),Operator.BINARY+Operator.LEFT+Operator.COMPOSITE));

            setPrecedenceTable(new int[][] 
                                         {   
                                             {OP_POWER},
                                             {OP_NEGATE,OP_UPLUS,OP_NOT},
                                             {OP_MULTIPLY,OP_DIVIDE,OP_MOD,OP_DOT,OP_CROSS},
                                             {OP_ADD,OP_SUBTRACT},
                                             {OP_LT,OP_LE,OP_GT,OP_GE},
                                             {OP_EQ,OP_NE},
                                             {OP_AND},
                                             {OP_OR},
                                             {OP_ASSIGN},
                                         });
                                 
            this.setStandardOperatorRelations();
        }
    }
    
    @Test
    public void testBug121() throws Exception {
        Jep jep = new Jep();
        jep.setComponent(new JepExpressionOperatorTable());
        //jep.setComponent(new StandardEvaluator());
    }
*/
    static class JepExpressionOperatorTable2 extends EmptyOperatorTable
    {
        private static final long serialVersionUID = 1L;

        JepExpressionOperatorTable2()
        {
           Set<Entry<OperatorKey, Operator>> es = this.entrySet();
           es.clear();
           
            addOperator(OperatorTable2.BasicOperators.NEG,new Operator("UMinus","-",new OperatorUMinus(),Operator.UNARY+Operator.RIGHT+Operator.PREFIX+Operator.SELF_INVERSE));
            addOperator(OperatorTable2.BasicOperators.ADD,new Operator("+",new OperatorAdd(),Operator.BINARY+Operator.LEFT+Operator.COMMUTATIVE+Operator.ASSOCIATIVE));
            addOperator(OperatorTable2.BasicOperators.SUB,new Operator("-",new OperatorSubtract(),Operator.BINARY+Operator.LEFT+Operator.COMPOSITE+Operator.USE_BINDING_FOR_PRINT));
            addOperator(OperatorTable2.BasicOperators.MUL,new Operator("*",new OperatorMultiply(),Operator.BINARY+Operator.LEFT+Operator.COMMUTATIVE+Operator.ASSOCIATIVE));
            addOperator(OperatorTable2.BasicOperators.DIV,new Operator("/",new OperatorDivide(),Operator.BINARY+Operator.LEFT+Operator.COMPOSITE));

            setPrecedenceTable(new OperatorKey[][] 
                                         {   
                                             {OperatorTable2.BasicOperators.NEG},
                                             {OperatorTable2.BasicOperators.MUL,OperatorTable2.BasicOperators.DIV},
                                             {OperatorTable2.BasicOperators.ADD,OperatorTable2.BasicOperators.SUB},
                                         });
                                 
            this.setStandardOperatorRelations();
        }
    }
    
    /**
     * Tests a custom operator table with only 5 operators, see bug #121
     * @throws Exception
     */
    @Test
    public void testBug121A() throws Exception {
        Jep jep = new Jep();
        jep.setComponent(new JepExpressionOperatorTable2());
        //jep.setComponent(new StandardEvaluator());
    }
    
    class myRound extends UnaryFunction
    {

        @Override
        public Object eval(Object arg) throws EvaluationException {
            return Double.valueOf(Math.round(((Double)arg).doubleValue()));
        }
        
    }
    
    @Test
    public void testBug122() throws Exception {
    // This breaks on the parse() call
    jep = new Jep();
    FunctionTable oldFT = jep.getFunctionTable();
    jep.setComponent(new CaseInsensitiveFunctionTable());
    for(Entry<String, PostfixMathCommandI> ent:oldFT.entrySet()) {
        jep.addFunction(ent.getKey(), ent.getValue());
    }
    //jep.addFunction("if",new If());
    jep.parse("if(1>0,2,3)");
    System.out.println(jep.evaluate());
    jep.parse("If(1>0,2,3)");
    System.out.println(jep.evaluate());
    }

    @Test
    public void testBug123() throws Exception {
        jep.addFunction("round", new myRound());
        Double res = (Double) jep.evaluate(jep.parse("round(1.5)"));
        assertEquals("round(1.5) = "+res,2.0d,res.doubleValue(),0.1);
        
        res = (Double) jep.evaluate(jep.parse("round(2.5)"));
        assertEquals("round(2.5) = "+res,3.0d,res.doubleValue(),0.1);
    
        System.out.println(java.lang.Math.round(1.5));
        System.out.println(java.lang.Math.round(2.5));
        System.out.println(java.lang.Math.rint(1.5));
        System.out.println(java.lang.Math.rint(2.5));
    }

    @Test
    public void testBug30_10_09() throws Exception {
        {
    jep.addFunction("isNull", new IsNull());
    ((FastEvaluator) jep.getEvaluator()).setTrapNullValues(false);
    Double y = new Double(5);           

    jep.addVariable("x",null);
    jep.addVariable("y", y);
    jep.addVariable("z", 50000);

    Node node = jep.parse("isNull(x)");
    System.out.println(jep.evaluate(node));
    node = jep.parse("isNull(y)");
    System.out.println(jep.evaluate(node));
    node = jep.parse("isNull(w)");
    System.out.println(jep.evaluate(node));
        }
    }
    
    @Test
    public void testBug23_11_09() {

        String f1 = "min(1)";
        String f2 = "min(2)";
        String f3 = "min(1,2)";
        String f4 = "max(min(1),min(2))";
        String f5 = "min(min(1),min(2))";

        testMU(f1);
        testMU(f2);
        testMU(f3);
        testMU(f4);
        testMU(f5);
    }
    
    private static void testMU(String formula) {

        try {

                Jep jep = new Jep();
                jep.parse(formula);
                Double d = (Double) jep.evaluate();
                System.out.println(d);
        } catch (Exception e) {

                System.out.println(e);
        }
    }
    
    @Test
    public void testDottedIdentifiers() throws ParseException {
        ConfigurableParser cp = new ConfigurableParser();
        cp.addHashComments();
        cp.addSlashComments();
        cp.addDoubleQuoteStrings();
        cp.addWhiteSpace();
        cp.addExponentNumbers();
        cp.addOperatorTokenMatcher();
        cp.addSymbols("(",")","[","]",",");
        cp.setImplicitMultiplicationSymbols("(","[");

        // Sets it up for identifiers with dots in them.
        cp.addTokenMatcher(IdentifierTokenMatcher.dottedIndetifierMatcher());

        cp.addSemiColonTerminator();
        cp.addWhiteSpaceCommentFilter();
        cp.addBracketMatcher("(",")");
        cp.addFunctionMatcher("(",")",",");
        cp.addListMatcher("[","]",",");
        cp.addArrayAccessMatcher("[","]");

        // Construct the Jep instance and set the parser
        jep = new Jep(cp);
        
        // Remove the dot operator
        ((OperatorTable2) jep.getOperatorTable()).removeOperator(jep.getOperatorTable().getDot());
        //notify other components of change in operator table
        jep.reinitializeComponents();
        
        Node n = jep.parse("a.b=c.d");
        Node n2 = n.jjtGetChild(0);
        assertEquals("a.b",n2.getName());
        
    }
    
    @Test
    public void test31Jan2010() throws JepException {
        Jep jep = new Jep();
        jep.addFunction("vsum",new VSum());
        String formula = "vsum(x)"; 
        Node node = jep.parse(formula); 
        SubstitutionVisitor sv = new SubstitutionVisitor(jep); 
        Node node1 = jep.parse("x = [1, 2]"); 
        Node substitute = sv.substitute(node, node1); 
        Object result = jep.evaluate(substitute); 
        System.out.println(result); 
        
        jep.addVariable("x", new Vector<Object>(Arrays.asList(3.0,2.0,4.0)));
        result = jep.evaluate(node); 
        System.out.println(result); 
    }
}
