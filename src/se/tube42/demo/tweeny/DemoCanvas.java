package se.tube42.demo.tweeny;

import java.awt.*;

import se.tube42.lib.tweeny.*;


public class DemoCanvas extends Canvas
{
    private BoxItem [] items;
    
    // double buffering stuff
    private int w2, h2;
    private Graphics g2;
    private Image image;
    
    public DemoCanvas(BoxItem [] items)
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
        
        // draw all items
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
