package com.singularsys.jepexamples.consoles;

import com.singularsys.jep.Jep;
import com.singularsys.jep.configurableparser.StandardConfigurableParser;

public class CPConsole extends Console {
    private static final long serialVersionUID = 300L;

    @Override
    public void initialise() {
        jep = new Jep(new StandardConfigurableParser());
    }

    /** Creates a new Console object and calls run() */
    public static void main(String args[]) {
        Console c = new CPConsole();
        c.run(args);
    }

}
