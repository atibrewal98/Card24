package com.singularsys.jeptests.system;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.ConfigurableParser;
import com.singularsys.jep.configurableparser.matchers.UpperCaseOperatorTokenMatcher;
import com.singularsys.jep.configurableparser.matchers.HexNumberTokenMatcher;
import com.singularsys.jep.configurableparser.tokens.Token;
import com.singularsys.jep.misc.javaops.JavaOperatorTable;
import com.singularsys.jep.parser.Node;

/**
 * Tests for the configurable parser.
 */
public class ExtendedParserTest extends JepTest {


    @Override
    @Before
    public void setUp() {
        this.jep = new Jep();
        JavaOperatorTable jot = new JavaOperatorTable("^","^^^");
        jot.getAnd().setSymbol("AND");
        jot.getOr().setSymbol("OR");
        jot.getNot().setSymbol("NOT");
        jep.setComponent(jot);
        jep.addFunction("ORGINIZE",null);
        ConfigurableParser cp = new ConfigurableParser();
        cp.addHashComments();
        cp.addSlashComments();
        cp.addDoubleQuoteStrings();
        cp.addWhiteSpace();
        cp.addTokenMatcher(new HexNumberTokenMatcher());
        cp.addExponentNumbers();
        cp.addTokenMatcher(new UpperCaseOperatorTokenMatcher());
        cp.addSymbols("(",")","[","]",",","IF","THEN","ELSE");
        cp.setImplicitMultiplicationSymbols("(","[");
        cp.addIdentifiers();
        cp.addSemiColonTerminator();
        cp.addWhiteSpaceCommentFilter();
        cp.addBracketMatcher("(",")");
        cp.addFunctionMatcher("(",")",",");
        cp.addListMatcher("[","]",",");
        cp.addArrayAccessMatcher("[","]");
        /*cp.addGrammerMatcher(new IfThenElseGrammerMatcher(
			cp.getSymbolToken("IF"),
			cp.getSymbolToken("THEN"),
			cp.getSymbolToken("ELSE"),
			new If()));*/
        jep.setComponent(cp);
    }

    @Test
    public void testBitwise() throws Exception {
        printTestHeader("Testing Bitwise operations");
        this.valueTestString("0x09","9");
        this.valueTestString("0x0a","10");
        this.valueTestString("0x0f","15");
        this.valueTestString("0x10","16");
        this.valueTestString("0x11","17");

        this.valueTestString("0x18 | 0x09","25");
        this.valueTestString("0x18 & 0x09","8");
        this.valueTestString("0x18 ^^^ 0x09","17");

        this.valueTestString("0x05 << 1","10");
        this.valueTestString("0x05 << 3","40");
        this.valueTestString("-0x05 << 2","-20");

        this.valueTestString("0x05 >> 1","2");
        this.valueTestString("0x05 >>> 1","2");

        this.valueTestString("-50 >> 2","-13");
        this.valueTestString("-50 >>> 2","1073741811");

    }

    @Test
    public void testTernary() throws Exception {
        printTestHeader("Testing Ternary operations");

        this.valueTestString("1==1?10:12","10.0");
        this.valueTestString("1==2?10:12","12.0");
        this.valueTestString("(1==0)?2+3:4+5","9.0");
        this.valueTestString("x=3","3.0");
        this.valueTestString("x<8?x<4?1:2:x<12?3:4","1.0");
        this.valueTestString("x=5","5.0");
        this.valueTestString("x<8?x<4?1:2:x<12?3:4","2.0");
        this.valueTestString("x=9","9.0");
        this.valueTestString("x<8?x<4?1:2:x<12?3:4","3.0");
        this.valueTestString("x=13","13.0");
        this.valueTestString("x<8?x<4?1:2:x<12?3:4","4.0");
        this.valueTestString("y=x<0?-x:x","13.0");
        this.valueTestString("y","13.0");
    }

    @Override
    @Test
    public void testComplex() throws Exception {
    }

    @Override
    @Test
    public void testFunction() throws Exception {
    }

    @Override
    @Test
    public void testPlusPlus() throws Exception {
    }

    @Test
    public void testIncrement() throws Exception {
        printTestHeader("Testing Increment and decrement operations");
        this.valueTestString("x=3","3.0");
        this.valueTestString("x++","3.0");
        this.valueTestString("x","4.0");
        this.valueTestString("++x","5.0");
        this.valueTestString("x","5.0");

        this.valueTestString("x--","5.0");
        this.valueTestString("x","4.0");
        this.valueTestString("--x","3.0");
        this.valueTestString("x","3.0");

    }

    @Test
    public void testOpEquals() throws Exception {
        printTestHeader("Testing += etc");
        this.valueTestString("x=3","3.0");
        this.valueTestString("y=4","4.0");
        this.valueTestString("y+=x","7.0");
        this.valueTestString("y","7.0");
        this.valueTestString("y-=x","4.0");
        this.valueTestString("y*=x","12.0");
        this.valueTestString("y/=x","4.0");
        this.valueTestString("y%=x","1.0");
        this.valueTestString("a=0x18","24");
        this.valueTestString("b=0x09","9");
        this.valueTestString("a|=b","25");
        this.valueTestString("a=0x18","24");
        this.valueTestString("a&=b","8");
        this.valueTestString("a=0x18","24");
        this.valueTestString("a^=b","17");

        this.valueTestString("a=0x05","5");
        this.valueTestString("a<<=1","10");
        this.valueTestString("a=0x05","5");
        this.valueTestString("a<<=3","40");
        this.valueTestString("a=-0x05","-5.0");
        this.valueTestString("a<<=2","-20");

        this.valueTestString("a=0x05","5");
        this.valueTestString("a>>=1","2");
        this.valueTestString("a=0x05","5");
        this.valueTestString("a>>>=1","2");

        this.valueTestString("a=-50","-50.0");
        this.valueTestString("a>>=2","-13");
        this.valueTestString("a=-50","-50.0");
        this.valueTestString("a>>>=2","1073741811");

        this.valueTestString("x=3","3.0");
        this.valueTestString("y=4","4.0");
        this.valueTestString("z=5","5.0");
        this.valueTestString("z-=y+=x","-2.0");
        this.valueTestString("x","3.0");
        this.valueTestString("y","7.0");
        this.valueTestString("z","-2.0");

    }

    @Override
    @Test
    public void testLogical() throws Exception {
    }

    @Override
    @Test
    public void testLazyLogical() throws Exception {
    }

    @Override
    @Test
    public void testNumParam() throws Exception {
    }

    @Override
    @Test
    public void testX2Y() throws Exception {
    }

    @Override
    @Test
    public void testStrings() throws Exception {
    }

    @Test
    public void testUpperCaseOperator() throws Exception
    {
        valueTest("T=1",1.0);
        valueTest("F=0",0.0);
        valueTest("NOT T",myFalse);
        valueTest("NOT F",myTrue);
        valueTest("NOT 5",myFalse);
        valueTest("-0==0",myTrue);
        valueTest("NOT -5",myFalse);
        //valueTest("-!5==0",myTrue);
        //valueTest("-!0",-1.0);
        valueTest("T AND T",myTrue);
        valueTest("T AND F",myFalse);
        valueTest("F AND T",myFalse);
        valueTest("F AND F",myFalse);
        valueTest("T OR T",myTrue);
        valueTest("T OR F",myTrue);
        valueTest("F OR T",myTrue);
        valueTest("F OR F",myFalse);

        valueTest("T and T",myTrue);
        valueTest("T And F",myFalse);
        valueTest("F And T",myFalse);
        valueTest("F and F",myFalse);
        valueTest("T or T",myTrue);
        valueTest("T Or F",myTrue);
        valueTest("F oR T",myTrue);
        valueTest("F OR F",myFalse);
    }
    @Override
    @Test
    public void testParseException() {
    }

    /*public void testIfThenElse() throws Exception {
	    valueTest("x=5",5.0);
	    Node n = jep.parse("IF x==5 THEN y=6 ELSE y=7");
	    Object res = jep.evaluate(n);
	}*/

    @Test
    public void testOverlappingNames() throws Exception {
        ConfigurableParser cp = (ConfigurableParser) jep.getParser();
        List<Token> toks = cp.scan(new StringReader("A ORGINIZE()"));
        Token t = toks.get(2);
        assertEquals("ORGINIZE",t.getSource());
    }

    @Test
    public void test2010_03_01() throws Exception {
        //ConfigurableParser cp = (ConfigurableParser) jep.getParser();
        Node n = jep.parse("OR_4=\"KO\"");
        assertEquals("OR_4=\"KO\"","OR_4",n.jjtGetChild(0).getName());
        n = jep.parse("OR5=\"KO\"");
        assertEquals("OR5=\"KO\"","OR5",n.jjtGetChild(0).getName());
    }

    
    @Override
    @Test
    public void testLazyLogicalBug() throws Exception {
        valueTest("true AND 1",myTrue);
    }


}
