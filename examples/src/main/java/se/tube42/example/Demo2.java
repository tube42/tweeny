package se.tube42.example;

import java.awt.*;
import java.awt.event.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/** tail chaining */
public class Demo2
extends BaseWindow

implements MouseListener
{
    private static final int POINTS = 32;
    private PointItem point;
    private int [] points;
    private int cnt;

    public Demo2()
    {
        setTitle("Tail test");
        label.setText("Click left to add a point, right to start tweening");

        this.point = new PointItem(Color.RED, 100, 100);
        this.points = new int[POINTS * 2];
        this.cnt = 0;

        for(int i = 0; i < 6; i++, cnt++) {
            points[cnt*2+0] = 64 + i * 128;
            points[cnt*2+1] = 64 + (i & 1) * 128;
        }

        canvas.addMouseListener(this);
        setSize(800, 400);
        start();
    }


    public void paintCanvas(Graphics g, int w, int h)
    {
        // clear screen
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        // draw the lines
        int lx = 0, ly = 0;
        g.setColor(Color.BLACK);
        for(int i = 0; i < cnt; i++) {
            if(i != 0) g.drawLine(lx, ly, points[i*2], points[i*2+1]);
            lx = points[i*2];
            ly = points[i*2+1];
        }

        // draw the points
        g.setColor(Color.BLUE);
        for(int i = 0; i < cnt; i++)
            g.fillRect(points[i*2 + 0] - 12, points[i*2 + 1] - 12, 24, 24);

        // draw the items
        point.draw(g);
    }


    // ----------------------------------
    public synchronized void mousePressed(MouseEvent e)
    {
        if( e.getButton() != MouseEvent.BUTTON1) {
            if(cnt > 0) {
                // run the chain
                TweenNode tn1 = point.set(PointItem.ITEM_X, points[0]).
                      configure(0.1f, TweenEquation.QUAD_IN);
                TweenNode tn2 = point.set(PointItem.ITEM_Y, points[1]).
                      configure(0.1f, TweenEquation.QUAD_IN);

                for(int i = 1; i < cnt; i++){
                    tn1 = tn1.tail(points[i*2+0]).configure(0.3f, TweenEquation.QUAD_IN);
                    tn2 = tn2.tail(points[i*2+1]).configure(0.3f, TweenEquation.QUAD_IN);
                }
            }
        } else {
            // add a point
            points[cnt * 2 + 0] = e.getX();
            points[cnt * 2 + 1] = e.getY();
            cnt++;
            if(cnt >= POINTS) cnt = 0;
        }
    }
}
