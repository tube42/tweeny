
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
     * @param fields number of properties in this item
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
    /**
     * is this property currently tweened? 
     * @param index index of the property of interest
     * @return true if currently tweened
     */
    public final boolean isTweenActive(int index)
    {
        return properties[index] != null;
    }
    
    /**
     * is any property currently tweened? 
     * @return true if anything is currently tweened
     */
    public final boolean isTweenActive()
    {
        for(final ItemProperty ip : properties) {
            if(ip != null)
                return true;
        }
        return false;
    }
    
    /** 
     * get tween time (assuimng ideal clock)
     * @return total time required by this tween and its tails.
     */    
    public final float getTime()
    {        
        float ret = 0;
        for(final ItemProperty ip : properties) {
            if(ip != null)
                ret = Math.max(ret, ip.getTime());
        }
        return ret;
    }
    
    /** 
     * stop tweening this variable 
     * @param index index of the property of interest
     * @param finish property is set to its final value if this is true    
     */
    public final void removeTween(int index, boolean finish)
    {
        final ItemProperty ip = properties[index];
        if(ip != null)            
            TweenManager.removeTween(ip, finish);
    }
    
    /** 
     * get current value of a variable
     * @param index index of the property of interest
     */
    public final float get(int index)
    {
        return data[index];
    }
    
    /** 
     * set current value of a variable 
     * @param index index of the property of interest     
     * @param value value to set
     */        
    public final void setImmediate(int index, float value)
    {        
        removeTween(index, false);        
        data[index] = value;        
    }
    
    /** 
     * set future value of a variable
     * @param index index of the property of interest
     * @param value final value
     * @return the tween node
     */
    public final TweenNode set(int index, float value)
    {             
        return set(index, data[index], value);
    }
    
    /** 
     * tween value of a variable from v0 to v1 
     * @param index index of the property of interest
     * @param v0 initial value
     * @param v1 final value
     * @return the tween node     
     */ 
    public final TweenNode set(int index, float v0, float v1)
    {
        return TweenManager.addTween(this, index, v0, v1);
    }
}
