package se.tube42.example.common;

import java.awt.*;

import se.tube42.lib.tweeny.*;

/** double buffered canvas, for your flicker free enjoyment */
public class DBCanvas extends Canvas
{
    private int w, h;
    private Graphics g;
    private Image image;
    private BaseWindow bw;
    
    public DBCanvas(BaseWindow bw)
    {
        this.bw = bw;
        this.image = null;
        this.g = null;
        this.w = -1;
        this.h = -1;
    }
    
    public final void update(Graphics g)
    {
        paint(g);
    }
    
    public final void paint(Graphics g_)
    {
        final int w_ = getWidth();
        final int h_ = getHeight();
        
        if(image == null || w_ > w || h_ > h)
            create_buffer(w_, h_);
        
        bw.paintCanvas(g, w_, h_);
        
        // ADD SOME STATS:
        g.setColor(Color.BLACK);
        g.drawString(
                  "Active tweens/animations: " + 
                  TweenManager.debugCountActiveTweens() + "/" +
                  TweenManager.debugCountActiveAnimations() + 
                  ", tweens/nodes in pool: " +
                  TweenManager.debugCountPoolTweens() + "/" +
                  TweenManager.debugCountPoolNodes(),                
                  10, h_ - 10);
        
        
        g_.drawImage(image, 0, 0, w_, h_, 0, 0, w_, h_, this);
    }
    
    
    private void create_buffer(int w, int h)
    {
        
        // give it some extra space (in case resized)
        w += 64;
        h += 64;
        
        if(g != null) {
            g.dispose();
            g = null;
        }
        
        if(image != null) {
            // image.dispose();
            image = null;
        }
        
        this.w = w;
        this.h = h;
        this.image = createImage(w, h);
        this.g = image.getGraphics();
    }    
}
