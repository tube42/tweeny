package se.tube42.example.demo3;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** Tweeny demo 3 */
public class Main 
extends BaseWindow 
implements MouseListener
{
    private static final int POINTS = 6;
    private PointItem point;
    private int [] points;    
    private int cnt;
    
    private Animation anim;
    private Object [] marker;
    
    public Main()
    {                
        setTitle("Dynamic test");
        
        // marker and data for the dynamic part        
        this.marker = new Object[POINTS*2];
        this.points = new int[POINTS*2];
        this.cnt = 0;
        
        AnimationBuilder ab = new AnimationBuilder();
        this.point = new PointItem(Color.RED, 100, 100);
        
        
        int idx = ab.addProperty( point, PointItem.ITEM_X, 0);
        marker[0] = ab.getMarker();
        
        int idy = ab.addProperty( point, PointItem.ITEM_Y, 0);
        marker[1] = ab.getMarker();
        
        for(int i = 1; i < POINTS; i++) {
            ab.set(idx, TweenEquation.BACK_IN, 0, 1f);
            marker[i * 2 + 0] = ab.getMarker();
            
            ab.set(idy, TweenEquation.BACK_IN, 0, 1f);
            marker[i * 2 + 1] = ab.getMarker();
        }
        
        // create animation
        anim = ab.build(null);
        
        // create initial values and update animation:
        for(int i = 0; i < POINTS; i++) {
            points[i * 2 + 0 ] = 64 + i * 128;
            points[i * 2 + 1 ] = 74 + 128 * (i & 1);
        }
        update_points();            
        anim.start();  // first animation
            
        
        canvas.addMouseListener(this);
        setSize(800, 400);        
        start();                
    }
    
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        // clear screen
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        // draw help text
        g.setColor(Color.BLACK);
        g.drawString("Left click to move the white point", 20, 100);
        g.drawString("Right click to start animation", 20, 140);
        
        // draw the lines
        int lx = 0, ly = 0;
        g.setColor(Color.BLACK);        
        for(int i = 0; i < POINTS; i++) {             
            if(i != 0) g.drawLine(lx, ly, points[i*2], points[i*2+1]);            
            lx = points[i*2];
            ly = points[i*2+1];
        }
        
        // draw the points
        for(int i = 0; i < POINTS; i++) {
            g.setColor(i == cnt ? Color.WHITE : Color.BLUE);                    
            g.fillRect(points[i*2 + 0] - 12, points[i*2 + 1] - 12, 24, 24);           
        }
        
        // draw the items
        point.draw(g);
    }
    
    
    // ----------------------------------
    // this is where we actually modify points in the generated animation    
    
    private void update_points()
    {
        for(int i = 0; i < points.length; i ++)
            AnimationBuilder.changeValueAtMarker(anim, 
                      marker[i], (float) points[i]);
    }
    
    // ----------------------------------
    
    public void mousePressed(MouseEvent e)
    {                 
        if( e.getButton() != MouseEvent.BUTTON1) {
            anim.start();
        } else {
            points[cnt * 2 + 0] = e.getX();
            points[cnt * 2 + 1] = e.getY();
            cnt++;
            if(cnt >= POINTS) cnt = 0;
            update_points();
        }
    }
    
    // -------------------------------------------
    
    public static void main(String []args)
    {
        new Main();
    }
}
