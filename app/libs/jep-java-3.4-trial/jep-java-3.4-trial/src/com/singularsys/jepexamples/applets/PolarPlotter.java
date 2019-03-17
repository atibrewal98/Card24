
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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
 * The FunctionPlotter class arranges the text field and GraphCanvas classes
 * and requests a repainting of the graph when the expression in the text
 * field changes. All plotting (and interaction with the Jep API) is performed
 * in GraphCanvas class.
 */
public class PolarPlotter extends Applet implements ItemListener {
    private static final long serialVersionUID = 330L;

    /** The expression field */
    TextField rexprField;

    TextField minField;
    TextField maxField;
    TextField stepsField;

    /** The canvas for plotting the graph */
    private PolarCanvas graphCanvas;

    protected Jep jep;

    double tMin=0;
    double tMax=2*Math.PI;
    int tSteps=2000;

    /** List of equations */
    List list = new List(20);

    Map<String,String[]> map = new HashMap<String,String[]>();

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
        j.setAllowUndeclared(false);
        j.setAllowAssignment(false);
        return j;
    }

    TextField createTextField(String parameter,String defaultVal) {
        String expr = getParameter(parameter);
        if (expr==null) expr=defaultVal;
        TextField field = new java.awt.TextField(expr);
        field.setBackground (java.awt.Color.white);
        field.setFont (new java.awt.Font ("Dialog", 0, 14));
        field.setForeground (java.awt.Color.black);
        return field;
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

        rexprField = createTextField("rexpr","th^2");

        rexprField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = rexprField.getText();
                rFieldChanged(expr);
            }});
        minField = createTextField("min","0");
        maxField = createTextField("max","2 pi");
        stepsField = createTextField("steps",String.valueOf(tSteps));

        minField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = minField.getText();
                minFieldChanged(expr);
            }});
        maxField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = maxField.getText();
                maxFieldChanged(expr);
            }});
        stepsField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = stepsField.getText();
                stepsFieldChanged(expr);
            }});


        final GridBagLayout gbl = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();  
        final Panel pan = new Panel(gbl);
        //pan.setBackground(Color.cyan);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        class Adder {
            void addGrid(Component comp,int x,int y) {
                gbc.gridx = x; gbc.gridy = y;
                gbc.gridwidth = 1;
                gbc.weightx = 0;
                gbl.setConstraints(comp, gbc);
                pan.add(comp);
            }
            void addGrid(Component comp,int x,int y,int w) {
                gbc.gridx = x; gbc.gridy = y;
                gbc.gridwidth = w;
                gbc.weightx = 1;
                gbl.setConstraints(comp, gbc);
                pan.add(comp);
            }}
        Adder adder = new Adder();
        adder.addGrid(new Label(" r"), 	0,0);
        adder.addGrid(rexprField, 	1,0,GridBagConstraints.REMAINDER);

        adder.addGrid(new Label(" th"),   0,1);
        adder.addGrid(new Label("min"), 1,1);
        adder.addGrid(minField, 	2,1);
        adder.addGrid(new Label("max"), 3,1);
        adder.addGrid(maxField, 	4,1);
        adder.addGrid(new Label("steps"), 5,1);
        adder.addGrid(stepsField, 	6,1);
        adder.addGrid(new Label(), 7,1);

        add ("South",pan);

        for(String[] eles:equations) {
            if(map.containsKey(eles[0]))
                System.out.println("Duplicate key: "+eles[0]);
            else {
                String[] vals = new String[eles.length-1];
                for(int i=1;i<eles.length;++i)
                    vals[i-1]=eles[i];
                map.put(eles[0],vals);
                list.add(eles[0]);
            }
        }
        list.addItemListener(this);
        add("East",list);


        // create the graph canvas and add it
        graphCanvas = createGraphCanvas(j);
        add ("Center", graphCanvas);
        rFieldChanged(rexprField.getText());
    }

    protected PolarCanvas createGraphCanvas(Jep j) throws JepException {
        Variable x;
        x = j.addVariable("th",0.0);
        PolarCanvas gc = new PolarCanvas(j,x,0,2*Math.PI,tSteps);
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
        System.out.println(newString);
        try {
            Node node = jep.parse(newString);
            return node;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Repaints the graphCanvas whenever the text in the expression field
     * changes.
     */
    void rFieldChanged(String expr) {
        Node node = parseExpression(expr);
        if(node!=null)
            rexprField.setForeground(Color.black);
        else
            rexprField.setForeground(Color.red);
        graphCanvas.setRexpression(node);
        graphCanvas.repaint();
    }

    void minFieldChanged(String s) {
        try {
            jep.parse(s);
            tMin = jep.evaluateD();
            minField.setForeground(Color.black);
            graphCanvas.setRange(tMin,tMax, tSteps);
            graphCanvas.repaint();
        } catch (JepException e) {
            minField.setForeground(Color.red);
        }
    }
    void maxFieldChanged(String s) {
        try {
            jep.parse(s);
            tMax = jep.evaluateD();
            maxField.setForeground(Color.black);
            graphCanvas.setRange(tMin,tMax, tSteps);
            graphCanvas.repaint();
        } catch (JepException e) {
            maxField.setForeground(Color.red);
        }
    }
    void stepsFieldChanged(String s) {
        try {
            tSteps = Integer.parseInt(s);
            stepsField.setForeground(Color.black);
            graphCanvas.setRange(tMin,tMax, tSteps);
            graphCanvas.repaint();
        } catch (NumberFormatException e) {
            stepsField.setForeground(Color.red);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        int index = (Integer) e.getItem();
        String key = list.getItem(index);
        String[] vals = map.get(key);
        if(vals.length==0) return;
        rexprField.setText(vals[0]);
        rFieldChanged(vals[0]);
        System.out.println(key+"\t"+vals[0]);
        if(vals.length>=3) {
            minField.setText(vals[1]);
            maxField.setText(vals[2]);
            minFieldChanged(vals[1]);
            maxFieldChanged(vals[2]);
        }
        if(vals.length>=4) {
            stepsField.setText(vals[3]);
            stepsFieldChanged(vals[3]);
        }
    }

    String[][] equations = new String[][]{
            {"Circle","1"},
            {"Circle center (1,0)","2 cos(th)","0","pi"},
            {"Vertical line","1/cos(th)","0","pi"},
            {"Ellipse","1/(1 + 0.5 cos(th))","-pi","pi"},
            {"Parabola","1/(1 + cos(th))","-pi","pi"},
            {"Hyperbola","1/(1 + 1.5 cos(th))","-pi","pi"},
            {"---- Spirals ----"},
            {"Archimedean spiral","th/pi","0","6 pi"},
            {"Fermat's spiral","sqrt(th)","0","6 pi"},
            {"Hyperbolic spiral","1/th","0","20 pi"},
            {"Lituus","1/sqrt(th)","0","20 pi"},
            {"Logarithmic spiral","1.1^th","0","6 pi"},
            {"Lemniscate of Bernoulli","sqrt(cos(2 th))","-pi/4","5pi/4","451"},
            {"---- Rhodonea curves ----"},
            {"Rose 2","cos(2 th)","-pi","pi"},
            {"Rose 3","cos(3 th)","-pi","pi"},
            {"Rose 4","cos(4 th)","-pi","pi"},
            {"Rose 5","cos(5 th)","-pi","pi"},
            {"Rose 6","cos(6 th)","-pi","pi"},
            {"Rose 1/2","cos(th/2)","-2pi","2pi"},
            {"Rose 3/2","cos(3 th/2)","-2pi","2pi"},
            {"Rose 5/2","cos(5 th/2)","-2pi","2pi"},
            {"Rose 1/3","cos(th/3)","-3pi","3pi"},
            {"Rose 2/3","cos(2 th/3)","-3pi","3pi"},
            {"Rose 4/3","cos(4 th/3)","-3pi","3pi"},
    };

}
