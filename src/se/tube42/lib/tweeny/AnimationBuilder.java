
package se.tube42.lib.tweeny;

import java.util.*;

// internal data for AnimationBuilder
/* package */ final class AnimationAction
{
    public int index;
    public float value;
    public int start_time;
    public int duration;
    public int pointer;
    public TweenEquation eq;
    
    public AnimationAction(int index, float value, int start_time, int duration, TweenEquation eq)
    {
        this.index = index;
        this.value = value;
        this.start_time = start_time;
        this.duration = duration;
        this.eq = eq;
        this.pointer = -1;
    }
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
    private ArrayList<AnimationAction> actions;
    
      
    public AnimationBuilder()
    {
        prop_time = new float[MAX_PROPS];
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
        
        AnimationAction aa = new AnimationAction(prop_cnt, start_value, -1, -1, null);
        actions.add(aa);
        
        return prop_cnt++;
    }
    
    /** set a property after some duration */
    public void set(int id, TweenEquation eq, 
              float... val_dur)
//              float value, float duration, )
    {        
        if(id < 0 || id >= prop_cnt) return; // invalid id
        
        for(int i = 0; i < val_dur.length; i += 2) {
            final float value = val_dur[i + 0];
            final float duration = val_dur[i + 1];
            
            
            AnimationAction aa = new AnimationAction(id, value,
                      /* start time */ (int)(0.5 + 1000f * prop_time[id]),
                      /* duration */ Math.max(1, (int)(0.5f + 1000f * duration)),
                      eq
                      );
                                    
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
    
    
    // ----------------------------------------------------------------------------------
    
    /** 
     * get a marker to the last set() operation, which can be used to change 
     * it's value at some later point
     */
    public Object getMarker()
    {
        final int size = actions.size() - 1;
        return size < 0 ? null : actions.get(size);
    }
    
    /**
     * change the value of the variable pointed to by the marker in this animation. 
     * Note that you must ensure the Animation and marker really are related.
     * @returns false if failed
     */
    public static boolean changeValueAtMarker(Animation animation, Object marker, float value)
    {
        try {            
            AnimationAction aa = (AnimationAction) marker;            
            if(aa.pointer >= 0 && aa.pointer < animation.value.length) {
                animation.value[aa.pointer] = value;
                return true;
            }
        } catch(Exception exx) { 
            // ignored
        }
        
        return false; // failed
    }
    // ----------------------------------------------------------------------------------
    
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
        
        // insert the initial values in a key-frame
        for(int i = 0; i < prop_cnt; i++) {
            AnimationAction aa = actions.get(i);
            aa.pointer = aa.index;
            anim.value[ aa.index] = aa.value;
        }    
        
        
        final int size = actions.size();        
        int cnt_data = 0, index_count = 0, cnt_eqs = 0;
        int cnt_val = prop_cnt; // start right after the initial values from above
        last_start = -1;        
        
        
        // insert the rest
        int last_end = (int)(0.5f + 1000 * getTime());
        for(int i = prop_cnt; i < size; i++) {
            AnimationAction aa = actions.get(i);
            
            // tweened value
            last_end = Math.max(last_end, aa.start_time + aa.duration);
            
            if(aa.start_time != last_start) {
                anim.data[cnt_data++] = last_start = aa.start_time;
                index_count = cnt_data;
                anim.data[cnt_data++] = 0;
            }
            
            anim.data[index_count]++;
            anim.data[cnt_data++] = aa.index; 
            anim.data[cnt_data++] = aa.duration;
            
            aa.pointer = cnt_val;  /* save position of value !*/
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
