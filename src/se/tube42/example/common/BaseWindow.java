package se.tube42.example.common;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;

public abstract class BaseWindow 
extends Frame 
implements Runnable, MouseListener, MouseMotionListener
{    
    protected static final WindowAdapter wc = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    };
    
    protected DBCanvas canvas;
    
    public BaseWindow()
    {
        this.canvas = new DBCanvas(this);
        add(canvas, BorderLayout.CENTER);
        
        setLocation(10, 10);        
        addWindowListener(wc);
    }
    
    public final void start()
    {
        // force re-layout?
        setVisible(true);
        setVisible(true);
        setVisible(true);
        
        Thread t = new Thread(this);
        t.start();
    }
    
    public abstract void paintCanvas(Graphics g, int w, int h);
    
    // probably not needed, but just in case
    public void update(Graphics g) 
    {
        paint(g);
    }
    
    public final void run() 
    {
        try {                   
            long t_old = System.currentTimeMillis();            
            for(;;) {                
                Thread.sleep(1000 / 100); // this is not really 100fps, the time is not that accurate ...
                long t_now = System.currentTimeMillis();
                long dt = t_now - t_old;
                t_old = t_now;
                
                frame(dt);
                
                if(canvas != null)
                    canvas.repaint();
            }
        } catch(Exception error) {
            System.out.println("ERROR " + error);
            error.printStackTrace();
            System.exit(0);
        }
    }
    
    public boolean frame(long dt)
    {
        return TweenManager.service( dt);        
    }
    
    
    // ---------------------------------------------
    public void mousePressed(MouseEvent e) { }   
    public void mouseReleased(MouseEvent e) { }    
    public void mouseClicked(MouseEvent e) { }    
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    
    // ---------------------------------------------
    public void mouseDragged(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }

}
