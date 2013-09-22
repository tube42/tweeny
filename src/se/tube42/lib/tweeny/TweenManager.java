
package se.tube42.lib.tweeny;

import java.util.*;
import java.lang.reflect.Array;


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
    private static int items_pool_cnt = 0;
    private static ItemProperty [] items = new ItemProperty[64];
    private static ItemProperty [] items_pool = new ItemProperty[64];
    private static int animations_cnt = 0;
    private static Animation [] animations = new Animation[8];
    
    
    // dummy item, not animated. see addTween for usage
    private static ItemProperty ip_dummy = new ItemProperty();    
    
    
    // ---------------------------------------------------------
    // ItemProperty pool
    private final static ItemProperty items_pool_get()
    {
        return (items_pool_cnt == 0) ? 
             new ItemProperty() : items_pool[--items_pool_cnt];
    }
    
    private final static void items_pool_put(ItemProperty ip)
    {
        if(items_pool_cnt == items_pool.length)
            items_pool = Arrays.copyOf( items_pool, items_pool.length * 4);
        
        ip.reset();
        items_pool[items_pool_cnt++] = ip;
    }    
    
    
    // ---------------------------------------------------------
    // Animation stuff
    
    /* packate */ static final void add(Animation anim)
    {
        anim.reset();        
        if(anim.active) return; // already in queue
        
        if(animations_cnt >= animations.length) {
            animations = Arrays.copyOf( animations, animations.length * 4);
        }
        
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
        
    /* package */ static ItemProperty addTween(Item item, int index, 
              float v0, float v1)
    {
        
        // no movement at all? don't add it
        if(v0 == v1) {
            item.setImmediate(index, v1);            
            return ip_dummy; // instead of NULL :(
        }
        
        
        // remove possible old tweens
        ItemProperty ip = item.properties[index];
        
        if(ip == null) 
            ip = item.properties[index] = items_pool_get();
            
        
        ip.set(item, index, v0, v1);
        ip.time_start = time_f;
                
        if(!ip.active) {
            ip.active = true;
            if(items_cnt >=  items.length) {
                items = Arrays.copyOf( items, items.length * 4);
            }
            items[items_cnt++] = ip;  
        }
        
        return ip;        
    }
    
   
       
    /** 
     * remove all currently active tweens.
     * if finish is true it will finish the movement, otherwise just
     * drop it where it is now */
    public static void removeTweens(boolean finish)
    {
        for(int i = 0; i < items_cnt; i++)
            removeTween(items[i], finish);
        items_cnt = 0;
    }
    
    public static void removeTween(ItemProperty ip, boolean finish)
    {
        ip.active = false;            
        if(finish)
            ip.item.data[ip.index] = ip.v1;                    
    }
    
  
    public static void removeTween(Item item, int index, boolean finish)
    {
        final ItemProperty ip = item.properties[index];
        if(ip != null)
            removeTween(ip, finish);
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
                float dt = time_f - ip.time_start;
                if(dt >= ip.duration) {
                    ip.active = false;
                    dt = ip.duration;
                }
                
                ip.update(ip.duration_inv * dt);
                
            }
            
            if(!ip.active)
                items_pool_put(ip);            
            else
                w0++;
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
