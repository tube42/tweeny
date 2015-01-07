package se.tube42.example.demo4;

import java.awt.*;
import java.awt.event.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** finish(Runnable) example */
public class Main
extends BaseWindow
implements MouseListener
{
    private static final int
          POINTS = 8,
          HEIGHT = 300,
          WIDTH = 1024 + 20;
          ;
    private static final float
          TIME_MOVE = 0.5f,
          TIME_MARK = 0.2f
          ;

    private Checkbox cb_slow;
    private PointItem point;
    private PointItem [] markers;
    private int cnt;

    public Main()
    {
        setTitle("finish(Runnable) test");

        // allow empty tweens for this demo
        TweenManager.allowEmptyTweens(true);

        // the markers are used to indicate that we have detected finish()
        this.markers = new PointItem[POINTS];
        for(int i = 0; i < POINTS; i++) {
            int x = 10 + (WIDTH - 20) * i / POINTS;
            markers[i] = new PointItem(Color.BLUE, x, HEIGHT * 3 / 5);
        }

        // this is the point that will tween across the screen
        this.point = new PointItem(Color.RED, 10, HEIGHT * 2 / 5);

        // set up UI
        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(cb_slow = new Checkbox("Slow down", false));

        canvas.addMouseListener(this);
        setSize(WIDTH, HEIGHT);
        start();
    }


    public void paintCanvas(Graphics g, int w, int h)
    {
        // clear screen
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        // draw help text
        g.setColor(Color.BLACK);
        g.drawString("Click to start the tweening", 10, HEIGHT * 1 / 5);

        // draw the items
        point.draw(g);
        for(int i = 0; i < POINTS; i++)
            markers[i].draw(g);

    }


    // ----------------------------------
    public void mousePressed(MouseEvent e)
    {
        // create a tweening acress the screen, for each point reached, add a finish() handler
        TweenNode tmp = point.set(PointItem.ITEM_X, 0).configure(0.01f, null);
        for(int i = 0; i < POINTS; i++) {
            final float x = markers[i].get(PointItem.ITEM_X);
            tmp = tmp.tail(x).configure(TIME_MOVE, null);
            tmp = tmp.finish( create(i));

        }
    }

    // create a finish handler for this
    // (in a real application, we would create POINT number of these at start up and re-use them)
    private Runnable create(final int k)
    {
        return new Runnable()
        {
            public void run()
            {
                markers[k].set(PointItem.ITEM_S, 2).configure(TIME_MARK, null).
                      tail(1).configure(TIME_MARK, null);
            }
        };
    }

    // modified frame funciton to allow slowing down animation
    public boolean frame(long dt)
    {
        if(cb_slow.getState()) dt /= 4;
        return super.frame(dt);
    }

    // -------------------------------------------

    public static void main(String []args)
    {
        new Main();
    }
}
