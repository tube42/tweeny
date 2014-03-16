package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.tweeny.*;


@RunWith(JUnit4.class)
public class TestRemove
{
    public static final float DELTA = 0.0001f;
   
    @Before public void initialize() 
    {
    	// this will ensure clean state for each test
    	TweenManager.reset(); 
     } 

    @Test public void testRemove()
    {
        Item it = new Item(2);        
        
        it.set(0, 0, 1).configure(0.5f, null);        
        it.set(1, 0, 1).configure(0.5f, null);                
        Assert.assertEquals("get initial  (0)", 0, it.get(0), DELTA);
        Assert.assertEquals("get initial  (1)", 0, it.get(1), DELTA);
        
        TweenManager.service(250);
        Assert.assertEquals("get after 250 (0)", 0.5f, it.get(0), DELTA);
        Assert.assertEquals("get after 250 (1)", 0.5f, it.get(1), DELTA);
        
        it.removeTween(0, true);        
        it.removeTween(1, false);
        Assert.assertEquals("get after finish (0)", 1, it.get(0), DELTA);
        Assert.assertEquals("get after finish (1)", 0.5f, it.get(1), DELTA);
        
        TweenManager.service(1000);
        Assert.assertEquals("get after finish +1s (0)", 1, it.get(0), DELTA);
        Assert.assertEquals("get after finish +1s (1)", 0.5f, it.get(1), DELTA);                
    }
    
    
        @Test public void testRemoveWithTail()
    {
        Item it = new Item(2);        
        
        it.set(0, 0, 1).configure(0.5f, null)
              .tail(2).configure(0.5f, null);
        it.set(1, 0, 1).configure(0.5f, null)
              .tail(2).configure(0.5f, null);              
        Assert.assertEquals("get initial  (0)", 0, it.get(0), DELTA);
        Assert.assertEquals("get initial  (1)", 0, it.get(1), DELTA);
        
        TweenManager.service(250);
        Assert.assertEquals("get after 250 (0)", 0.5f, it.get(0), DELTA);
        Assert.assertEquals("get after 250 (1)", 0.5f, it.get(1), DELTA);
        
        it.removeTween(0, true);        
        it.removeTween(1, false);
        Assert.assertEquals("get after finish (0)", 2, it.get(0), DELTA);
        Assert.assertEquals("get after finish (1)", 0.5f, it.get(1), DELTA);
        
        TweenManager.service(1000);
        TweenManager.service(1000);
        Assert.assertEquals("get after finish +1s (0)", 2, it.get(0), DELTA);
        Assert.assertEquals("get after finish +1s (1)", 0.5f, it.get(1), DELTA);                
    }

}
