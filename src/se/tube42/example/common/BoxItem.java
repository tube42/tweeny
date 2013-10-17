package se.tube42.example.common;

import java.awt.*;
import java.util.*;

import se.tube42.lib.tweeny.*;


public class BoxItem extends Item
{
    public static final int
          ITEM_X = 0,
          ITEM_Y = 1,
          ITEM_SW = 2,
          ITEM_SH = 3
          ;
    
    public static final int SIZE = 60;
    
    // these are not tweened
    public int w, h;
    public Color c;
        
    public BoxItem(Color c, int x, int y)
    {
        super(4);
        
        this.c = c;
        this.w = SIZE;
        this.h = SIZE;
        
        setImmediate(ITEM_X, x);
        setImmediate(ITEM_Y, y);
        setImmediate(ITEM_SW, 1);
        setImmediate(ITEM_SH, 1);
    }
    
    public void draw(Graphics g)
    {        
        final float sw = getWidthScale();
        final float sh = getHeightScale();
        final float x = getX();
        final float y = getY();
        
        final float x0 = x + (1 - sw) * w / 2;
        final float y0 = y + (1 - sh) * h / 2;
        
        g.setColor(c);
        g.fillRect( 
                  (int) (x0),
                  (int) (y0),
                  (int) (w * sw),
                  (int) (h * sh)
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
        set(ITEM_X, x).configure(0.3f, TweenEquation.BACK_OUT);
        set(ITEM_Y, y).configure(0.3f, TweenEquation.BACK_OUT);
    }
    
    public float getX() { return get(ITEM_X); }
    public float getY() { return get(ITEM_Y); }
    public float getWidthScale() { return get(ITEM_SW); }
    public float getHeightScale() { return get(ITEM_SH); }
    
    
    
}
