package se.tube42.example.common;

import java.awt.*;
import java.util.*;

import se.tube42.lib.tweeny.*;


public class PointItem extends Item
{
    public static final int
          SIZE = 12
          ;

    public static final int
          ITEM_X = 0,
          ITEM_Y = 1,
          ITEM_S = 2
          ;

    // ---------------------------------------------

    // these are not tweened
    public Color c;
    public int size = SIZE;

    public PointItem(Color c, int x, int y)
    {
        super(3);

        this.c = c;
        setImmediate(ITEM_X, x);
        setImmediate(ITEM_Y, y);
        setImmediate(ITEM_S, 1);
    }

    public void draw(Graphics g)
    {
        final float s = size * get(ITEM_S);
        final float x = get(ITEM_X);
        final float y = get(ITEM_Y);

        g.setColor(c);

        // g.fillOval((int)x, (int)y, (int)size, (int)size);
        g.fillOval(
                  (int)(x - s / 2),
                  (int)(y - s / 2),
                  (int)s,
                  (int)s
                  );

    }
}
