/*
Created 4 Aug 2008 - Richard Morris
 */
package com.singularsys.jepexamples.applets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.Timer;


public class RepeatButton extends JButton implements MouseListener {
    private static final long serialVersionUID = 330L;

    Timer timer;
    /**
     * @param text
     */
    public RepeatButton(String text) {
        super(text);
        timer = new Timer(60,new RepeatButtonListener() );
        timer.setInitialDelay(300);
        this.addMouseListener(this);
    }

    class RepeatButtonListener implements ActionListener {
        
        public void actionPerformed(ActionEvent e) {
            System.out.println("Repeat: "+System.currentTimeMillis());
            fire();
        }
    }

    void fire() {
        this.fireActionPerformed(
                new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"repeat",System.currentTimeMillis(),0));
    }

    public void mouseClicked(MouseEvent e) {
        // not responded to
    }
    public void mouseEntered(MouseEvent e) {
        // not responded to
    }
    public void mouseExited(MouseEvent e) {
        // not responded to
    }

    public void mousePressed(MouseEvent e) {
        if(!timer.isRunning())
            timer.start();

    }
    public void mouseReleased(MouseEvent e) {
        if(timer.isRunning())
            timer.stop();

    }


}
