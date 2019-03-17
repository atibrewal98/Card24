
/*
HTML code for applet:
<applet code="org.nfunk.jepexamples.FunctionPlotter" width=300 height=320>
<param name=initialExpression value="100 sin(x/3) cos(x/70)">
</applet>
 */

package com.singularsys.jepexamples.applets;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;
import com.singularsys.jep.Variable;
import com.singularsys.jep.parser.Node;

/**
 * This applet is a demonstration of the possible applications of the Jep
 * mathematical expression parser.<p>
 * The FunctionPlotter class arranges the text field and FunctionCanvas classes
 * and requests a repainting of the graph when the expression in the text
 * field changes. All plotting (and interaction with the Jep API) is performed
 * in FunctionCanvas class.
 */
public class FunctionPlotter extends Applet implements ItemListener {
    private static final long serialVersionUID = 330L;

    /** The expression field */
    TextField exprField;

    /** List of equations */
    List list = new List(20);

    Map<String,String> map = new HashMap<String,String>();

    /** The canvas for plotting the graph */
    private FunctionCanvas graphCanvas;

    protected Jep jep;
    /**
     * Initializes the applet FunctionPlotter
     */
    @Override
    public void init ()  {
        try {
            jep = initJep();
            initComponents(jep);

         } catch (JepException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the jep instance
     * @return the new instance
     * @throws JepException
     */
    protected Jep initJep()  throws JepException {
        Jep j = new Jep();
        // Allow implicit multiplication
        j.setImplicitMul(true);

        return j;
    }

    /**
     * Sets the layout of the applet window to BorderLayout, creates all
     * the components and associates them with event listeners if necessary.
     * @param j 
     * @throws JepException 
     */
    private void initComponents (Jep j) throws JepException {
        setLayout(new BorderLayout());
        setBackground (java.awt.Color.white);

        // get the initial expression from the parameters
        String expr = getParameter("initialExpression");
        
        // Try to see if equation was specified in URL
        URL docBase = this.getDocumentBase(); 
        String query = docBase.getQuery();
        if(query!=null) {
            String[] parts = query.split("/\\&/");
            for(String part : parts) {
                if(part.startsWith("EQN="))
                    expr =part.substring(4);
            }
         }

        // set default expression if none specified
        if (expr==null)
            expr="x*sin(1/x)";

        
        exprField = new java.awt.TextField(expr);

        // adjust various settings for the expression field
        exprField.setBackground (java.awt.Color.white);
        exprField.setName ("exprField");
        exprField.setFont (new java.awt.Font ("Dialog", 0, 14));
        exprField.setForeground (java.awt.Color.black);
        exprField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = exprField.getText();
                exprFieldTextValueChanged(expr);
            }
        });

        add ("South", exprField);

        for(String[] eles:equations) {
            if(map.containsKey(eles[0]))
                System.out.println("Duplicate key: "+eles[0]);
            else {
                map.put(eles[0],eles[1]);
                list.add(eles[0]);
            }
        }
        list.addItemListener(this);
        add("East",list);

        // create the graph canvas and add it
        graphCanvas = createGraphCanvas(j);
        add ("Center", graphCanvas);
        exprFieldTextValueChanged(expr);
    }
    String[][] equations = new String[][]{
            {"Line","3 x-1"},
            {"x","x"},
            {"x^2","x^2"},
            {"x^3","x^3"},
            {"x^4","x^4"},
            {"exp(x)","exp(x)"},
            {"log(x)","log(x)"},
            {"sin(x)","sin(x)"},
            {"sin(1/x)","sin(1/x)"},
            {"x sin(1/x)","x sin(1/x)"},
            {"x^2 sin(1/x)","x^2 sin(1/x)"},
            {"Sawtooth","x % 1 + if(x<0,1,0)"},
            {"Square wave","if(x % 2 + if(x<0,2,0)>1,1,0)"},
    };

    public void itemStateChanged(ItemEvent e) {
        int index = (Integer) e.getItem();
        String key = list.getItem(index);
        String val = map.get(key);
        if(val==null) return;
        System.out.println(key+"\t"+val);
        exprField.setText(val);
        exprFieldTextValueChanged(val);
    }


    protected FunctionCanvas createGraphCanvas(Jep j) throws JepException {
        Variable x;
        x = j.addVariable("x",0.0);
        FunctionCanvas gc = new FunctionCanvas(j,x);
        return gc;
    }

    /**
     * Attempts to parse the expression.
     * 
     * @param newString
     * @return the node representing the expression or null on errors
     */
    private Node parseExpression(String newString) {
        // Parse the new expression
        try {
            Node node = jep.parse(newString);
            return node;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Repaints the graphCanvas whenever the text in the expression field
     * changes.
     */
    void exprFieldTextValueChanged(String expr) {
        Node node = parseExpression(expr);
        if(node!=null)
            exprField.setForeground(Color.black);
        else
            exprField.setForeground(Color.red);
        graphCanvas.setExpression(node);
        graphCanvas.repaint();
    }


    @Override
    public String getAppletInfo() {
        return "Jep Function Plotter\n"+
            "Author: N. Funk and R. Morris\n"+
            "Draws functions of a single variable.\n"+
            "The initial expression can be specified with the initialExpression parameter\n"+
            "or the URL query string in the EQN field";
    }

    @Override
    public String[][] getParameterInfo() {
        
        String pinfo[][] = {
                {"initialExpression",    "mathematical expression",    "initial expression to use"},
        };

        return pinfo;
    }


}
