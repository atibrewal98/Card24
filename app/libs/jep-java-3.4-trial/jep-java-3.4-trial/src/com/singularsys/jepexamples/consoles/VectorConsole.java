package com.singularsys.jepexamples.consoles;

import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.ConfigurableParser;

/**
 * A console which illustrates a different syntax for vectors using mathematical (1,2,3) notation.
the tokenizer

 */
public class VectorConsole extends Console {
    private static final long serialVersionUID = 300L;

    public VectorConsole() { /* do nothing */ }


    static class VectorParser extends ConfigurableParser {
        private static final long serialVersionUID = 320L;

        VectorParser() {
            this.addHashComments();
            this.addSlashComments();
            this.addDoubleQuoteStrings();
            this.addWhiteSpace();
            this.addExponentNumbers();
            this.addIdentifiers();
            this.addOperatorTokenMatcher();
            this.addSymbols(new String[]{"(",")","[","]",","});
            this.setImplicitMultiplicationSymbols(new String[]{"(","["});
            this.addSemiColonTerminator();
            this.addWhiteSpaceCommentFilter();
            this.addListOrBracketMatcher("(",")",",");
            this.addFunctionMatcher("(",")",",");
            this.addListMatcher("[","]",",");
            this.addArrayAccessMatcher("[","]");

        }
    }
    @Override
    public void initialise() {
        jep = new Jep();
        jep.setComponent(new VectorParser());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        VectorConsole vc = new VectorConsole();
        vc.run(args);

    }

}
