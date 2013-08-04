
package se.tube42.example;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.tweeny.*;

public class Main extends Frame
{
    private static final Random rnd = new Random();
    private final TweenEquation [] eqs = {
        TweenEquation.LINEAR,
        TweenEquation.QUAD_IN,
        TweenEquation.QUAD_OUT,
        TweenEquation.CUBE_IN,
        TweenEquation.CUBE_OUT,
        TweenEquation.ELASTIC_IN,
        TweenEquation.ELASTIC_OUT
    };
    
    public Main()
    {
        
        // Frame stuff
        setSize(800, 480);
        setVisible(true);
        addWindowListener( new WindowAdapter() {
                  public void windowClosing(WindowEvent e) {
                  System.exit(0);
              }                  
              });
        
        
        // create one item for each equation
        final ExampleItem [] items = new ExampleItem[eqs.length];
        for(int i = 0; i < items.length; i++) {
            items[i] = new ExampleItem();
            items[i].setPosition(32, 32 + i * 40);
            items[i].setSize(32, 32);            
            items[i].setPositionDuration(2.0f);
            items[i].setPositionEquation(eqs[i]);            
        }
        TweenManager.removeTweens(true); // commit all tweens
        
        // this canvas will draw all items
        final Canvas c = new ExampleCanvas(items);
        add(c);
        
        // the worker thread will update the world time and request the canvas to draw the items
        new Thread() {
            public void run() {
                try {                 
                    boolean working = true, forward = false;                    
                    for(;;) {
                        Thread.sleep(1000);                        
                        long t_old = System.currentTimeMillis();
                        do {
                            Thread.sleep(1000 / 60); 
                            long t_now = System.currentTimeMillis();
                            working = TweenManager.service( (t_now - t_old) / 1000.0f);
                            t_old = t_now;
                            c.repaint();
                        } while(working);
                        
                        // nothing to tween? move some stuff                        
                        int x = forward ? 132 : c.getWidth() - 140 - 32;
                        forward = !forward;
                        for(int i = 0; i < items.length; i++)
                            items[i].setPosition(x, 32 + i * 40);                
                        
                        // change the speed
                        if(forward) {
                            float t = 0.1f + 0.2f * (rnd.nextInt() & 0xF);
                            setTitle("[Tweeny test] time=" +t);
                            for(int i = 0; i < items.length; i++) 
                                items[i].setPositionDuration(t); 
                        }
                    }
                } catch(Exception fi) { }                    
            }
        }.start();
    }
    
    
    public static void main(String []args)
    {
        new Main();
    }
}
