
package se.tube42.lib.tweeny;

/**
 * Tween manager, handles the frame movement. 
 */
public final class TweenManager
{
    // Optimally, we want time as a float so we don't need to do a FP division by 1000.0,
    // but as the float time grows, addition with small numbers will stop working.
    // Hence we track time with a long and convert it to a float.
    // To avoid further problems, we also reset then when no tweens or animations are queued
    private static long time_l = 0;    
    private static float time_f = 0;
    private static int items_cnt = 0;    
    private static ItemProperty [] items = new ItemProperty[64];
    
    private static int animations_cnt = 0;
    private static Animation [] animations = new Animation[16];
       
    // ---------------------------------------------------------
    // Animation stuff
    
    private static void grow_animations()
    {
        final int old_size = animations.length;
        final int new_size = old_size * 2 + 2;
        Animation [] new_animations = new Animation[new_size];
        for(int i = 0; i < old_size; i++) 
            new_animations[i] = animations[i];
        animations = new_animations;
    }    
    
    /* packate */ static final void add(Animation anim)
    {
        anim.reset();        
        if(anim.active) return; // already in queue
        
        if(animations_cnt >=  animations.length)
            grow_animations();
        
        anim.active = true;
        animations[animations_cnt++] = anim;
    }
    
    /* packate */ static final void remove(Animation anim)
    {
        anim.active = false;
    }
    
    /** 
     * remove all currently active animations.
     */
    public static void removeAnimations()
    {
        for(int i = 0; i < animations_cnt; i++)
            animations[i].stop();
        animations_cnt = 0;
    }
    
    
    // ---------------------------------------------------------
    // ItemProperty stuff    
    private static void grow_items()
    {
        final int old_size = items.length;
        final int new_size = old_size * 2 + 2;
        ItemProperty [] new_items = new ItemProperty[new_size];
        for(int i = 0; i < old_size; i++) 
            new_items[i] = items[i];
        items = new_items;
    }
    
    /* package */ static void add(ItemProperty ip)
    {
        // do this before active check!        
        ip.time_start = time_f;
        ip.vc = ip.v0;
        
        if(ip.active) return;
                
        if(items_cnt >=  items.length)
            grow_items();
        
        ip.active = true;
        items[items_cnt++] = ip;  
    }
    
   
    /* package */ static void remove(ItemProperty ip)
    {
        ip.active = false;  // will be removed in the service loop
    }
       
    /** 
     * remove all currently active tweens.
     * if finish is true it will finish the movement, otherwise just
     * drop it where it is now */
    public static void removeTweens(boolean finish)
    {
        for(int i = 0; i < items_cnt; i++)
            items[i].removeTween(finish);
        items_cnt = 0;
    }
    
    /**
     * service the tweens for this frame.
     * returns false when tween queue is empty
     * 
     * delta_time is frame time in milliseconds
     */
    
    public static boolean service(long delta_time)
    {        
        // this sanity check will help removing some error vectors later on
        if(delta_time <= 0) return (items_cnt + animations_cnt) > 0;
        time_l += delta_time;        
        time_f = time_l / 1000f;
        
        boolean active = false;
        
        // service items
        final int items_len = items_cnt;        
        int w0 = 0;                           
        for(int r0 = 0; r0 < items_len; r0++) {
            final ItemProperty ip = items[r0];
            
            if(w0 != r0) items[w0] = ip;
            
            if(ip.active) {
                // final float dt = (time - ip.time_start) / 1000f;
                final float dt = time_f - ip.time_start;
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
        items_cnt = w0;        
        active |= items_cnt != 0;
        
        
        // service animations:
        final int animations_len = animations_cnt;                
        w0 = 0;                           
        for(int r0 = 0; r0 < animations_len; r0++) {
            final Animation an = animations[r0];            
            if(w0 != r0) animations[w0] = an;
            
            if(an.active) {
                active |= an.service(delta_time);
                
                if(!an.active && an.on_finish != null)
                    an.on_finish.run();
            }
            if(an.active)
                w0++;           
        }
        animations_cnt = w0;        
        
        
        // lets restart the counter, this will help us avoid FP problems
        if(animations_cnt + items_cnt == 0) {
            time_l = 0;
            time_f = 0;
        }
        
        return active;
    }
}
