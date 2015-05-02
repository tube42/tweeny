package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.tweeny.*;

@RunWith(JUnit4.class)
public class TestHelper
{
    public static final float DELTA = 0.0001f;
    
    private static void assertBetween(String msg, float min, float max, float actual)
    {
        Assert.assertTrue(msg, min <= actual);
        Assert.assertTrue(msg, max >= actual);
    }
    
    private Item [] its;
    
    @Before public void initialize()
    {
        its = new Item[4];
        for(int i = 0; i < its.length; i++)
            its[i] = new Item(2);
        
        // this will ensure clean state for each test
        TweenManager.reset();
    }

    @Test public void testAnimate()
    {
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
    
    
    @Test public void testSet()
    {
        TweenHelper.set(its, 0, 100);
        TweenHelper.set(its, 1, 200);
        
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("Set (0)", 100, its[i].get(0), DELTA);
            Assert.assertEquals("Set (1)", 200, its[i].get(1), DELTA);
        }
    }
    
    @Test public void testSetRange()
    {
        TweenHelper.set(its, 0, 100, 200);
        TweenHelper.set(its, 1, 200, 300);
        
        for(int i = 0; i < its.length; i++) {
            assertBetween("SetRange (0)", 100, 200, its[i].get(0));
            assertBetween("SetRange ", 200, 300, its[i].get(1));
        }
    }
    
    @Test public void testRemove()
    {
        for(int i = 0; i < its.length; i++) {
            its[i].set(0, 10, 20).configure(1, null);
            its[i].set(1, 40, 50).configure(1, null);
        }
        
        TweenManager.service(500); // halfway
        TweenHelper.remove(its, 0, true);
        TweenHelper.remove(its, 1, false);
        
        for(int i = 0; i < its.length; i++) {
            Assert.assertEquals("remove and finish (0)", 20, its[i].get(0), DELTA);
            Assert.assertEquals("remove and dont finish (1)", 45, its[i].get(1), DELTA);
        }
    }
}
