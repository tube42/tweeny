package se.tube42.example.demo2;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** Tweeny demo 2 */
public class Main 
extends BaseWindow 
{
    private PointItem point;
    
    private Animation press_anim;
    
    public Main()
    {   
        setTitle("Animation delta demo");
        this.point = new PointItem(Color.RED, -100, -100);
        
        // create animation to play when clicked
        AnimationBuilder ab = new AnimationBuilder();
        int idx = ab.addProperty(point, PointItem.ITEM_X, 0f);
        int idy = ab.addProperty(point, PointItem.ITEM_Y, 0f);
        int ids = ab.addProperty(point, PointItem.ITEM_S, 0f);
        
        for(int i = 0; i < 10; i++) {
            float t = 0.02f * (i + 1);
            int a = 5 + 5 * (i + 1);            
            ab.set(idx, TweenEquation.QUAD_IN, -a, t, -a, t, +a, t, +a, t);
            ab.set(idy, TweenEquation.QUAD_IN, -a, t, +a, t, +a, t, -a, t);
        }
        
        for(int i = 0; i < 10; i++)
            ab.set(ids, TweenEquation.LINEAR, -0.5f, 0.1f, 0, 0.1f, +0.5f, 0.1f, 0, 0.1f);        
        
        // end at zero
        ab.set(idx, TweenEquation.LINEAR, 0, 0.1f);
        ab.set(idy, TweenEquation.LINEAR, 0, 0.1f);
        ab.set(ids, TweenEquation.LINEAR, 0, 0.1f);
        
        press_anim = ab.build(null);
        
        
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        setSize(800, 400);        
        start();        
    }
    
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        g.setColor(Color.BLACK);        
        g.drawString("Touch anywhere in the screen and then move the pointer", 30, 70);
        
        point.draw(g);
    }
    
    // ----------------------------------
    
    public void mouseDragged(MouseEvent e)
    {
        press_anim.deltaSet(point, PointItem.ITEM_X, e.getX() );
        press_anim.deltaSet(point, PointItem.ITEM_Y, e.getY() );        
    }
    
    public void mouseMoved(MouseEvent e)
    {
        press_anim.deltaSet(point, PointItem.ITEM_X, e.getX() );
        press_anim.deltaSet(point, PointItem.ITEM_Y, e.getY() );                
    }
    // ----------------------------------
    
    public void mousePressed(MouseEvent e)
    {         
        point.setImmediate(PointItem.ITEM_X, e.getX() );
        point.setImmediate(PointItem.ITEM_Y, e.getY() );
        point.setImmediate(PointItem.ITEM_S, 1);
        press_anim.deltaSet(point);
        
        press_anim.start();
    }
    
    // -------------------------------------------
    
    public static void main(String []args)
    {
        new Main();
    }
}
