
package se.tube42.lib.tweeny;

/** 
 * an animation is a set of tweens on a set of properties.
 * use an animation if you have a complex sequence of tweening 
 * but beware that creating animation is slightly expensive
 */

public final class Animation 
{
    // the animation class is a little bit weird. our reason for that is
    // that we wanted to avoid allocating too many small object hence
    // everything is packed into a few large arrays
    
    /* package */ Item [] items;
    /* package */ int [] indices;
    /* package */ int [] data;        // |time0|count0|mem0|dur0|mem1|dur1|...|time1|count1|
    /* package */ float [] value;       // |init0|init1|... |val0|val1|..
    /* package */ TweenEquation [] eqs;  // |eq0|eq1|...
    /* package */ boolean active;
    /* package */ Runnable on_finish;     // run this when we are done
    
    private int max_kf, max_ips;
    private int cnt_data, cnt_val, cnt_eqs, cnt_kf;
    private int curr_time, next_time;
    
    /* package */ Animation(Item [] items, int [] indices, int ips_count, int kf_count, int val_count)
    {
        this.items = items;
        this.indices = indices;
        this.max_ips = ips_count;
        this.max_kf = kf_count;
        this.data = new int[kf_count * 2 + val_count * 2];
        this.value = new float[val_count + ips_count];
        this.eqs = new TweenEquation[val_count];
        this.active = false;
    }
    
    /** 
     * Clone an existing animation.
     * Use this instead of creating a new Animation using AnimationBuilder
     * if all you want to do is to run an identical animation with
     * different properties
     */
    public Animation(Animation clone)
    {
        this.items = new Item[clone.max_ips];
        this.indices = new int[clone.max_ips];
        
        for(int i = 0; i < this.items.length; i++) {
            this.items[i] = clone.items[i];
            this.indices[i] = clone.indices[i];
        }
                
        this.max_ips = clone.max_ips;
        this.max_kf = clone.max_kf;
        
        this.data = clone.data;
        this.value = clone.value; 
        this.eqs = clone.eqs;
        this.active = false;
        
    }
    
    /**
     * use this function to replace a property with a different one.
     * All animation timing, values etc. will remain as before
     */
    public void replaceProperty(int current_id, Item new_item, int new_index)
    {
        if(current_id < 0 || current_id >= max_ips) return;
        
        items[current_id] = new_item;
        indices[current_id] = new_index;
    }
              
    // reset animation, called by TweenManger after start()
    /* package */ void reset()
    {
        cnt_val = cnt_eqs = cnt_data = cnt_kf = 0;
        next_time = data[cnt_data++];
        curr_time = 0;
        
        // stop current tweens and set initial values
        for(int i = 0; i < max_ips; i++) {
            final float val = value[cnt_val++];
            items[i].setImmediate(indices[i], val);
        }
    }
    
    /** sttop the animation sequence, will restart it if already active */
    public void start()
    {
        TweenManager.add(this);
    }
    
    /** start the animation sequence if it is active */
    public final void stop()
    {
        TweenManager.remove(this);        
    }
    
    /* package */ boolean service(long dt)
    {
        curr_time += dt;
        
        while(cnt_kf < max_kf && curr_time >= next_time)
            do_one_kf();                   
        
        active = (cnt_kf < max_kf);    
        return active;
    }
    
    // execute one key frame
    private void do_one_kf()
    {
        int cnt = data[cnt_data++];
                        
        while(cnt-- > 0) {
            final int mem = data[cnt_data++];
            final int dur = data[cnt_data++];            
            final float val = value[cnt_val++];
            final TweenEquation eq = eqs[cnt_eqs++];             
            items[ mem].set( indices[mem], val).configure(dur / 1000.0f, eq);            
        } 
        
        // advance key frame
        cnt_kf++;        
        if(cnt_kf < max_kf)
            next_time = data[cnt_data++];
    }
}

