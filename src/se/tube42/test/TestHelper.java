package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.tweeny.*;

@RunWith(JUnit4.class)
public class TestHelper
{
    public static final float DELTA = 0.0001f;

    @Before public void initialize()
    {
        // this will ensure clean state for each test
        TweenManager.reset();
    }

    public static void assertListener(DummyListener dl, int cnt, int index, Item item, int msg, String text)
    {
        Assert.assertEquals("cnt " + text, cnt, dl.cnt);
        Assert.assertEquals("index " + text, index, dl.index);
        Assert.assertEquals("item " + text, item, dl.item);
        Assert.assertEquals("msg " + text, msg, dl.msg);
    }

    @Test public void testAnimate()
    {
        Item [] its = new Item[4];
        for(int i = 0; i < its.length; i++)
            its[i] = new Item(2);        
                
        TweenHelper.animate(its, 0, 100, 200, 0.1f, 0.1f, null); // prop 0
        TweenHelper.animate(its, 1, 100, 200, 0.2f, 0.2f, null); // prop 1
        
        TweenManager.service(50);
                
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("Middle (0)", 150, its[i].get(0), DELTA);
            Assert.assertEquals("Quarter (1)", 125, its[i].get(1), DELTA);
        }
        
        TweenManager.service(50);
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("End (0)", 200, its[i].get(0), DELTA);
            Assert.assertEquals("Middle (1)", 150, its[i].get(1), DELTA);
        }
        
        
        TweenManager.service(100);
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("End (again, 0)", 200, its[i].get(0), DELTA);
            Assert.assertEquals("End (1)", 200, its[i].get(1), DELTA);
        }
    }
    
    

    @Test public void testAnimatePaused()
    {
        Item [] its = new Item[4];
        for(int i = 0; i < its.length; i++)
            its[i] = new Item(2);        
                
        TweenHelper.animate(its, 0, 100, 200, 0.1f, 0.1f, 0.2f, 0.2f, null); // prop 0
        TweenHelper.animate(its, 1, 100, 200, 0.2f, 0.2f, 0.2f, 0.2f, null); // prop 1
                
        TweenManager.service(200);        
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("Paused (0)", 100, its[i].get(0), DELTA);
            Assert.assertEquals("Paused (1)", 100, its[i].get(1), DELTA);
        }
        
        TweenManager.service(50);        
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("Middle (0)", 150, its[i].get(0), DELTA);
            Assert.assertEquals("Quarter (1)", 125, its[i].get(1), DELTA);
        }
        
        TweenManager.service(50);
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("End (0)", 200, its[i].get(0), DELTA);
            Assert.assertEquals("Middle (1)", 150, its[i].get(1), DELTA);
        }
        
        
        TweenManager.service(100);
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("End (again, 0)", 200, its[i].get(0), DELTA);
            Assert.assertEquals("End (1)", 200, its[i].get(1), DELTA);
        }
    }    
}
