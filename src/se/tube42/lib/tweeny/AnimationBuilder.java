
package se.tube42.lib.tweeny;

import java.util.*;

/**
 * this is a heavy weight class used to create animations
 */

// internal data for AnimationBuilder
/* package */ class AnimationAction
{
    public int index;
    public float value;
    public float start_time;
    public float duration;
    public TweenEquation eq;
}

public class AnimationBuilder
{
    
    protected static Comparator<AnimationAction> comp = new Comparator<AnimationAction>() {
        public int compare(AnimationAction a, AnimationAction b) {
            if(a.start_time > b.start_time) return +1;
            if(a.start_time < b.start_time) return -1;
            return 0;
        }
    };
    
    /** max number of properties we can animate */
    private static final int MAX_PROPS = 64;
    
    private int prop_cnt;
    private ItemProperty [] prop_obj;
    private float [] prop_time;
    private float [] prop_start;
    
    private ArrayList<AnimationAction> actions;
    
    public AnimationBuilder()
    {
        prop_obj = new ItemProperty[MAX_PROPS];
        prop_time = new float[MAX_PROPS];
        prop_start = new float[MAX_PROPS];
        actions = new ArrayList<AnimationAction>();        
        reset();
    }
    
    /** reset all animation data, for reusing the AnimationBuilder object */
    public void reset()
    {
        prop_cnt = 0;
        actions.clear();
    }
    
    /** add a property in the timeline */
    public int addProperty(Item owner, int index, float start_value)
    {
        if(prop_cnt >= MAX_PROPS) return -1;
        
        ItemProperty ip = owner.properties[index];                
        prop_obj[prop_cnt] = ip;
        prop_time[prop_cnt] = 0;
        prop_start[prop_cnt] = start_value;
        return prop_cnt++;
    }
    
    /** set a property after some duration */
    public void set(int id, float value, float duration, TweenEquation eq)
    {        
        if(id < 0 || id >= prop_cnt) return; // invalid id
        
        AnimationAction aa = new AnimationAction();
        aa.index = id;
        aa.duration = duration;
        aa.start_time = prop_time[id];
        aa.value = value;
        aa.eq = eq;
        
        prop_time[id] += duration;
        actions.add(aa);
    }
    
    /** add pause to a timeline for the given duration */
    public void pause(int id, float duration)
    {        
        if(id < 0 || id >= prop_cnt) return; // invalid id        
        prop_time[id] += duration;
    }
    
    /** add pause to a timeline until this frame time is reached*/
    public void pauseUntil(int id, float time)
    {        
        if(id < 0 || id >= prop_cnt) return; // invalid id        
        if(prop_time[id] < time)
            prop_time[id] = time;
    }
    
    
    /** retruns the last key time */
    public float getTime()
    {
        float ret = 0;
        for(int i = 0; i < prop_cnt; i++)
            ret = Math.max(ret, prop_time[i]);
        return ret;
    }
    
    /** synchronize all timelines to the last key. return the sync time */
    public float synchronize()
    {
        float time = getTime();
        for(int i = 0; i < prop_cnt; i++)
            pauseUntil(i, time);
        return time;
    }
    
    
    /** create the animation object */
    public Animation build(Runnable on_finish)
    {
        // sort everything with duration time
        Collections.sort(actions, comp);
        
        // find the number of key frames
        int frames = 0;
        float last_time = -1;
        for(AnimationAction aa : actions) {
            if(aa.start_time != last_time) {
                frames ++;
                last_time = aa.start_time;
            }
        }
        
        // warning: unreadable & crazy code to follow
        Animation anim = new Animation(prop_obj, prop_cnt, frames, actions.size());
        int cnt_kf = 0, cnt_count = 0, cnt_val = 0, cnt_eq = 0;
        last_time = -1;
        
        // insert the initial values in a key-frame
        anim.kf_start_time[0] = -1;
        anim.kf_member_count[cnt_count++] = -prop_cnt;
        for(int i = 0; i < prop_cnt; i++) {
            anim.val_dur[cnt_val++] = prop_start[i];
            anim.kf_member_count[cnt_count++] = i;
        }
        
        // insert the rest
        int loc_count = 0;
        for(AnimationAction aa : actions) {
            if(aa.start_time != last_time) {
                cnt_kf++;
                loc_count = cnt_count;                
                anim.kf_member_count[cnt_count++] = 0;
                anim.kf_start_time[cnt_kf] = last_time = aa.start_time;
            }
            anim.kf_member_count[loc_count]++;
            anim.kf_member_count[cnt_count++] = aa.index; 
            anim.val_dur[cnt_val++] = aa.value;
            anim.val_dur[cnt_val++] = aa.duration;
            anim.eqs[cnt_eq++] = aa.eq;
        }
        
        anim.on_finish = on_finish;
        
        return anim;
    }
        
}
