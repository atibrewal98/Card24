
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
public class ParametrisedPlotter extends Applet implements ItemListener {
    private static final long serialVersionUID = 330L;

    /** The expression field */
    TextField xexprField;
    TextField yexprField;
    TextField minField;
    TextField maxField;
    TextField stepsField;

    /** The canvas for plotting the graph */
    private ParametrisedCanvas graphCanvas;

    protected Jep jep;

    double tMin=-100.0;
    double tMax=100.0;
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

        xexprField = createTextField("xexpr","t^2");
        yexprField = createTextField("yexpr","t^3");
        minField = createTextField("min",String.valueOf(tMin));
        maxField = createTextField("max",String.valueOf(tMax));
        stepsField = createTextField("steps",String.valueOf(tSteps));

        xexprField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = xexprField.getText();
                xFieldChanged(expr);
            }});
        yexprField.addTextListener (new java.awt.event.TextListener () {
            public void textValueChanged (java.awt.event.TextEvent evt) {
                String expr = yexprField.getText();
                yFieldChanged(expr);
            }});
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
        adder.addGrid(new Label(" x"), 	0,0);
        adder.addGrid(xexprField, 	1,0,GridBagConstraints.REMAINDER);
        adder.addGrid(new Label(" y"), 	0,1);
        adder.addGrid(yexprField, 	1,1,GridBagConstraints.REMAINDER);
        adder.addGrid(new Label(" t"),   0,2);
        adder.addGrid(new Label("min"), 1,2);
        adder.addGrid(minField, 	2,2);
        adder.addGrid(new Label("max"), 3,2);
        adder.addGrid(maxField, 	4,2);
        adder.addGrid(new Label("steps"), 5,2);
        adder.addGrid(stepsField, 	6,2);
        adder.addGrid(new Label(), 7,2);
        /*
		gbc.gridx = 1; gbc.gridy = 0; 
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(xexprField,gbc);
		pan.add(xexprField);
		gbc.gridwidth = 1;

		gbc.gridx = 0; gbc.gridy = 1;
		pan.add(new Label("y"),gbc);

		gbc.gridx = 1; gbc.gridy = 1; 
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(yexprField,gbc);
		pan.add(yexprField);
		gbc.gridwidth = 1;

		gbc.gridx = 0; gbc.gridy = 2;
		pan.add(new Label("Min"),gbc);
		gbc.gridx = 1;
		pan.add(minField);
		gbc.gridx = 2;
		pan.add(new Label("Max"));
		gbc.gridx = 3;
		pan.add(maxField);
		gbc.gridx = 4;
		pan.add(new Label("Steps"));
		gbc.gridx = 5;
		pan.add(stepsField);
         */		
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
        xFieldChanged(xexprField.getText());
        yFieldChanged(yexprField.getText());
    }


    protected ParametrisedCanvas createGraphCanvas(Jep j) throws JepException {
        Variable x;
        x = j.addVariable("t",0.0);
        try {
            tMin = Double.parseDouble(minField.getText());
        } catch (NumberFormatException e) {/*ignored*/ }
        try {
            tMax = Double.parseDouble(maxField.getText());
        } catch (NumberFormatException e) {/*ignored*/ }
        try {
            tSteps = Integer.parseInt(stepsField.getText());
        } catch (NumberFormatException e) {/*ignored*/ }
        ParametrisedCanvas gc = new ParametrisedCanvas(j,x,tMin,tMax,tSteps);
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
    void xFieldChanged(String expr) {
        Node node = parseExpression(expr);
        if(node!=null)
            xexprField.setForeground(Color.black);
        else
            xexprField.setForeground(Color.red);
        graphCanvas.setXExpression(node);
        graphCanvas.repaint();
    }
    void yFieldChanged(String expr) {
        Node node = parseExpression(expr);
        if(node!=null)
            yexprField.setForeground(Color.black);
        else
            yexprField.setForeground(Color.red);
        graphCanvas.setYExpression(node);
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
        System.out.println(key+"\t"+vals[0]+","+ vals[1]);
        xexprField.setText(vals[0]);
        xFieldChanged(vals[0]);
        yexprField.setText(vals[1]);
        yFieldChanged(vals[1]);
        if(vals.length>=4) {
            minField.setText(vals[2]);
            maxField.setText(vals[3]);
            minFieldChanged(vals[2]);
            maxFieldChanged(vals[3]);
        }
        if(vals.length>=5) {
            stepsField.setText(vals[4]);
            stepsFieldChanged(vals[4]);
        }
    }

    String[][] equations = new String[][]{
            {"Line","3 t-1","t+1"},
            {"Circle","cos(t)","sin(t)","-pi","pi"},
            {"Ellipse","2 cos(t)","sin(t)","-pi","pi"},
            {"Cycloid","t-sin(t)","1-cos(t)","-20","20"},
            {"---- Hypocycloids ----"},
            {"Deltoid","2 cos(t)+cos(2t)","2 sin(t)-sin(2t)","-pi","pi"},
            {"Astroid","cos(t)^3","sin(t)^3","-pi","pi"},
            {"Hypocycloids 5",
                "cos(t) + cos(4 t)/4",
                "sin(t) - sin(4 t)/4","-pi","pi" },
                {"---- Epicycloids ----"},
                {"Cardioid",
                    "cos(t) - cos(2 t)/2",
                    "sin(t) - sin(2 t)/2","-pi","pi" },
                    {"Nephroid",
                        "cos(t) - cos(3 t)/3",
                        "sin(t) - sin(3 t)/3","-pi","pi" },
                        {"Epicycloid 3",
                            "cos(t) - cos(4 t)/4",
                            "sin(t) - sin(4 t)/4","-pi","pi" },
                            {"Epicycloid 4",
                                "cos(t) - cos(5 t)/5",
                                "sin(t) - sin(5 t)/5","-pi","pi" },
                                {"Epicycloid 5",
                                    "cos(t) - cos(6 t)/6",
                                    "sin(t) - sin(6 t)/6","-pi","pi" },

                                    {"---- Lissajous curves ----"},
                                    {"Lissajous 1,2","sin(t)","sin(2 t)","-pi","pi"},
                                    {"Lissajous 1,3","sin(t-pi/2)","sin(3 t)","-pi","pi"},
                                    {"Lissajous 1,4","sin(t)","sin(4 t)","-pi","pi"},
                                    {"Lissajous 2,1","sin(2t)","sin(t)","-pi","pi"},
                                    {"Lissajous 2,2","sin(2t-pi/4)","sin(2t)","-pi","pi"},
                                    {"Lissajous 2,3","sin(2t-pi/3)","sin(3t)","-pi","pi"},
                                    {"Lissajous 3,4","sin(3t)","sin(4t)","-pi","pi"},
    };

}
