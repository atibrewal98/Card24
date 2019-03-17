/*
Created 2 Nov 2006 - Richard Morris
 */
package com.singularsys.jepexamples.consoles;

import com.singularsys.jep.JepException;
import com.singularsys.jep.parser.Node;
import com.singularsys.jep.walkers.PrefixTreeDumper;
import com.singularsys.jep.walkers.TreeAnalyzer;

/**
 * A console application which dumps the tree representing an equation and statistics about the tree. 
 * @see TreeAnalyzer
 * @see PrefixTreeDumper
 */
public class PrefixDumperConsole extends Console {
    private static final long serialVersionUID = 1L;
    PrefixTreeDumper dumper;
    TreeAnalyzer totals = new TreeAnalyzer();
    boolean setDump = true;
    boolean setStats = true;
    public static void main(String[] args)
    {
        Console c = new PrefixDumperConsole();
        c.run(args);
    }

    @Override
    public void initialise() {
        super.initialise();
        dumper = new PrefixTreeDumper();
    }

    @Override
    public void processEquation(Node node) throws JepException {
        Object value = this.jep.evaluate(node);
        println("Result: "+value);
        TreeAnalyzer ta = new TreeAnalyzer(node);
        totals.merge(ta);
        if(this.setStats) {
            println(ta.toString());
            println("");
        }
        if(this.setDump)
            dumper.dump(node);
    }

    @Override
    public void printHelp() {
        super.printHelp();
        this.println("'setDump flag' whether to dump node, flag is y or n");
        this.println("'setStats flag' whether to print statistics, flag is y or n");
        this.println("'totals' prints the total statistics for all nodes");
    }

    @Override
    public SPEC_ACTION testSpecialCommands(String command) {
        if(command.startsWith("setDump")) {
            setDump = ("y".equals(this.split(command)[1]));
            return SPEC_ACTION.BREAK;
        }
        if(command.startsWith("setStats")) {
            setStats = ("y".equals(this.split(command)[1]));
            return SPEC_ACTION.BREAK;
        }
        if(command.startsWith("totals")) {
            this.println(totals.toString());
            return SPEC_ACTION.BREAK;
        }
        return super.testSpecialCommands(command);
    }


}
