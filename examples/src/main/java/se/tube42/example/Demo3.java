package se.tube42.example;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;



/**
 * Equation graph
 */

class EquationCanvas extends Canvas
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




/**
 * this is an example Item where position = (x, y) is tweened
 */

class ExampleItem extends Item
{
    private final static Random rnd = new Random();

    /**
     * These are our item properties
     */
    public static final int
          ITEM_X = 0,
          ITEM_Y = 1
          ;

    // these are not tweened
    public int w, h;
    public Color c;
    private String eq_name;

    private float duration;
    private TweenEquation eq;

    public ExampleItem()
    {
        super(2);

        eq_name = "?";
        c = new Color( rnd.nextInt() & 0xFF, rnd.nextInt() & 0xFF, rnd.nextInt() & 0xFF);

        setPositionDuration(1f);
        setPositionEquation(TweenEquation.LINEAR);

        setImmediate(ITEM_X, 0);
        setImmediate(ITEM_Y, 0);

    }

    public void draw(Graphics g)
    {
        final int x = (int)(0.5f + getX());
        final int y = (int)(0.5f + getY());

        g.setColor(c);
        g.fillRect( x, y, w, h);

        g.setColor(Color.BLACK);
        g.drawString(eq_name,  32, y + 18);

    }

    public void setSize(float w, float h)
    {
        this.w = (int) w;
        this.h = (int) h;
    }

    // -------------------------------------------
    // helper functions for position tweening with Item
    public void setPosition(float x, float y)
    {
        set(ITEM_X, x).configure(duration, eq);
        set(ITEM_Y, y).configure(duration, eq);
    }

    public float getX() { return get(ITEM_X); }
    public float getY() { return get(ITEM_Y); }


    public void setPositionDuration(float t)
    {
        this.duration = t;
    }

    public void setPositionEquation(TweenEquation eq)
    {
        this.eq = eq;
        eq_name = eq.toString();
    }
}

/**
 * this class demonstrates some tweeny functions
 */
public class Demo3 extends BaseWindow
{
    private static final Random rnd = new Random();

    private final TweenEquation [] eqs = {
        TweenEquation.LINEAR,
        TweenEquation.DELAYED,
        TweenEquation.DISCRETE,
        TweenEquation.QUAD_IN,
        TweenEquation.QUAD_OUT,
        TweenEquation.QUAD_INOUT,
        TweenEquation.CUBE_IN,
        TweenEquation.CUBE_OUT,
        TweenEquation.SIN_IN,
        TweenEquation.SIN_OUT,
        TweenEquation.BACK_IN,
        TweenEquation.BACK_OUT,
        TweenEquation.TUBE42_1,
        TweenEquation.TUBE42_2,
        TweenEquation.ELASTIC_IN,
        TweenEquation.ELASTIC_OUT
    };

    private Checkbox slowdown, allow_empty;
    private ExampleItem [] items;

    public Demo3()
    {
        setTitle("Ease equations");

        final int n = (int)Math.ceil(Math.sqrt( eqs.length));

        // The graph window...
        Frame f = new Frame();
        f.setLayout(new GridLayout(n, n));
        for(int i = 0; i < eqs.length; i++)
            f.add( new EquationCanvas(eqs[i]));

        f.setSize(100, 100);
        f.setSize(84 * n, 84 * n);
        f.setVisible(true);

        // -------------------------------------
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT));
        add(p, BorderLayout.NORTH);
        p.add(slowdown = new Checkbox("Slow down", false));
        p.add(new Label("    "));
        p.add( allow_empty = new Checkbox("Allow empty tweens", true));
        // create two items
        items = new ExampleItem[ eqs.length];
        for(int i = 0; i < items.length; i++) {
            ExampleItem it = items[i] = new ExampleItem();
            it.setPosition(132, 12 + i * 40);
            it.setSize(33, 33);
            it.setPositionEquation(eqs[i]);
        }
        TweenManager.removeTweens(true); // commit all tweens


        // create a bunch of animation to show before we start
        TweenNode tmp = null;
        for(int i = 0; i < items.length; i++) {
            final ExampleItem ei = items[i];
            final float t = 0.3f + 0.05f * (rnd.nextInt() & 7);

            final float y1 = i * 40 + 12;
            final float y2 = ((i + 4) % items.length) * 40 + 12;

            tmp = ei.pause(ExampleItem.ITEM_Y, -100, 2f)
                  .tail(y2).configure(t, TweenEquation.BACK_OUT)
                  .pause(1f)
                  .tail(y1).configure(1, TweenEquation.BACK_IN);
        }

        // mark start and end of initial animation
        System.out.println("FYI: starting the initial animation");

        tmp.pause(0.5f).finish(new Runnable() {
                  public void run(){
                  System.out.println("FYI: animation ended. Reseting equations");
                  for(int i = 0; i < items.length; i++) items[i].setPositionEquation(eqs[i]);
              }
              });


        setSize(540, 800);
        start();
    }

    private boolean forward = false;
    private int speed = 0;

    int x = 0;
    public synchronized boolean frame(long dt)
    {
        // allow empty tweens?
        TweenManager.allowEmptyTweens(allow_empty.getState());

        // possibly slow down and run a frame
        if(slowdown.getState())
            dt = Math.max(1, dt / 4);
        boolean ret = super.frame(dt);

        // nothing to tween? move some stuff
        if(!ret) {
            forward = !forward;
            int x = forward ? canvas.getWidth() - 80 - 32 : 132;
            for(int i = 0; i < items.length; i++) {
                items[i].setPosition(x, 12 + i * 40);
            }

            // change the speed
            if(!forward) {
                final float t = 0.5f + speed * 0.5f;
                speed = (speed + 1) & 7;
                setTitle("[Tweeny test] time=" + t + (slowdown.getState() ? " (slowed down)" : "") );

                for(int i = 0; i < items.length; i++)
                    items[i].setPositionDuration(t);
            }
        }

        return ret;
    }

    public void paintCanvas(Graphics g, int w, int h)
    {

        // draw to back buffer: clear screen and draw each item
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        // draw the line representing current time:
        final int x = (int)(0.5f + items[0].getX()) + items[0].w / 2;
        g.setColor(items[0].c);
        g.drawLine(x, 0, x, h);

        // draw all items
        for(int i = 0; i < items.length; i++)
           items[i].draw(g);

    }
}

