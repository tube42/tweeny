
package se.tube42.tweeny;

/**
 * Tween manager, handles the frame movement
 */
public final class TweenManager
{
    private static float time = 0;    
    private static int count = 0;    
    private static ItemProperty [] active_items = new ItemProperty[64];
    
    private static void grow()
    {
        final int old_size = active_items.length;
        final int new_size = old_size * 2 + 2;
        ItemProperty [] new_items = new ItemProperty[new_size];
        for(int i = 0; i < old_size; i++) 
            new_items[i] = active_items[i];
        active_items = new_items;
    }
    
    public static float getTime()
    {
        return time;
    }
    
    /* package */ static void add(ItemProperty ip)
    {
        // do this before active check!        
        ip.time_start = time; 
        ip.vc = ip.v0;
        
        if(ip.active) return;
                
        if(count >=  active_items.length)
            grow();
        
        ip.active = true;
        active_items[count++] = ip;                
    }
    
   
    /* package */ static void remove(ItemProperty ip)
    {
        if(!ip.active) return;        
        ip.active = false;  // will be removed in the service loop
    }
       
    /** remove all currently active tweens.
     * if finish is true it will finish the movement, otherwise just
     * drop it where it is now */
    public static void removeTweens(boolean finish)
    {
        for(int i = 0; i < count; i++)
            active_items[i].removeTween(finish);
        count = 0;
    }
    
    /**
     * service the tweens for this frame.
     * returns false when tween queue is empty
     */
    
    public static boolean service(float delta_time)
    {
        final int len = count;
           
        /* this sanity check will help removing some error vectors later on */
        if(delta_time <= 0) return len == 0;
        time += delta_time;
           
        int w0 = 0;                           
        for(int r0 = 0; r0 < len; r0++) {
            final ItemProperty ip = active_items[r0];
            
            if(w0 != r0) active_items[w0] = ip;
            
            if(ip.active) {
                final float dt = time - ip.time_start;
                ip.flags |= ItemProperty.FLAGS_CHANGED;
                if(dt >= ip.duration) {
                    ip.vc = ip.v0 + ip.vd;
                    ip.active = false;
                } else {
                    w0++;
                    ip.vc = ip.v0 + ip.vd * ip.equation.compute(ip.duration_inv * dt);
                }                   
            } 
        }
        count = w0;
        
        return count != 0;
    }
}
