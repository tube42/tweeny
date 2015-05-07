package se.tube42.example.demo6;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

/*
 * demo6: some examples for using TweenHelper to simplify your code
 */
public class Main extends BaseWindow implements ActionListener
{
    private PointItem [] points;
    private Button b1, b2, b3;
    
    public Main()
    {
        setTitle("demo6: TweenHelper");

        // allow empty tweens for this demo
        TweenManager.allowEmptyTweens(true);

        // the markers are used to indicate that we have detected finish()
        points = new PointItem[49];
        Random rnd = new Random();
        for(int i = 0; i < points.length; i++) {
            points[i] = new PointItem(
                new Color(rnd.nextInt()),
                40 + 64 * (i % 7), 40 + 64 * (i / 7));
            points[i].size = 60;
        }
        
        // set up UI
        Panel panel = new Panel();
        add(panel, BorderLayout.NORTH);
        panel.add(b1 = new Button("Random time"));
        panel.add(b2 = new Button("Random pause & time"));
        panel.add(b3 = new Button("Random value"));
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        
        setSize(30 + 64 * 7, 100 + 64 * 7);
        start();
    }

    // ----------------------------------

    public void paintCanvas(Graphics g, int w, int h)
    {
        // clear screen
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, w, h);

        // draw the items
        for(int i = 0; i < points.length; i++)
            points[i].draw(g);

    }

    // -------------------------------------------

    public void actionPerformed(ActionEvent ae)
    {
        final Object src = ae.getSource();
        if(src == b1) {
            // animate S from 0 to 1 with a random time between 0.1 and 0.8
            TweenHelper.animate(points, PointItem.ITEM_S, 0, 1f, 0.1f, 0.8f, 
                TweenEquation.BACK_OUT);
        } else if(src == b2) {
            // animate S from 0 to 1 with a random time between 0.1 and 0.8,
            // but first pause between 0.1 and 0.8
            TweenHelper.animate(points, PointItem.ITEM_S, 0, 1f, 
                0.1f, 0.8f, 0.1f, 0.8f, TweenEquation.BACK_OUT);
        } else if(src == b3) {
            // set S to a random value between 0.5 and 1.2
            TweenHelper.set(points, PointItem.ITEM_S, 0.5f, 1.2f);            
        }                   
        
    }
    // -------------------------------------------

    public static void main(String []args)
    {
        new Main();
    }
}
