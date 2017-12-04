package se.tube42.example;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** basic tweening demo */
public class Demo1
extends BaseWindow
{
    private BoxItem [] items;
    private int state;

    private Checkbox cb_anim, cb_slow, cb_empty;

    public Demo1()
    {
        setTitle("Tween and animation demo");
        label.setText("Press a box to start multiple tweenings!");
        
        this.state = 0;
        this.items = new BoxItem[3];
        this.items[0] = new BoxItem(Color.RED, 210, 210);
        this.items[1] = new BoxItem(Color.BLUE, 20, 20);
        this.items[2] = new BoxItem(Color.GREEN, 400, 20);

        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(cb_anim = new Checkbox("Animate", true));
        panel.add(cb_slow = new Checkbox("Slow down", false));
        panel.add(cb_empty = new Checkbox("Allow empty tweens", true));

        canvas.addMouseListener(this);

        setSize(500, 600);
        start();
    }

    // ------------------------------------
    // animators

    private void anim_press(BoxItem bi)
    {
        bi.set(BoxItem.ITEM_SW, 1f, 0.9f).configure(0.1f, TweenEquation.QUAD_IN)
              .tail(1.1f).configure(0.1f, TweenEquation.QUAD_IN)
              .tail(1.0f).configure(0.2f, TweenEquation.QUAD_IN);

        bi.set(BoxItem.ITEM_SH, 1f, 0.9f).configure(0.1f, TweenEquation.QUAD_IN)
              .tail(1.1f).configure(0.1f, TweenEquation.QUAD_IN)
              .tail(1.0f).configure(0.2f, TweenEquation.QUAD_IN);
    }

    private void anim_stop(BoxItem bi)
    {
        bi.set(BoxItem.ITEM_SH, 1f, 0.95f).configure(0.05f, TweenEquation.QUAD_IN)
              .tail(1.0f).configure(0.2f, TweenEquation.QUAD_IN);
    }

    // ------------------------------------

    public void paintCanvas(Graphics g, int w, int h)
    {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        for(int i = 0; i < items.length; i++)
            items[i].draw(g);
    }



    public boolean frame(long dt)
    {
        TweenManager.allowEmptyTweens(cb_empty.getState());

        if(!cb_anim.getState()) dt = 10000;
        else if(cb_slow.getState()) dt /= 4;

        return super.frame(dt);
    }

    // ----------------------------------

    public synchronized void mousePressed(MouseEvent e)
    {
        final int x = e.getX();
        final int y = e.getY();

        for(int i = 0; i < 3; i++) {
            if(items[i].hit(x, y)) {
                anim_press(items[i]);
                break;
            }
        }
    }

    public synchronized void mouseReleased(MouseEvent e)
    {

        final int x = e.getX();
        final int y = e.getY();

        if(items[0].hit(x, y)) {
            state = (state + 1) & 3;
            switch(state) {
            case 0:
                items[1].setPosition( 20 , 20);
                items[2].setPosition( 400, 20);
                break;
            case 1:
                items[1].setPosition( 20 , 400);
                items[2].setPosition( 400, 400);
                break;
            case 2:
                items[1].setPosition( 400, 400);
                items[2].setPosition( 20 , 400);
                break;
            case 3:
                items[1].setPosition( 400,  20);
                items[2].setPosition( 20 ,  20);
                break;
            }
        }

        if(items[1].hit(x, y)) {
            float y1 = items[0].getY() - 20;
            if(y1 < 20) {
                anim_stop(items[0]);
                y1= 20;
            }
            items[0].setPosition(items[0].getX(), y1);
        }

        if(items[2].hit(x, y)) {
            float y1 = items[0].getY() + 20;
            if(y1 > 400) {
                anim_stop(items[0]);
                y1 = 400;
            }
            items[0].setPosition(items[0].getX(), y1);
        }        
    }
}
