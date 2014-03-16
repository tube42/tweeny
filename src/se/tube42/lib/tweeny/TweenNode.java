
package se.tube42.lib.tweeny;

import java.util.*;

/**
 * this class represents the first node in a list of tweens
 */

/* package */ final class ItemProperty extends TweenNode
{
    public boolean active;
    public float v0, vd, time_start, duration_inv;
    
    public Item item;
    public int index;
    
    public final void reset()
    {           
        if(item != null && item.properties[index] == this) {
            item.properties[index] = null;            
        }
        this.active = false;
        this.item = null;
        
        removeTails();
        super.reset();                
    }
    
    /* package */ final void set(Item item, int index, float v0, float v1)
    {
        this.item = item;
        this.index = index;
        
        this.v0 = v0;
        set(v1);        
    }
    
    /* package */ final void set(float v1)
    {
        this.v1 = v1;
        this.vd = v1 - v0;        
    }
    
    
    /* package */ final void update(float d)
    {
        final float d2 = equation.compute(d);
        item.data[index] = v0 + vd * d2;
    }
    
    /** 
     * get tween time (assuimng ideal clock)
     * @return total time required by this tween and its tails.
     */    
    /* package */ final float getTime()
    {
        TweenNode tmp = this;        
        float t = 0;
        
        while(tmp != null) {
            t += tmp.duration;
            tmp = tmp.tail;
        }
        return t;        
    }
    
    public final TweenNode configure(float duration, TweenEquation equation)
    {
        super.configure(duration, equation);
        this.duration_inv = 1f / this.duration;        
        return this;
    }

    public boolean processTail()
    {
        if(tail == null)
            return false;
        
        final TweenNode tmp = tail;
        this.tail = tmp.tail;
        
        this.on_end_r = tmp.on_end_r;
        this.on_end_l = tmp.on_end_l;
        this.on_end_m = tmp.on_end_m;
        
        // install this tail as the current target
        this.v0 = this.v1;
        set(tmp.v1);
        configure(tmp.duration, tmp.equation);
        
        TweenManager.nodes_pool_put(tmp);
        return true;
    }
}

/**
 * This represents a key, i.e. tween target point
 */

public class TweenNode
{
    /* package */ float v1;
    /* package */ float duration;
    /* package */ Runnable on_end_r;
    /* package */ TweenListener on_end_l;
    /* package */ int on_end_m;
    
    protected TweenEquation equation;
    protected TweenNode tail;
    
    public TweenNode()
    {
        reset();
    }
    
    /** 
     * configure a tween with these parameters
     * @param duration sets tween time
     * @param equation sets tween equation if not null 
     */
    public TweenNode configure(float duration, TweenEquation equation)
    {
        this.duration = Math.max(0.0001f, duration);
        if(equation != null) 
            this.equation = equation;
        return this;
    }
    
    public TweenNode finish(Runnable r)
    {
        this.on_end_r = r;
        return this;
    }
    
    public TweenNode finish(TweenListener tl, int msg)
    {
        this.on_end_l = tl;
        this.on_end_m = msg;
        return this;
    }
    
    /** 
     * Add a tail to this tween with a new value point. To change other param, use configure()
     * @param value the value to tween to
     */
    public final TweenNode tail(float value)
    {        
        // already have one?
        if(tail != null)
            return tail;
        
        TweenNode tmp = TweenManager.nodes_pool_get();
        tmp.v1 = value;
        this.tail = tmp;
        
        return tmp;
    }
    
    /** 
     * Add a pause after this tween       
     * @param time the pause time
     */
    public final TweenNode pause(float time)
    {        
        return tail(this.v1).configure(time, null);
    }
    
    // -------------------------------------
    /* package */ void reset()
    {   
        configure(1f, TweenEquation.LINEAR);
        removeCallbacks();		
        this.tail = null;        
    }    
    
    /* package */ void removeCallbacks()
    {
        this.on_end_r = null;
        this.on_end_l = null;
    }
    
    /* package */ void removeTails()
    {
    	if(tail != null) {
    		tail.removeTails();
    		TweenManager.nodes_pool_put(tail);		
			tail = null;
    	}
    }

    /* package */ float getChainFinalValue()
    {
        TweenNode t = this;
        while(t.tail != null)
            t = t.tail;
        return t.v1;
    }
    
}
