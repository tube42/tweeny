package se.tube42.test;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import se.tube42.lib.tweeny.*;

/* package */ class DummyListener implements TweenListener
{
    public int cnt = 0;
    public Item item = null;
    public int index = -1;
    public int msg = -1;

    public void onFinish(Item item, int index, int msg)
    {
        this.cnt++;
        this.item = item;
        this.index = index;
        this.msg = msg;
    }
}

@RunWith(JUnit4.class)
public class TestListener
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

    @Test public void testListener()
    {
        DummyListener r1 = new DummyListener();
        Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .finish(r1, 10);

        Assert.assertEquals("run after (0)", 0, r1.cnt);
        Assert.assertEquals("get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        assertListener(r1, 1, 0, it, 10, "after (1)");
        Assert.assertEquals("get after (a)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        TweenManager.service(500);
        TweenManager.service(500);
        Assert.assertEquals("run after some more time (2)", 1, r1.cnt);
        Assert.assertEquals("get after some more time (2)", 1, it.get(0), DELTA);

    }

    @Test public void testFinishChain()
    {
        DummyListener r1 = new DummyListener();
        Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .tail(2).configure(0.5f, null)
              .finish(r1, 20);

        Assert.assertEquals("chained run (0)", 0, r1.cnt);
        Assert.assertEquals("chained get after (0)", 0, it.get(0), DELTA);

        TweenManager.service(500);
        Assert.assertEquals("chained run (1)", 0, r1.cnt);
        Assert.assertEquals("chained get after (1)", 1, it.get(0), DELTA);

        TweenManager.service(500);
        assertListener(r1, 1, 0, it, 20, "after (2)");
        Assert.assertEquals("chained get after (2)", 2, it.get(0), DELTA);

        TweenManager.service(5000);
        Assert.assertEquals("chained run after more time (3)", 1, r1.cnt);
        Assert.assertEquals("chained get after more time (3)", 2, it.get(0), DELTA);
    }

    @Test public void testFinishMulti()
    {
        DummyListener r1 = new DummyListener();
        DummyListener r2 = new DummyListener();

        Item it = new Item(2);

        it.set(1, 0, 1).configure(0.5f, null)
              .finish(r1, 1)
              .tail(2).configure(0.5f, null)
              .finish(r2, 2)
              .tail(2).configure(0.5f, null)
              .finish(r1, 3);

        Assert.assertEquals("chained run (0.1)", 0, r1.cnt);
        Assert.assertEquals("chained run (0.2)", 0, r2.cnt);

        TweenManager.service(500);
        assertListener(r1, 1, 1, it, 1, "chained run (2)");
        Assert.assertEquals("chained run (1.2)", 0, r2.cnt);

        TweenManager.service(500);
        assertListener(r1, 1, 1, it, 1, "chained run (2.1)");
        assertListener(r2, 1, 1, it, 2, "chained run (2.2)");

        TweenManager.service(5000);
        assertListener(r1, 2, 1, it, 3, "chained run (3.1)");
        assertListener(r2, 1, 1, it, 2, "chained run (3.2)");
    }


    @Test public void testFinishSelfModifying()
    {
        final Item it = new Item(2);

        it.set(0, 0, 1).configure(0.5f, null)
              .finish( new TweenListener() {
                       public void onFinish(Item item, int index, int msg) {
                       Assert.assertEquals("self-chained msg", 555, msg);
                       Assert.assertEquals("self-chained index", 0, index);
                       Assert.assertEquals("self-chained item", it, item);

                       item.set(index, 2).configure(0.5f, null);
                   }}, 555);

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
              .finish( new TweenListener() {
                       public void onFinish(Item item, int index, int msg) {
                       Assert.assertEquals("self-chained msg", 555, msg);
                       Assert.assertEquals("self-chained index", 0, index);
                       Assert.assertEquals("self-chained item", it, item);

                       item.set(index, 2).configure(0.5f, null);
                   }}, 555)
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
