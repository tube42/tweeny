
package se.tube42.demo.tweeny;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;

/**
 * Twey demo
 */
public class Main extends Frame implements Runnable, MouseListener
{
    private DemoCanvas canvas;
    private BoxItem [] items;    
    private int state;
    
    private Animation press_anim0, press_anim1, press_anim2, stop_anim0;
    private Checkbox animate, slowdown;
    
    public Main()
    {                
        this.state = 0;
        this.items = new BoxItem[3];
        this.items[0] = new BoxItem(Color.RED, 210, 210);
        this.items[1] = new BoxItem(Color.BLUE, 20, 20);
        this.items[2] = new BoxItem(Color.GREEN, 400, 20);
        
        
        items[0].setEquation(BoxItem.ITEM_X, TweenEquation.QUAD_OUT);
        items[0].setEquation(BoxItem.ITEM_Y, TweenEquation.QUAD_OUT);
        
        AnimationBuilder ab = new AnimationBuilder();
        int id1 = ab.addProperty(items[0], BoxItem.ITEM_SW, 1f);
        int id2 = ab.addProperty(items[0], BoxItem.ITEM_SH, 1f);
        ab.set(id1, TweenEquation.QUAD_IN, 0.9f, 0.1f, 1.1f, 0.1f, 1f, 0.2f);        
        ab.set(id2, TweenEquation.QUAD_IN, 0.9f, 0.1f, 1.1f, 0.1f, 1f, 0.2f);
        press_anim0 = ab.build(null);
        
        // clone it and make it tween item1 instead
        press_anim1 = new Animation( press_anim0);
        press_anim1.replaceProperty(id1, items[1], BoxItem.ITEM_SW);
        press_anim1.replaceProperty(id2, items[1], BoxItem.ITEM_SH);
        
        // same for #2
        press_anim2 = new Animation( press_anim0);
        press_anim2.replaceProperty(id1, items[2], BoxItem.ITEM_SW);
        press_anim2.replaceProperty(id2, items[2], BoxItem.ITEM_SH);
        
        
        // the stop animation
        ab.reset();
        id2 = ab.addProperty(items[0], BoxItem.ITEM_SH, 1f);
        ab.set(id2, TweenEquation.QUAD_IN, 0.95f, 0.05f, 1f, 0.2f);
        stop_anim0 = ab.build(null);
        
        
        // this canvas will draw all items
        add( canvas = new DemoCanvas(items), BorderLayout.CENTER);
        canvas.addMouseListener(this);
        
        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(animate = new Checkbox("Animate", true));
        panel.add(slowdown = new Checkbox("Slow down", false));
        
        Thread t = new Thread(this);
        t.start();
        
        // Frame stuff        
        final WindowAdapter wc = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        
        setVisible(true);
        setSize(500, 600);
        setLocation(10, 10);        
        addWindowListener(wc);
        
    }
    
    
    // probably not needed, but just in case
    public void update(Graphics g) 
    {
        paint(g);
    }
    
    // the worker thread will update the world time and request the canvas to draw the items
    
    public void run() {
        try {       
            
            long t_old = System.currentTimeMillis();            
            for(;;) {                
                Thread.sleep(1000 / 100); // this is not really 100fps, the time is not that accurate ...
                long t_now = System.currentTimeMillis();
                long dt = t_now - t_old;
                t_old = t_now;
                
                if(!animate.getState()) dt = 10000;
                else if(slowdown.getState()) dt = Math.max(1, dt / 2);
                TweenManager.service( dt);
                
                canvas.repaint();
            }
        } catch(Exception error) {
            System.out.println("ERROR " + error);
            System.exit(0);
        }
    }
    
    public void mousePressed(MouseEvent e)
    { 
        final int x = e.getX();
        final int y = e.getY();
        
        if(items[0].hit(x, y)) press_anim0.start();        
        else if(items[1].hit(x, y)) press_anim1.start();        
        else if(items[2].hit(x, y)) press_anim2.start();        
    }
    
    public void mouseReleased(MouseEvent e) 
    { 
        final int x = e.getX();
        final int y = e.getY();
        
        if(items[0].hit(x, y)) {
            state = (state + 1) & 3;
            switch(state) {
            case 0: 
                items[1].setPosition( 20 , 20); 
                items[2].setPosition( 400, 20); 
                break;
            case 1: 
                items[1].setPosition( 20 , 400); 
                items[2].setPosition( 400, 400); 
                break;
            case 2: 
                items[1].setPosition( 400, 400); 
                items[2].setPosition( 20 , 400); 
                break;
            case 3: 
                items[1].setPosition( 400,  20); 
                items[2].setPosition( 20 ,  20);                 
                break;                
            }
        }        
        
        if(items[1].hit(x, y)) {            
            float y1 = items[0].getY() - 20;
            if(y1 < 20) {
                stop_anim0.start();
                y1= 20;
            }
            items[0].setPosition(items[0].getX(), y1);
        }
        
        if(items[2].hit(x, y)) {            
            float y1 = items[0].getY() + 20;
            if(y1 > 400) {
                stop_anim0.start();
                y1 = 400;
            }
            items[0].setPosition(items[0].getX(), y1);
        }
    }
    
    
    public void mouseClicked(MouseEvent e) { }    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    
    public static void main(String []args)
    {
        new Main();
    }
}
