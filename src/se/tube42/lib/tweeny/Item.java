
package se.tube42.lib.tweeny;

/**
 * The Item class is where your tweened variable are.
 * 
 * We recommend you subclass this class and add access function
 * for better readability
 */

public class Item
{
        
    /* package */ ItemProperty [] properties;
    /* package */ float [] data;
    
    /**
     * create an item with the given number of variables
     */    
    public Item(int fields) 
    {
        if(fields > 0) {
            properties = new ItemProperty[fields];
            data = new float[fields];
        } else {
            properties = null;
            data = null;
        }
    }    
    
    // ------------------------------------------------------
    /** is this variable currently tweened? */
    public final boolean isTweenActive(int index)
    {
        return properties[index] != null;
    }
    
    /** stop tweening this variable */
    public final void removeTween(int index, boolean finish)
    {
        final ItemProperty ip = properties[index];
        if(ip != null)            
            TweenManager.removeTween(ip, finish);
    }
    
    /** get current value of a variable */    
    public final float get(int index)
    {
        return data[index];
    }
    
    /** set current value of a variable */        
    public final void setImmediate(int index, float value)
    {        
        removeTween(index, false);        
        data[index] = value;        
    }
    
    /** set future value of a variable */        
    public final TweenNode set(int index, float value)
    {             
        return set(index, data[index], value);
    }
    
    /** tween value of a variable from v0 to v1 */        
    public final TweenNode set(int index, float v0, float v1)
    {
        return TweenManager.addTween(this, index, v0, v1);
    }                
}
