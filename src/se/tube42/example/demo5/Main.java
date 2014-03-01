package se.tube42.example.demo5;

import java.awt.*;
import java.awt.event.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** finish(TweenListener) example */
public class Main 
extends BaseWindow 
implements TweenListener

{
    private static final int 
          HEIGHT = 400,
          WIDTH = 400 + 40,
          BW = 128,
          BH = 128
          ;
    
    private Checkbox cb_slow;  
    private BoxItem [] boxes;
    
    public Main()
    {                
        setTitle("finish(TweenListener) test");
        
        // allow empty tweens for this demo
        TweenManager.allowEmptyTweens(true);
        
        // the markers are used to indicate that we have detected finish()
        boxes = new BoxItem[3];
        for(int i = 0; i < boxes.length; i++) {
            boxes[i] = new BoxItem(Color.BLACK, 
                     (BW  + 5) * i + 10, HEIGHT / 2 - BH);
            boxes[i].w = BW;
            boxes[i].h = BH;
        }
        
        // set up UI
        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(cb_slow = new Checkbox("Slow down", false));
                
        canvas.addMouseListener(this);
        setSize(WIDTH, HEIGHT);
        start();     
    }
    
        
    // ----------------------------------  
    
    
    /**
     * this is the function that is called when those tween sequences finish.
     * In a game, you could use these for example to animate an sprite by
     * moving to the next frame each time onFinish() is called
     * 
     * We don't have anything useful in this demo, so we simply set the box color
     * here. This creates the illusion of a rectangle with different front and
     * back colors.
     */
    
    public void onFinish(Item it, int index, int msg)
    {
        int c = msg & 0xFF;
        int i = (msg >> 8) % 3;
        
        switch(i) {
        case 0:
            boxes[0].c = new Color( c, 0, 0);
            break;
            
        case 1:
            boxes[1].c = new Color( 0, c, 0);
            break;
            
        case 2:
            boxes[2].c = new Color( 0, 0, c);
            break;            
        }
    }
    
    // ----------------------------------  
    
    public void mousePressed(MouseEvent e)
    {                              
        for(int i = 0; i < boxes.length; i++) {            
            boxes[i].pause(BoxItem.ITEM_SW, 1, 0.1f + i * 0.05f)
                  // turn 1
                  .tail(0).configure(0.3f, TweenEquation.SIN_OUT).finish(this, 0xFF | (i << 8))
                  .tail(1).configure(0.3f, TweenEquation.SIN_IN)
                  
                  // delay in the middle
                  .pause(1)                  
                  
                  // turn 2
                  .tail(0).configure(0.3f, TweenEquation.SIN_OUT).finish(this, 0x00 | (i << 8))
                  .tail(1).configure(0.4f, TweenEquation.SIN_IN)
                  ;
                  
        }
    }
    
    // modified frame funciton to allow slowing down animation
    public boolean frame(long dt)
    {
        if(cb_slow.getState()) dt /= 4;        
        return super.frame(dt);
    }
    
    public void paintCanvas(Graphics g, int w, int h)
    {
        // clear screen
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);
        
        // draw help text
        g.setColor(Color.RED);
        g.drawString("Click to start the tweening", 20, 30);

        // draw the items
        for(int i = 0; i < boxes.length; i++)
            boxes[i].draw(g);

    }
    
    // -------------------------------------------
    
    public static void main(String []args)
    {
        new Main();
    }
}
