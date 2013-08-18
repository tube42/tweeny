
package se.tube42.example.tweeny;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;

/**
 * this class demonstrates some tweeny functions
 */
public class Main extends Frame
{
    private static final Random rnd = new Random();
    
    private final TweenEquation [] eqs = {
        TweenEquation.LINEAR,
        TweenEquation.DELAYED,
        TweenEquation.DISCRETE,        
        TweenEquation.QUAD_IN,
        TweenEquation.QUAD_OUT,
        TweenEquation.CUBE_IN,
        TweenEquation.CUBE_OUT,
        TweenEquation.SIN_IN,
        TweenEquation.SIN_OUT,        
        TweenEquation.BACK_IN,
        TweenEquation.BACK_OUT,
        TweenEquation.TUBE42_1,
        TweenEquation.TUBE42_2,        
        TweenEquation.ELASTIC_IN,
        TweenEquation.ELASTIC_OUT
    };
    private Checkbox slowdown;
    
    public Main()
    {
        
        final WindowAdapter wc = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        
        // Frame stuff
        setVisible(true);
        setSize(540, 800);        
        addWindowListener(wc);
        
        Panel p = new Panel();
        add(p, BorderLayout.NORTH);
        p.add(slowdown = new Checkbox("Slow down", false));
        
        // create two items
        final ExampleItem [] items = new ExampleItem[ eqs.length];
        for(int i = 0; i < items.length; i++) {
            ExampleItem it = items[i] = new ExampleItem();
            it.setPosition(132, 12 + i * 40);
            it.setSize(33, 33);            
            it.setPositionEquation(eqs[i]);            
        }
        TweenManager.removeTweens(true); // commit all tweens
        
        // this canvas will draw all items
        final Canvas c = new ExampleCanvas(items);
        add(c);
        
        
        // create a bunch of animation to show before we start
        AnimationBuilder ab = new AnimationBuilder();   
        for(int i = 0; i < items.length; i++) {
            final ExampleItem ei = items[i];
            final ExampleItem ei2 = items[ (i + 4) % items.length];
            final float t = 0.3f + 0.05f * (rnd.nextInt() & 7); 
            int idy = ab.addProperty(ei, ExampleItem.ITEM_Y, -100);
            ab.pause(idy, 2f);                                  
            ab.set(idy, TweenEquation.BACK_OUT, ei2.getY(), t);
            ab.pause(idy, 1f);                      
            ab.set(idy, TweenEquation.BACK_IN, ei.getY(), 1);
        }
        
        
        final Animation anim = ab.build(new Runnable() {
                  public void run(){
                  System.out.println("FYI: animation ended. Reseting equations");
                  for(int i = 0; i < items.length; i++) items[i].setPositionEquation(eqs[i]);                          
                  
              }
              });
        System.out.println("FYI: starting the initial animation");
        anim.start();            
        
        
        // the worker thread will update the world time and request the canvas to draw the items
        new Thread() {
            public void run() {
                try {        
                    boolean working = true, forward = false;
                    for(int speed = 0; ; ) {
                        long t_old = System.currentTimeMillis();
                        do {
                            Thread.sleep(1000 / 100); // this is not really 100fps, the time is not that accurate ...
                            long t_now = System.currentTimeMillis();
                            long dt = t_now - t_old;
                            t_old = t_now;
                            
                            if(slowdown.getState())
                                dt = Math.max(1, dt / 5);
                            
                            working = TweenManager.service(dt);
                            
                            c.repaint();
                            
                        } while(working);
                        
                        // nothing to tween? move some stuff                        
                        forward = !forward;                        
                        int x = forward ? 132 : c.getWidth() - 80 - 32;
                        for(int i = 0; i < items.length; i++) {
                            items[i].setPosition(x, 12 + i * 40);                
                        }
                        
                        // change the speed
                        if(!forward) {
                            // final float t = 0.1f + 0.3f * (rnd.nextInt() & 0x3);
                            final float t = 0.5f + speed * 0.5f;
                            speed = (speed + 1) & 7;
                            setTitle("[Tweeny test] time=" + t);
                            
                            for(int i = 0; i < items.length; i++)                         
                                items[i].setPositionDuration(t); 
                        }                        
                        Thread.sleep(1000);                                                                    
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
        
        setLocation(10, 10);
        f.setLocation(100 + getWidth(), 10);
        this.toFront();
    }
    
    // probably not needed, but just in case
    public void update(Graphics g) 
    {
        paint(g);
    }
    
    public static void main(String []args)
    {
        new Main();
    }
}
