package se.tube42.example.demo3;

import java.awt.*;

import se.tube42.lib.tweeny.*;


/**
 * Equation graph
 */

public class EquationCanvas extends Canvas
{
    private TweenEquation eq;
    
    public EquationCanvas(TweenEquation eq)
    {
        this.eq = eq;
    }
    
    
    public void update(Graphics g) 
    {        
        paint(g);
    }    
    
    public void paint(Graphics g) 
    {        
        final int w = getWidth(), h = getHeight();
        final int s = (int)(Math.min(w, h) * 0.7 - 10);
        
        int x0 = (w - s) / 2;
        int y0 = (h + s) / 2;
        
        // draw to back buffer: clear screen and draw each item
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        g.setColor(Color.RED);        
        g.drawLine(x0-10, y0, x0 + s, y0);
        g.drawLine(x0, y0+10, x0, y0 - s);
        
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, w, h);
        g.drawString(eq.toString(), 20, 20);
                
        final int COUNT = 50;
        float x_old = 0, y_old = 0;
        
        for(int i = 0; i <= COUNT; i++) {
            float x_new = i / (float) COUNT;
            float y_new = eq.compute(x_new);
            
            if(i != 0)
                g.drawLine( 
                          x0 + (int) (x_old * s),
                          y0 - (int) (y_old * s),
                          x0 + (int) (x_new * s),
                          y0 - (int) (y_new * s)
                          );
            x_old = x_new;
            y_old = y_new;
        }
        
    }
}
