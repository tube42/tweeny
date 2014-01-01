package se.tube42.example.ease1;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/**
 * this class demonstrates some tweeny functions
 */
public class Main extends BaseWindow
{
    private static final Random rnd = new Random();
    
    private final TweenEquation [] eqs = {
        TweenEquation.LINEAR,
        TweenEquation.DELAYED,
        TweenEquation.DISCRETE,        
        TweenEquation.QUAD_IN,
        TweenEquation.QUAD_OUT,
        TweenEquation.QUAD_INOUT,
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
    
    private Checkbox slowdown, allow_empty;
    private ExampleItem [] items;
    
    public Main()
    {      
        setTitle("Ease equations");
        
        final int n = (int)Math.ceil(Math.sqrt( eqs.length));        

        // The graph window...
        Frame f = new Frame("The euqations...");
        f.addWindowListener(wc);        
        f.setVisible(true);
        f.setSize(84 * n, 84 * n);
        
        f.setLayout(new GridLayout(n, n));
        for(int i = 0; i < eqs.length; i++) 
            f.add( new EquationCanvas(eqs[i]));        
        
        setLocation(10, 10);
        f.setLocation(100 + getWidth(), 10);
        this.toFront();
        
        // -------------------------------------
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT));
        add(p, BorderLayout.NORTH);
        p.add(slowdown = new Checkbox("Slow down", false));
        p.add(new Label("    "));
        p.add( allow_empty = new Checkbox("Allow empty tweens", true));
        // create two items
        items = new ExampleItem[ eqs.length];
        for(int i = 0; i < items.length; i++) {
            ExampleItem it = items[i] = new ExampleItem();
            it.setPosition(132, 12 + i * 40);
            it.setSize(33, 33);            
            it.setPositionEquation(eqs[i]);            
        }
        TweenManager.removeTweens(true); // commit all tweens
        
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
        
        setSize(540, 800);        
        start();                
    }
    
    private boolean forward = false;
    private int speed = 0;
    
    int x = 0;
    public boolean frame(long dt)
    {
        // allow empty tweens?
        TweenManager.allowEmptyTweens(allow_empty.getState());
        
        // possibly slow down and run a frame
        if(slowdown.getState())
            dt = Math.max(1, dt / 4);        
        boolean ret = super.frame(dt);
                
        // nothing to tween? move some stuff            
        if(!ret) {
            forward = !forward;                        
            int x = forward ? canvas.getWidth() - 80 - 32 : 132;
            for(int i = 0; i < items.length; i++) {
                items[i].setPosition(x, 12 + i * 40);                
            }
            
            // change the speed
            if(!forward) {
                final float t = 0.5f + speed * 0.5f;
                speed = (speed + 1) & 7;
                setTitle("[Tweeny test] time=" + t + (slowdown.getState() ? " (slowed down)" : "") );
                
                for(int i = 0; i < items.length; i++)                         
                    items[i].setPositionDuration(t); 
            }
        }
        
        return ret;        
    }
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        
        // draw to back buffer: clear screen and draw each item
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        // draw the line representing current time:
        final int x = (int)(0.5f + items[0].getX()) + items[0].w / 2;
        g.setColor(items[0].c);
        g.drawLine(x, 0, x, h);
        
        // draw all items
        for(int i = 0; i < items.length; i++)
           items[i].draw(g);
        
    }
    
    
    public static void main(String []args)
    {
        new Main();
    }
}
