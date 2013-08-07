
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
    
    /* package */ ItemProperty [] ips;
    /* package */ float [] kf_start_time; // |time0|time1|...
    /* package */ int [] kf_member_count; // |count0|mem0|mem1|...|count1|...
    /* package */ float [] val_dur;       // |init0|init1|... |val0|dur0|val1|dur1|...|val1|dur1|...
    /* package */ TweenEquation [] eqs;   // |eq0|eq1|...
    /* package */ boolean active;
    /* package */ Runnable on_finish;     // run this when we are done
    
    private int max_kf;
    private int cnt_kf;
    private int cnt_kf_count;
    private int cnt_val;
    private int cnt_eqs;
    private int max_ips;
    private float curr_time, next_time;
    
    /* package */ Animation(ItemProperty [] ips, int ips_count, int kf_count, int val_count)
    {
        this.ips = ips;
        this.max_ips = ips_count;
        this.max_kf = kf_count + 1;
        this.kf_start_time = new float[kf_count + 1];
        this.kf_member_count = new int[kf_count + 1 + val_count];
        this.val_dur = new float[val_count * 2 +  ips_count];
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
        this.ips = new ItemProperty[clone.max_ips];
        for(int i = 0; i < this.ips.length; i++)
            this.ips[i] = clone.ips[i];
        
        this.max_ips = clone.max_ips;
        this.max_kf = clone.max_kf;
        this.kf_start_time = clone.kf_start_time;
        this.kf_member_count = clone.kf_member_count;
        this.val_dur = clone.val_dur;
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
        ips[current_id] = new_item.properties[new_index];
    }
              
    // reset animation, called by TweenManger after start()
    /* package */ void reset()
    {
        cnt_kf = 0;
        cnt_kf_count = 0;
        cnt_val = 0;
        cnt_eqs = 0;
        next_time = kf_start_time[0];
        curr_time = 0;
        
        // stop current tweens and set initial values
        for(int i = 0; i < max_ips; i++) {
            final float val = val_dur[cnt_val++];
            ips[i].removeTween(false);
            ips[i].setImmediate(val);
        }
    }
    
    /** sttop the animation sequence, will restart it if already active */
    public void start()
    {
        TweenManager.add(this);
    }
    
    /** start the animation sequence if it is active */
    private final void stop()
    {
        TweenManager.remove(this);
        
    }
    
    /* package */ boolean service(long dt)
    {
        curr_time += dt / 1000f;
        
        while(cnt_kf < max_kf && curr_time >= next_time)
            do_one_kf();                   
        
        active = (cnt_kf < max_kf);    
        return active;
    }
    
    // execute one key frame
    private void do_one_kf()
    {
        int cnt = kf_member_count[cnt_kf_count++];
        
        while(cnt-- > 0) {
            final float val = val_dur[cnt_val++];
            final float time = val_dur[cnt_val++];
            TweenEquation eq = eqs[cnt_eqs++]; 
            final int index = kf_member_count[cnt_kf_count++];
            final ItemProperty ip = ips[index];
            ip.set(val);
            ip.setDuration(time);
            ip.setEquation(eq);
        }    
        
        // advance key frame
        cnt_kf++;        
        if(cnt_kf < max_kf)
            next_time = kf_start_time[cnt_kf];                
    }
}

