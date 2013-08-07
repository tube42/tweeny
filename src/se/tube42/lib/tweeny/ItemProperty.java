
package se.tube42.lib.tweeny;

/**
 * This represents a single property in an Item
 */
/* package */ final class ItemProperty
{
    /** bitfield for flags */
    public static final int
          FLAGS_CHANGED = 1
          ;
    
    /* package */ float v0, v1, vd, vc;
    /* package */ float duration, duration_inv;
    /* package */ long time_start;
    /* package */ boolean active;
    /* package */ TweenEquation equation;
    
    /** flag variable with various bits, mostly user defined */
    public int flags;
    
    public ItemProperty()
    {
        this.active = false;
        this.flags = 0;
        setEquation(TweenEquation.LINEAR);
        setDuration(1f);
    }
    
    /** set the equation if not null */
    public final void setEquation(TweenEquation equation)
    {
        if(equation != null)
            this.equation = equation;
    }
    
    public final float getDuration()
    {
        return duration;
    }
    public final void setDuration(float time)
    {
        this.duration = Math.max(0.0001f, time);
        this.duration_inv = 1f / this.duration;
    }
    /** get current value */
    public final float get() 
    {
        return vc;
    }
    
    /** set value immediately , without tweening */
    public final void setImmediate(float value)
    {
        this.vc = value;
        deactivate();
    }
    
    /** set value with tweening */
    public final void set(float value)
    {
        set(vc, value);
    }
    
    public final void set(float v0, float v1)
    {
        this.v0 = this.vc = v0;
        this.v1 = v1;
        this.vd = v1 - v0;
        if(v0 == v1) 
            deactivate();
        else
            activate();
    }
    
    // -------------------------------------
    private final void activate()
    {
        TweenManager.add(this);
    }
    private final void deactivate()
    {
        TweenManager.remove(this);
    }
    
    /* package */ void removeTween(boolean finish)
    {
        if(active) {
            if(finish) vc = v1;
            deactivate();
        }
    }
}
