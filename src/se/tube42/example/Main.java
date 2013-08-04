
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
        TweenEquation.DISCRETE,        
        TweenEquation.QUAD_IN,
        TweenEquation.QUAD_OUT,
        TweenEquation.CUBE_IN,
        TweenEquation.CUBE_OUT,
        TweenEquation.SIN_IN,
        TweenEquation.SIN_OUT,        
        TweenEquation.BACK_IN,
        TweenEquation.BACK_OUT,
        TweenEquation.ELASTIC_IN,
        TweenEquation.ELASTIC_OUT
    };
    
    public Main()
    {
        
        final WindowAdapter wc = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
                    
        // Frame stuff
        setVisible(true);
        setSize(800, 600);        
        addWindowListener(wc);
        
        
        // create two items
        final ExampleItem [] items = new ExampleItem[ eqs.length];
        for(int i = 0; i < items.length; i++) {
            ExampleItem it = items[i] = new ExampleItem();
            it.setPosition(132, 12 + i * 40);
            it.setSize(32, 32);            
            it.setPositionEquation(eqs[i]);
        }
        TweenManager.removeTweens(true); // commit all tweens
        
        // this canvas will draw all items
        final Canvas c = new ExampleCanvas(items);
        add(c);
        
        // the worker thread will update the world time and request the canvas to draw the items
        new Thread() {
            public void run() {
                try {        
                    Thread.sleep(1000);                                            
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
                        forward = !forward;                        
                        int x = forward ? 132 : c.getWidth() - 140 - 32;
                        for(int i = 0; i < items.length; i++)                         
                            items[i].setPosition(x, 12 + i * 40);                
                        
                        
                        // change the speed
                        if(!forward) {
                            final float t = 0.1f + 0.2f * (rnd.nextInt() & 0xF);
                            setTitle("[Tweeny test] time=" + t);
                            
                            for(int i = 0; i < items.length; i++)                         
                                items[i].setPositionDuration(t); 
                        }                        
                    }
                } catch(Exception fi) { }                    
            }
        }.start();
        
        
        // The graph window...
        Frame f = new Frame("The euqations...");
        int n = (int)Math.ceil(Math.sqrt( eqs.length));
        
        f.setVisible(true);
        f.setSize(84 * n, 84 * n);
        f.addWindowListener(wc);
            
        f.setLayout(new GridLayout(n, n));
        for(int i = 0; i < eqs.length; i++) 
            f.add( new EquationCanvas(eqs[i]));
        
    }
    
    
    public static void main(String []args)
    {
        new Main();
    }
}
