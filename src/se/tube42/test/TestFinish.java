package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.tweeny.*;

/* package */ class DummyRunnable implements Runnable
{
    public int cnt = 0;
    public void run() { cnt ++; }
}

@RunWith(JUnit4.class)
public class TestFinish
{
    public static final float DELTA = 0.0001f;

    @Before public void initialize()
  	{
  		// this will ensure clean state for each test
  		TweenManager.reset();
    }

    @Test public void testFinish()
    {
        DummyRunnable r1 = new DummyRunnable();
        Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .finish(r1);

        Assert.assertEquals("run after (0)", 0, r1.cnt);
        Assert.assertEquals("get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("run after (1)", 1, r1.cnt);
        Assert.assertEquals("get after (a)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        TweenManager.service(500);
        TweenManager.service(500);
        Assert.assertEquals("run after some more time (2)", 1, r1.cnt);
        Assert.assertEquals("get after some more time (2)", 1, it.get(0), DELTA);

    }

    @Test public void testFinishChain()
    {
        DummyRunnable r1 = new DummyRunnable();
        Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .tail(2).configure(0.5f, null)
              .finish(r1);

        Assert.assertEquals("chained run (0)", 0, r1.cnt);
        Assert.assertEquals("chained get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("chained run (1)", 0, r1.cnt);
        Assert.assertEquals("chained get after (1)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("chained run (2)", 1, r1.cnt);
        Assert.assertEquals("chained get after (2)", 2, it.get(0), DELTA);

        TweenManager.service(5000);
        Assert.assertEquals("chained run after more time (3)", 1, r1.cnt);
        Assert.assertEquals("chained get after more time (3)", 2, it.get(0), DELTA);
    }

    @Test public void testFinishMulti()
    {
        DummyRunnable r1 = new DummyRunnable();
        DummyRunnable r2 = new DummyRunnable();

        Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .finish(r1)
              .tail(2).configure(0.5f, null)
              .finish(r2);

        Assert.assertEquals("chained run (0.1)", 0, r1.cnt);
        Assert.assertEquals("chained run (0.2)", 0, r2.cnt);

        TweenManager.service(500);
        Assert.assertEquals("chained run (1.1)", 1, r1.cnt);
        Assert.assertEquals("chained run (1.2)", 0, r2.cnt);

        TweenManager.service(500);
        Assert.assertEquals("chained run (2.1)", 1, r1.cnt);
        Assert.assertEquals("chained run (2.2)", 1, r2.cnt);

        TweenManager.service(5000);
        Assert.assertEquals("chained run (3.1)", 1, r1.cnt);
        Assert.assertEquals("chained run (3.2)", 1, r2.cnt);
    }

    @Test public void testFinishSelfModifying()
    {
        final Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .finish( new Runnable() {
                       public void run() {
                       it.set(0, 2).configure(0.5f, null);
                   }
                   });

        Assert.assertEquals("self-chained get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("self-chained get after (1)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("self-chained get after (2)", 2, it.get(0), DELTA);

        TweenManager.service(5000);
        Assert.assertEquals("self-chained get after more time (3)", 2, it.get(0), DELTA);
    }

    @Test public void testFinishSelfModifyingBreak()
    {
        final Item it = new Item(2);

        // the runnable will break tween 0 -> 1 -> -5 to 0 -> 1 -> 2
        it.set(0, 0, 1).configure(0.5f, null)
              .finish( new Runnable() {
                       public void run() {
                       it.set(0, 2).configure(0.5f, null);
                   }
                   })
              .tail(-5).configure(0.5f, null);

        Assert.assertEquals("self-chained-break get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("self-chained-break get after (1)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("self-chained-break get after (2)", 2, it.get(0), DELTA);

        TweenManager.service(5000);
        Assert.assertEquals("self-chained-break get after more time (3)", 2, it.get(0), DELTA);
    }



}
