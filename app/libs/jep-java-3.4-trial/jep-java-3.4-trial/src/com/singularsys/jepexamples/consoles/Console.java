
package com.singularsys.jepexamples.consoles;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import com.singularsys.jep.FunctionTable;
import com.singularsys.jep.Jep;
import com.singularsys.jep.Operator;
import com.singularsys.jep.JepException;
import com.singularsys.jep.OperatorTableI;
import com.singularsys.jep.Variable;
import com.singularsys.jep.VariableTable;
import com.singularsys.jep.parser.Node;

/**
 * This class implements a simple command line utility for evaluating
 * mathematical expressions.
 * <pre>
 *   Usage: java com.singularsys.jepexamples.consoles.Console [expression]
 * </pre>
 * If an argument is passed, it is interpreted as an expression
 * and evaluated. Otherwise, a prompt is printed, and the user can enter
 * expressions to be evaluated. 
 * 
 * <p>
 * This class and its subclasses can also be run as a java applet 
 * which displays a textarea for interactive input.
 * <p>
 * This class has been designed to be sub classed to allow different
 * consol applications.
 * The methods
 * <pre>
 * public void initialise()
 * public void processEquation(Node node) throws Exception
 * public boolean testSpecialCommands(String command)
 * public void printPrompt()
 * public void printIntroText()
 * public void printHelp()
 * </pre>
 * can all be overwritten.
 * </p>
 * <p>
 * Furthermore main should be overwritten. For example
 * <pre> 
 * 	public static void main(String args[]) {
 *		Console c = new DJepConsole();
 *		c.run(args);
 *	}
 *</pre>
 *<p>
 *The main input loop is approximately
 *<pre>
 * initialise();
 * printIntroText();
 * print(getPrompt());
 * String command;
 * while((command = getCommand()) != null) 
 * {
 *	if(command.equals("quit") || command.equals("exit"))
 *		break;
 *	if(!testSpecialCommands(command)) continue;
 *   try {
 *	  Node n = j.parse(command);
 *	  processEquation(n);
 *   } catch(Exception e) {}
 *	print(getPrompt());
 * }
 *</pre>
 */

public class Console extends Applet implements KeyListener {

    private static final long serialVersionUID = 9035584745289937584L;

    /** Main Jep object */
    protected Jep jep;	

    /** The input reader */
    private BufferedReader br;

    /** Text area for user input in applets. */
    protected TextArea ta = null;

    /** Constructor */
    public Console() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    /** Applet initialization */

    @Override
    public void init() 
    {
        initialise();
        this.setLayout(new BorderLayout(1,1));
        ta = new TextArea("",10,80,TextArea.SCROLLBARS_BOTH);
        ta.setEditable(true);
        ta.addKeyListener(this);
        add("Center",ta);
        printIntroText();
        print(getPrompt());
    }

    /** sets up all the needed objects. */
    public void initialise()
    {
        jep = new Jep();
        //		j.addStandardConstants();
        //		j.addStandardFunctions();
        //		j.addComplex();
        //		j.setAllowUndeclared(true);
        //		j.setAllowAssignment(true);
        //		j.setImplicitMul(true);
    }

    /** Creates a new Console object and calls run() */
    public static void main(String args[]) {
        Console c = new Console();
        c.run(args);
    }

    /** The main entry point with command line arguments 
     */
    public void run(String args[]) {
        initialise();

        if (args.length>0) {
            for (int i=1; i<args.length; i++)
            {
                processCommand(args[i]);
            }
        }
        else
            inputLoop();
    }

    /**
     * The main input loop for interactive operation.
     * Repeatedly calls getCommand() and processCommand().
     */
    public void inputLoop() {
        String command="";

        printIntroText();
        print(getPrompt());
        while((command = getCommand()) != null) 
        {
            if( !processCommand(command)) break;
            print(getPrompt());
        }
    }

    /** 
     * Process a single command.
     * <ol>
     * <li>Tests for exit, quit, and help.</li>
     * <li>Tests for any special commands used by sub classes.
     * {@link #testSpecialCommands(String)}</li>
     * <li>Parses the command.</li>
     * <li>Processes the node. {@link #processEquation(Node)}<li>
     * <li>Checks for errors. {@link #handleError(Exception)}</li>
     * </ol>
     * 
     * @param command The line to be processed
     * @return false if un-recoverable error or 'quit' or 'exit'
     */
    public boolean processCommand(String command) 
    {	
        switch(testSpecialCommands(command)) {
        case EXIT: return false;
        case BREAK: return true;
        case CONTINUE: break;
        }


        try {
            Node n = jep.parse(command);
            processEquation(n);
        }
        catch(Exception e) { return handleError(e); }

        return true;
    }



    /** Performs the required operation on a node. 
     * Typically evaluates the node and prints the value.
     * 
     * @param node Node representing expression
     * @throws JepException if a Parse or evaluation error
     */ 
    public void processEquation(Node node) throws JepException
    {
        Object res = jep.evaluate(node);
        println(res);
    }


    /**
     * Get a command from the input.
     * @return null if an IO error or EOF occurs.
     */
    protected String getCommand() {
        String s=null;

        if (br == null)	return null;

        try
        {
            if ( (s = br.readLine()) == null) return null;
        }
        catch(IOException e)
        {
            println("IOError exiting"); return null;
        }
        return s;
    }

    /** Prints the prompt string. */
    public String getPrompt() { return "Jep > "; }

    /** Prints a standard help message. 
     * Type 'quit' or 'exit' to quit, 'help' for help.
     **/
    public final void printStdHelp() {
        if(ta == null)
            println("Type 'help' for help and 'quit' or 'exit' to quit.");
        else 
            println("Type 'help' for help.");
    }		

    /** Print help message. */
    public void printHelp() { 
        printStdHelp();
        println("'functions' lists defined functions"); 
        println("'operators' lists defined operators"); 
        println("'variables' lists variables and constants"); 
    }

    /** Prints introductory text. */
    public void printIntroText() {
        println("Jep Console.");
        printStdHelp();
    }

    /** Prints a list of defined functions. */
    public void printFuns() {
        FunctionTable ft = jep.getFunctionTable();
        println("Known functions:");
        for(String key:ft.keySet())
        {
            println("\t"+key);
        }
    }

    /** Prints a list of defined operators. */
    public void printOps() {
        OperatorTableI opset = jep.getOperatorTable();
        println("Known operators:");
        for(Operator op:opset.getOperators())
            println("\t"+op);
    }

    /** Prints a list of variable. */
    public void printVars() {
        VariableTable st = jep.getVariableTable();
        println("Variables:");
        for(Variable var:st.getVariables())
        {
            println(var);
        }
    }
    public enum SPEC_ACTION {CONTINUE,BREAK,EXIT}
    /**
     * Checks for special commands.
     * For example a subclass may have a verbose mode
     * switched on of off using the command
     * <pre>verbose on</pre>
     * This method can be used detected this input, 
     * perform required actions and skip normal processing by returning true.
     * 
     * In general sub classes should call the superclass methods to test for special commands that class implements
     * @param command
     * @return SPEC_ACTION.CONTINUE - continue processing this equation, SPEC_ACTION.BREAK - stop processing this equation and get the next line of input, SPEC_ACTION.EXIT stop the program
     * @see #split(String)
     */
    public SPEC_ACTION testSpecialCommands(String command)	{ 
        if(Pattern.matches("^\\s*$",command)) return SPEC_ACTION.BREAK;
        if(command.equals("quit") || command.equals("exit"))
            return SPEC_ACTION.EXIT;

        if(command.equals("help"))	{
            printHelp();
            return SPEC_ACTION.BREAK;
        }

        if(command.equals("functions"))	{
            printFuns();
            return SPEC_ACTION.BREAK;
        }

        if(command.equals("operators"))	{
            printOps();
            return SPEC_ACTION.BREAK;
        }

        if(command.equals("variables"))	{
            printVars();
            return SPEC_ACTION.BREAK;
        }
        return SPEC_ACTION.CONTINUE; 
    }		

    /**
     * Handle an error in the parse and evaluate routines.
     * Default is to print the error message for JepExceptions and a stack trace for other exceptions
     * @param e
     * @return false if the error cannot be recovered and the program should exit
     */
    public boolean handleError(Exception e)
    {
        if(e instanceof JepException) { 
            println(e.toString()); }
        else
            e.printStackTrace();

        return true;
    }

    /** Splits a string on spaces.
     * 
     * @param s the input string
     * @return an array of the tokens in the string
     */	
    public String[] split(String s)
    {
        StringTokenizer st = new StringTokenizer(s);
        int tokCount = st.countTokens();
        String res[] = new String[tokCount];
        int pos=0;
        while (st.hasMoreTokens()) {
            res[pos++]=st.nextToken();
        }
        return res;	
    }

    /** Prints a line of text no newline.
     * Subclasses should call this method rather than 
     * System.out.print to allow for output to different places.
     * 
     */
    public void print(Object o)
    {
        String s=null;
        if(o == null) s = "null";
        else s = o.toString();

        if(ta != null)
            ta.append(s);
        else
            System.out.print(s);
    }

    /** Prints a line of text no newline.
     * Subclasses should call this method rather than 
     * System.out.print to allow for output to different places.
     */
    public void println(Object o)
    {
        String s=null;
        if(o == null) s = "null";
        else s = o.toString();

        if(ta != null)
            ta.append(s + "\n");
        else
            System.out.println(s);
    }

    /**
     * Handles keyRelease events
     */
    public void keyReleased(KeyEvent event)
    {
        int code = event.getKeyCode();
        if(code == KeyEvent.VK_ENTER)
        {
            int cpos = ta.getCaretPosition();
            String alltext = ta.getText();
            String before = alltext.substring(0,cpos-1);
            int startOfLine = before.lastIndexOf('\n');
            if(startOfLine > 0)
                before = before.substring(startOfLine+1);
            String prompt = getPrompt();
            String line=null;
            if(before.startsWith(prompt))
            {
                line = before.substring(prompt.length());					
                this.processCommand(line);
            }
            //			System.out.println("line ("+line+")");
            //if(!flag) this.exit();
            this.print(getPrompt());
        }
    }

    public void keyPressed(KeyEvent arg0)    { /* Not handled */    }

    public void keyTyped(KeyEvent arg0)    { /* Not handled */    }

    @Override
    public String getAppletInfo()
    {
        return "Jep Console applet\n" +
        "R Morris Mar 2005\n" +
        "See http://www.singularsys.com/";
    }

}
