
package se.tube42.example;

import java.awt.*;
import java.util.*;

import se.tube42.tweeny.*;


/**
 * this is an example Item where position = (x, y) is tweened
 */

public class ExampleItem extends Item
{
    private final static Random rnd = new Random();
    
    /**
     * These are our item properties 
     */
    public static final int
          ITEM_X = 0,
          ITEM_Y = 1
          ;
    
    // these are not tweened
    private int w, h;
    private Color c;
    private String eq_name;
    
    public ExampleItem()
    {
        super(2);
        
        eq_name = "?";
        c = new Color( rnd.nextInt() & 0xFF, rnd.nextInt() & 0xFF, rnd.nextInt() & 0xFF);
        setImmediate(ITEM_X, 0);
        setImmediate(ITEM_Y, 0);
    }
    
    public void draw(Graphics g)
    {
        final int x = (int)(0.5f + getX());
        final int y = (int)(0.5f + getY());
        
        g.setColor(c);
        g.fillRect( x, y, w, h);
        
        g.setColor(Color.BLACK);
        g.drawString(eq_name,  32, y + 18);
        
    }
    
    public void setSize(float w, float h)
    {
        this.w = (int) w;
        this.h = (int) h;
    }    
    
    // -------------------------------------------
    // helper functions for position tweening with Item
    public void setPosition(float x, float y)
    {
        set(ITEM_X, x);
        set(ITEM_Y, y);
    }
    
    public float getX() { return get(ITEM_X); }
    public float getY() { return get(ITEM_Y); }
    
    
    public void setPositionDuration(float t)
    {
        setDuration(ITEM_X, t);
        setDuration(ITEM_Y, t);
    }
    
    public void setPositionEquation(TweenEquation eq)
    {
        setEquation(ITEM_X, eq);
        setEquation(ITEM_Y, eq);
        eq_name = eq.toString();
    }        
}
