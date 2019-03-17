/*
Created 18 Dec 2006 - Richard Morris
 */
package com.singularsys.jepexamples.consoles;

import java.math.MathContext;

import com.singularsys.jep.Jep;
import com.singularsys.jep.bigdecimal.BigDecComponents;

public class BigDecimalConsole extends Console {
    private static final long serialVersionUID = 1L;

    BigDecComponents bdc;
    /**
     * @param args
     */
    public static void main(String[] args) {
        BigDecimalConsole bdc = new BigDecimalConsole();
        bdc.run(args);
    }

    @Override
    public void initialise() {
        bdc = new BigDecComponents(MathContext.UNLIMITED);
        jep = new Jep(bdc);
    }

    @Override
    public String getPrompt() {
        return "BigDecConsole > ";
    }

    @Override
    public SPEC_ACTION testSpecialCommands(String command) {
        if(command.startsWith("setPrec")) {
            String[] args = split(command);
            String prec = args[1];
            String rm;
            if(args.length>=3)
                rm = args[2];
            else
                rm = "HALF_UP";
            bdc.setMathContext(new MathContext("precision="+prec+" roundingMode="+rm));
            return SPEC_ACTION.BREAK;
        }
        return super.testSpecialCommands(command);
    }

    @Override
    public void printHelp() {
        super.printHelp();
        println("'setPrec precision rounding_mode' sets the precision and rounding mode");
        println("\tprecision is the number of decimal digit accuracy, 0 is infinite precision");
        println("\trounding_mode is HALF_UP by default, see java.math.RoundingMode.");
    }

}
