

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
    // To avoid further problems, we also reset then when no tweens are queued
    private static long time_l;    
    private static float time_f;
    private static int items_cnt;    
    private static int items_pool_cnt;
    private static int items_new_cnt;    
    private static int nodes_pool_cnt;
    private static int nodes_new_cnt;
    
    private static ItemProperty [] items;
    private static ItemProperty [] items_pool;
    private static TweenNode [] nodes_pool;
   
    // dummy item, not animated. see addTween for usage
    private static ItemProperty ip_dummy = new ItemProperty();            
    private static boolean allow_empty = false;
    
   // ---------------------------------------------------------

    static {
    	reset();
    }

    /** reset manager state. note that this will reset all standing tweens
     * and leak memory */
    public static void reset()
    {
		time_l = 0;    
    	time_f = 0;
		items_cnt = items_pool_cnt = items_new_cnt = nodes_pool_cnt = nodes_new_cnt = 0;
    	items = new ItemProperty[64];
    	items_pool = new ItemProperty[64];
    	nodes_pool = new TweenNode[64];        
   		ip_dummy = new ItemProperty();
    }

    // ---------------------------------------------------------

    // TweenNode pool
    /* package */ final static TweenNode nodes_pool_get()
    {
        if(nodes_pool_cnt == 0) {
            nodes_new_cnt++;
            return new TweenNode();
        } else
            return nodes_pool[--nodes_pool_cnt];            
    }
    
    /* package */ final static void nodes_pool_put(TweenNode tn)
    {
        if(nodes_pool_cnt == nodes_pool.length)
            nodes_pool = Arrays.copyOf( nodes_pool, nodes_pool.length * 4);
        
        tn.reset();
        nodes_pool[nodes_pool_cnt++] = tn;
    }    
    
    
    // ---------------------------------------------------------
    // ItemProperty pool
    private final static ItemProperty items_pool_get()
    {       
        ItemProperty ip = null;        
        if(items_pool_cnt == 0) {
            items_new_cnt++;
            ip = new ItemProperty();
        } else {
            ip = items_pool[--items_pool_cnt];
        }
        
        // add it to pool if active items:
        if(items_cnt >=  items.length) 
            items = Arrays.copyOf( items, items.length * 4);            
        items[items_cnt++] = ip;              
        
        return ip;
    }
    
    private final static void items_pool_put(ItemProperty ip)
    {
        if(items_pool_cnt == items_pool.length)
            items_pool = Arrays.copyOf( items_pool, items_pool.length * 4);
        
        ip.reset();
        items_pool[items_pool_cnt++] = ip;        
    }    
    
    
    // ---------------------------------------------------------
    // misc. functions
    /**
     * When true, any tween or series of tweens that starts with 
     * an empty movement is (source and destination are equal) is ignored
     * <br>
     * <b>Important note:<b> if you set this to false, all tweens 
     * starting with a none-move or a pause will stop working!
     * @param allow allow empty tweens
     */
    public static void allowEmptyTweens(boolean allow)
    {
        allow_empty = allow;
    }
    
    
    /**
     * For debugging only!    
     * @return the number of currently active tweens
     */
    public static int debugCountActiveTweens()
    {
        return items_cnt;
    }
        
    /**
     * For debugging only!
     * @return tweens in the pool
     */
    public static int debugCountPoolTweens()
    {
        return items_pool_cnt;
    }
    
    /**
     * For debugging only!    
     * @return nodes in the pool
     */
    public static int debugCountPoolNodes()
    {
        return nodes_pool_cnt;
    }
    
    /**
     * For debugging only!
     * @return tweens allocated in total
     */
    public static int debugCountAllocatedTweens()
    {
        return items_new_cnt;
    }
    
    /**
     * For debugging only!    
     * @return nodes allocated in total
     */
    public static int debugCountAllocatedNodes()
    {
        return nodes_new_cnt;
    }
    
    // ---------------------------------------------------------
    // ItemProperty stuff    
    
    /* package */ static ItemProperty addTween(Item item, int index, 
              float v0, float v1)
    {
        return addTween(item, index, v0, v1, allow_empty);
    }
    
    /* package */ static ItemProperty addTween(Item item, int index, 
              float v0, float v1, boolean allow_empty)
    {
        
        // no movement at all? don't add it
        if(!allow_empty && v0 == v1) {
            item.setImmediate(index, v1);         
            return ip_dummy; // instead of NULL :(
        }
        
        
        // remove possible old tweens
        ItemProperty ip = item.properties[index];
        
        if(ip == null) {
            ip = item.properties[index] = items_pool_get();                        
        } else {
            ip.removeTails();
            ip.removeCallbacks();
        }
        
        ip.set(item, index, v0, v1);
        ip.time_start = time_f;
        ip.active = true;                
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
    }
    
    /* package */ static void removeTween(ItemProperty ip, boolean finish)
    {
        if(finish)
            ip.item.data[ip.index] = ip.getChainFinalValue();
        ip.removeTails();  
        ip.active = false;
    }
     
    
    
    /* package */ static void removeTween(Item item, int index, boolean finish)
    {
        final ItemProperty ip = item.properties[index];
        if(ip != null)
            removeTween(ip, finish);
    }
    
    
    /**
     * service the tweens for this frame.
     * returns false when tween queue is empty
     *      
     * @param delta_time is frame time in milliseconds
     * @return false if there is nothing to do (i.e. no tweens were active)
     */
    
    public static boolean service(long delta_time)
    {        
        // this sanity check will help removing some error vectors later on
        if(delta_time <= 0) return items_cnt > 0;
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
                boolean ended = false;
                if(dt >= ip.duration) {
                    dt = ip.duration;
                    ended = true;
                }
                
                ip.update(ip.duration_inv * dt);                                

                if(ended) {
                    final Runnable r = ip.on_end_r;
                    final TweenListener tl = ip.on_end_l;
                    final int msg = ip.on_end_m;
                    final int index = ip.index;
                    final Item item = ip.item;
                    
                    if(!ip.processTail())
                        ip.active = false; // really ended
                    else
                        ip.time_start = time_f; // tails exist, started a new node
                    
                    // run runnable
                    if(r != null) {
                        try {
                            r.run();
                         } catch(Exception ignored) { }
                    }
                    
                    // run listener
                    if(tl != null) {
                        try {
                            tl.onFinish(item, index, msg);
                        } catch(Exception ignored) { }
                    }
                }
            }
            
            if(!ip.active)
                items_pool_put(ip);            
            else
                w0++;
        }
        
        // did we create new tweens while in the loop above?
        // in that case include them in our list
        if(items_cnt != items_len) {
            active = true;
            for(int r0 = items_len; r0 < items_cnt; r0++) {
                if(w0 != r0) items[w0] = items[r0];
                w0++;
            }                
        }
        
        
        items_cnt = w0;
        active |= items_cnt != 0;        
                
        // lets restart the counter, this will help us avoid FP problems
        if(items_cnt == 0) {
            time_l = 0;
            time_f = 0;
        }
        
        return active;
    }
}
