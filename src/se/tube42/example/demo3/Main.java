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
    private PointItem point1, point2;
    private Animation anim;
    private Object [] marker;
    private int [] points;
    private int cnt;
    
    public Main()
    {                
        setTitle("Dynamic test");
        
        // marker and data for the dynamic part        
        this.marker = new Object[8];
        this.points = new int[8];
        this.cnt = 0;
        
        AnimationBuilder ab = new AnimationBuilder();
        this.point1 = new PointItem(Color.RED, 100, 100);
        this.point2 = new PointItem(Color.BLUE, 200, 100);
        
        int [] ids = new int[4];
        
        // create start points        
        for(int i = 0; i < 4; i++) {
            ids[i] = ab.addProperty( i > 1 ? point2 : point1, 
                      (i & 1) == 0 ? PointItem.ITEM_X : PointItem.ITEM_Y, 
                      0f /* <- this will be replaced */ );
            marker[i] = ab.getMarker();            
        }
        
        // get the end points
        for(int i = 0; i < 4; i++) {        
            ab.set(ids[i], TweenEquation.BACK_IN, 0 /* <-- this will be replaced */, 1f);
            marker[4 + i] = ab.getMarker();
        }
        
        // create animation
        anim = ab.build(null);
        
        // create initial values and update animation:
        Random rnd = new Random();
        for(int i = 0; i < points.length; i++)
            points[i] = Math.abs( rnd.nextInt(300) );
        update_points();
        anim.start();  // first animation
            
        
        canvas.addMouseListener(this);
        setSize(800, 400);        
        start();                
    }
    
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        // draw to back buffer: clear screen and draw each item
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        // draw help text
        g.setColor(Color.BLACK);
        g.drawString("Right click to change each point", 20, 100);
        g.drawString("Left click to start animation", 20, 140);

        // draw end points
        g.setColor(Color.black);
        g.drawLine(points[0], points[1], points[4], points[5]);
        g.drawLine(points[2], points[3], points[6], points[7]);
        
        for(int i = 0; i < points.length; i += 2) {
            g.setColor(i == cnt ? Color.WHITE : Color.YELLOW);            
            g.fillRect(points[i + 0] - 12, points[i + 1] - 12, 24, 24);   
            g.setColor(Color.BLACK);
        }
        
        // draw the items
        point1.draw(g);
        point2.draw(g);
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
        if( e.getButton() == MouseEvent.BUTTON1) {
            anim.start();
        } else {
            points[cnt++] = e.getX();
            points[cnt++] = e.getY();
            if(cnt >= points.length) cnt = 0;
            update_points();
        }
    }
    
    public void mouseReleased(MouseEvent e) {  }        
    public void mouseClicked(MouseEvent e) { }    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    
    // -------------------------------------------
    
    public static void main(String []args)
    {
        new Main();
    }
}
