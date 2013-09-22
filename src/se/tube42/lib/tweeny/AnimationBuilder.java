
package se.tube42.lib.tweeny;

import java.util.*;

// internal data for AnimationBuilder
/* package */ final class AnimationAction
{
    public int index;
    public float value;
    public int start_time;
    public int duration;
    public TweenEquation eq;
}


/**
 * This is a heavy weight class used to create animations.
 * 
 * To re-use an AnimationBuilder object, call reset()
 */

public final class AnimationBuilder
{
    
    // this Comparator is used to sort action by their start time
    protected static Comparator<AnimationAction> comp = new Comparator<AnimationAction>() {
        public int compare(AnimationAction a, AnimationAction b) {
            return a.start_time - b.start_time;
        }
    };
    
    /** max number of properties we can animate */
    private static final int MAX_PROPS = 128;
    
    private int prop_cnt;
    private Item [] prop_owner;
    private int [] prop_index;
    private float [] prop_time;
    private float [] prop_start;    
    private ArrayList<AnimationAction> actions;
    
      
    public AnimationBuilder()
    {
        prop_time = new float[MAX_PROPS];
        prop_start = new float[MAX_PROPS];
        actions = new ArrayList<AnimationAction>();        
        reset();
    }
    
    /** reset all animation data, for reusing the AnimationBuilder object */
    public void reset()
    {
        prop_cnt = 0;
        
        prop_owner = new Item[MAX_PROPS];
        prop_index = new int[MAX_PROPS];
        actions.clear();
    }
    
    /** add a property to the timeline. the start value is applied at time zero */
    public int addProperty(Item owner, int index, float start_value)
    {
        if(prop_cnt >= MAX_PROPS) return -1;
                
        // see if we already have this one:
        for(int i = 0; i < prop_cnt; i++)
            if(prop_owner[i] == owner && prop_index[i] == index)
                return i;
               
        // add new one
        prop_owner[prop_cnt] = owner;
        prop_index[prop_cnt] = index;
        prop_time[prop_cnt] = 0;
        prop_start[prop_cnt] = start_value;
        return prop_cnt++;
    }
    
    /** set a property after some duration */
    public void set(int id, TweenEquation eq, 
              float... val_dur)
//              float value, float duration, )
    {        
        if(id < 0 || id >= prop_cnt) return; // invalid id
        
        for(int i = 0; i < val_dur.length; i += 2) {
            AnimationAction aa = new AnimationAction();
            final float value = val_dur[i + 0];
            final float duration = val_dur[i + 1];
            aa.index = id;
            aa.duration   = Math.max(1, (int)(0.5f + 1000f * duration));
            aa.start_time = (int)(0.5 + 1000f * prop_time[id]);
            aa.value = value;
            aa.eq = eq;
                        
            prop_time[id] += duration;
            actions.add(aa);
        }
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
        int last_start = -2;
        for(AnimationAction aa : actions) {
            if(aa.start_time != last_start) {
                frames ++;
                last_start = aa.start_time;
            }
        }
        
        // warning: unreadable & crazy code to follow
        Animation anim = new Animation(prop_owner, prop_index,
                  prop_cnt, 
                  frames + 1, 
                  actions.size());
        int cnt_data = 0, cnt_val = 0, index_count = 0, cnt_eqs = 0;
        last_start = -1;
        
        // insert the initial values in a key-frame
        for(int i = 0; i < prop_cnt; i++)
            anim.value[cnt_val++] = prop_start[i];        
        
        // insert the rest
        int last_end = (int)(0.5f + 1000 * getTime());
        for(AnimationAction aa : actions) {
            last_end = Math.max(last_end, aa.start_time + aa.duration);
            
            if(aa.start_time != last_start) {
                anim.data[cnt_data++] = last_start = aa.start_time;
                index_count = cnt_data;
                anim.data[cnt_data++] = 0;
            }
            
            anim.data[index_count]++;
            anim.data[cnt_data++] = aa.index; 
            anim.data[cnt_data++] = aa.duration;
            
            anim.value[cnt_val++] = aa.value;
            anim.eqs[cnt_eqs++] = aa.eq;
        }
        
        // final frame has no movements, it is just there so we
        // can detect the end and call on_finish
        anim.data[cnt_data++] = last_end;
        anim.data[cnt_data++] = 0; // zero members
        
        anim.on_finish = on_finish;
        
        return anim;
    }
        
}
