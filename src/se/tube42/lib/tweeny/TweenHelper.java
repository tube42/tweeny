

package se.tube42.lib.tweeny;

import java.util.*;


/**
 * Helper class for common user code
 */
public class TweenHelper
{
    
    // random numbers
    private final static Random r = new Random();
    private static float random(float min, float max)
    {
        final float d = max - min;
        return min + d * r.nextFloat();
    }
    
    /**
     * animate array members with random time
     */
    public static void animate(Item [] items, int property, 
        float vstart, float vend, float tmin, float tmax,
        TweenEquation eq)
    {
        for(int i = 0; i < items.length; i++) {
            final float t = random(tmin, tmax);
            items[i].set(property, vstart, vend).configure(t, eq);
        }
    }

    /**
     * animate array members with random time and initial pause
     */    
    public static void animate(Item [] items, int property, 
        float vstart, float vend, float tmin, float tmax,
        float pmin, float pmax, TweenEquation eq)
    {
        for(int i = 0; i < items.length; i++) {
            final float p = random(pmin, pmax);
            final float t = random(tmin, tmax);
            items[i].pause(property, vstart, p)
                .tail(vend).configure(t, eq);
        }
    }
}