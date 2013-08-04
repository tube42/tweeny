package se.tube42.example;

import java.awt.*;

import se.tube42.tweeny.*;


/**
 * This is just the canvas that does the drawing, nothing special here :(
 * 
 * All extra code is for double buffering to avoid flicker
 */

public class ExampleCanvas extends Canvas
{
    private ExampleItem [] items;
    
    // double buffering stuff
    private int w2, h2;
    private Image image;
    private Graphics g2;
    
    public ExampleCanvas(ExampleItem [] items)
    {
        this.items = items;
        this.image = null;
        this.g2 = null;
    }
    
    // this is needed to minimze the flickering :(
    public void update(Graphics g) 
    {
        paint(g);
    }
    
    public void paint(Graphics g) 
    {        
        final int w = getWidth(), h = getHeight();
        
        // have a working back buffer?
        if(image == null || w2 != w || h2 != h)
            create_buffer(w, h);
        
        // draw to back buffer: clear screen and draw each item
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, w, h);
        
        for(int i = 0; i < items.length; i++) 
            items[i].draw(g2);
        
        // draw back buffer to front buffer
        g.drawImage(image, 0, 0,this);        
    }
    
    // this will create a back buffer of the right size...
    private void create_buffer(int w, int h)
    {
        
        if(g2 != null) {
            g2.dispose();
            g2 = null;
        }
        
        if(image != null) {
            // image.dispose();
            image = null;
        }
        
        w2 = w;
        h2 = h;
        image = createImage(w, h);
        g2 = image.getGraphics();
    }
    
}
