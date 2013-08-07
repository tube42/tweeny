
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

    /**
     * create an item with the given number of variables
     */    
    public Item(int fields) 
    {
        if(fields > 0) {
            properties = new ItemProperty[fields];
            for(int i = 0; i < properties.length; i++) 
                properties[i] = new ItemProperty();
        } else {
            properties = null;
        }
    }
    
    
    // ------------------------------------------------------
    /** is this variable currently tweened? */
    public final boolean isTweenActive(int index)
    {
        return properties[index].active;
    }
    
    /** stop tweening this variable */
    public final void removeTween(int index, boolean finish)
    {
        properties[index].removeTween(finish);
    }
    
    /** set tweening equation for this variable */
    public final void setEquation(int index, TweenEquation eq)
    {
        properties[index].setEquation(eq); 
    }
    /** set tweening duration for this variable */
    public final void setDuration(int index, float time)
    {
        properties[index].setDuration(time);                
    }
    
    /** get current value of a variable */    
    public final float get(int index)
    {
        return properties[index].get();
    }
    
    /** set current value of a variable */        
    public final void setImmediate(int index, float value)
    {
        properties[index].setImmediate(value);        
    }
    
    /** set future value of a variable */        
    public final void set(int index, float value)
    {
        properties[index].set(value);                
    }
    
    /** tween value of a variable from v0 to v1 */        
    public final void set(int index, float v0, float v1)
    {
        properties[index].set(v0, v1);          
    }        
}
