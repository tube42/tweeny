
package se.tube42.demo.tweeny;

import java.awt.*;
import java.util.*;

import se.tube42.lib.tweeny.*;


public class BoxItem extends Item
{
    public static final int
          ITEM_X = 0,
          ITEM_Y = 1,
          ITEM_S = 2
          ;
    
    // these are not tweened
    public int w, h;
    public Color c;
    
    public BoxItem(Color c, int x, int y)
    {
        super(3);
        
        this.c = c;
        this.w = 64;
        this.h = 64;
        
        setImmediate(ITEM_X, x);
        setImmediate(ITEM_Y, y);
        setImmediate(ITEM_S, 1);
        
        setDuration(ITEM_X, 0.3f);
        setDuration(ITEM_Y, 0.3f);                
        setEquation(ITEM_X, TweenEquation.QUAD_IN);
        setEquation(ITEM_Y, TweenEquation.QUAD_IN);
    }
    
    public void draw(Graphics g)
    {        
        final float s = getScale();
        final float x = getX();
        final float y = getY();
        
        final float x0 = x + (1 - s) * w / 2;
        final float y0 = y + (1 - s) * h / 2;
        
        g.setColor(c);
        g.fillRect( 
                  (int) (x0),
                  (int) (y0),
                  (int) (w * s),
                  (int) (h * s)
                  );
                  
    }
    
    
    public boolean hit(int x, int y)
    {
        final int x0 = (int)(0.5f + getX());
        final int y0 = (int)(0.5f + getY());
        
        return (x >= x0 && x < x0 + w && y >= y0 && y < y0 + h);
    }
    
    // -------------------------------------------
    // helper functions for position tweening with Item
    public void setPosition(float x, float y)
    {
        set(ITEM_X, x);
        set(ITEM_Y, y);
    }
    public void setScale(float s)
    {
        set(ITEM_S, s);
    }
    
    public float getX() { return get(ITEM_X); }
    public float getY() { return get(ITEM_Y); }
    public float getScale() { return get(ITEM_S); }
    
    
    
}
