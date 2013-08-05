
package se.tube42.lib.tweeny;

public class Item
{

    private ItemProperty [] properties;

    /**
     * create an item with the given number of properties
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
    public final boolean isTweenActive(int index)
    {
        return properties[index].active;
    }
    
    public final void removeTween(int index, boolean finish)
    {
        properties[index].removeTween(finish);
    }
    
    public final void setEquation(int index, TweenEquation eq)
    {
        properties[index].setEquation(eq); 
    }
    
    public final void setDuration(int index, float time)
    {
        properties[index].setDuration(time);                
    }
    
    public final float get(int index)
    {
        return properties[index].get();
    }
    
    public final void setImmediate(int index, float value)
    {
        properties[index].setImmediate(value);        
    }
    
    public final void set(int index, float value)
    {
        properties[index].set(value);                
    }
    
    public final void set(int index, float v0, float v1)
    {
        properties[index].set(v0, v1);          
    }
    
    
}
