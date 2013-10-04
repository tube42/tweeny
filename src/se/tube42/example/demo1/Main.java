package se.tube42.example.demo1;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** Tweeny demo1 */
public class Main 
extends BaseWindow 
implements MouseListener
{
    private BoxItem [] items;    
    private int state;
    
    private Animation press_anim0, press_anim1, press_anim2, stop_anim0;
    private Checkbox cb_anim, cb_slow;
        
    public Main()
    {                
        this.state = 0;
        this.items = new BoxItem[3];
        this.items[0] = new BoxItem(Color.RED, 210, 210);
        this.items[1] = new BoxItem(Color.BLUE, 20, 20);
        this.items[2] = new BoxItem(Color.GREEN, 400, 20);
                        
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
                
        
        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(cb_anim = new Checkbox("Animate", true));
        panel.add(cb_slow = new Checkbox("Slow down", false));
        
        
        canvas.addMouseListener(this);
        
        setSize(500, 600);        
        start();        
    }
    
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        // draw to back buffer: clear screen and draw each item
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        // draw all items
        for(int i = 0; i < items.length; i++)
           items[i].draw(g);                
    }
    
    
    // ------------------------------------
    
    public boolean frame(long dt)
    {
        if(!cb_anim.getState()) dt = 10000;
        else if(cb_slow.getState()) dt /= 4;
        
        return super.frame(dt);
    }
    
    // ----------------------------------
    
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
