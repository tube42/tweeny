
package se.tube42.lib.tweeny;

/**
 * This represents a property in an Item
 */

public final class ItemProperty
{
    /* package */ boolean active;
    /* package */ float v0, v1, vd;
    /* package */ float duration, duration_inv;
    /* package */ float time_start;
    /* package */ TweenEquation equation;
    
    protected Item item;
    protected int index;
    
    /** for debugging */
    public static int object_count = 0;
    
    /** flag variable with various bits, mostly user defined */
    public int flags;
    
    public ItemProperty()
    {
        object_count++;
        reset();
    }
    
    /** set the equation if not null */
    public final void configure(float duration, TweenEquation equation)
    {
        this.duration = Math.max(0.0001f, duration);
        this.duration_inv = 1f / this.duration;        
        this.equation = equation;
    }
                
    /* package */ final void set(Item item, int index, float v0, float v1)
    {
        this.item = item;
        this.index = index;
        
        this.v0 = v0;
        set(v1);
    }
    
    /* package */ void set(float v1)
    {
        this.v1 = v1;
        this.vd = v1 - v0;        
    }
    
    /* package */ void update(float d)
    {
        final float d2 = equation.compute(d);
        item.data[index] = v0 + vd * d2;
    }
    
    // -------------------------------------
    /* package */ void reset()
    {   
        if(item != null && item.properties[index] == this) {
            item.properties[index] = null;            
        }
        this.active = false;
        this.item = null;
        this.flags = 0;
        configure(1f, TweenEquation.LINEAR);        
        
        
    }    
}
