package se.tube42.test;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import se.tube42.lib.tweeny.*;

/* package */ class RestartListener implements TweenListener
{
    public int cnt = 0;
    public void onFinish(Item item, int index, int msg)
    {
        this.cnt++;

        float c = item.get(index);
        final TweenNode tn = item.set(index, c, c + 1).configure(0.5f, null);
        if(msg > 0) tn.finish(this, msg-1);
    }
}

public class TestReuse
{
    public static final float DELTA = 0.0001f;

  	@Before public void initialize()
  	{
  		// this will ensure clean state for each test
  		TweenManager.reset();
    }

    @Test public void testReuseListener()
    {
        DummyListener r1 = new DummyListener();
        Item it1 = new Item(1);
        Item it2 = new Item(1);

        // normal callback
        it1.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);
        for(int i = 0; i < 10; i++) TweenManager.service(500);
        Assert.assertEquals("listener after (1)", 2, r1.cnt);

        // no callback
		it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);
        for(int i = 0; i < 10; i++) TweenManager.service(500);
        Assert.assertEquals("listener after (2)", 2, r1.cnt);

        // normal callback replaced with a no callback
        it1.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);
        TweenManager.service(100);
		it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);

        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("listener after (3)", 2, r1.cnt);

 		// no callback replace with a normal callback
        it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);
        TweenManager.service(100);
		it1.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1, 0);

        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("listener after (4)", 4, r1.cnt);
    }


	@Test public void testReuseRunnable()
    {
        DummyRunnable r1 = new DummyRunnable();
        Item it1 = new Item(1);
        Item it2 = new Item(1);

        // normal callback
        it1.set(0, 0, 1).configure(0.5f, null).finish(r1);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1);
        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("run after (1)", 2, r1.cnt);

        // no callback
		it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);
        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("run after (2)", 2, r1.cnt);

        // normal callback replace with a callback
        it1.set(0, 0, 1).configure(0.5f, null).finish(r1);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1);

        TweenManager.service(100);
		it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);

        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("run after (3)", 2, r1.cnt);

		// no callback replace with a normal callback
        it1.set(0, 0, 1).configure(0.5f, null);
        it2.set(0, 0, 1).configure(0.5f, null);

        TweenManager.service(100);
		it1.set(0, 0, 1).configure(0.5f, null).finish(r1);
        it2.set(0, 0, 1).configure(0.5f, null).finish(r1);

        for(int i = 0; i < 10; i++)	TweenManager.service(500);
        Assert.assertEquals("listener after (4)", 4, r1.cnt);
    }


 	@Test public void testReuseTween()
    {
		Item item = new Item(1);
		Assert.assertEquals("active tweens (0)", 0, TweenManager.debugCountActiveTweens());
		Assert.assertEquals("allocated tweens (0)", 0, TweenManager.debugCountAllocatedTweens());
		Assert.assertEquals("pooled tweens (0)", 0, TweenManager.debugCountPoolTweens());

		// normal use
		for(int i = 0; i < 10; i++) {
			item.set(0, 0, 1).configure(0.5f, null);
			Assert.assertEquals("Normal: active tweens (1)", 1, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("Normal: allocated tweens (1)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("Normal: pooled tweens (1)", 0, TweenManager.debugCountPoolTweens());

			TweenManager.service(1000);
			Assert.assertEquals("Normal: active tweens (2)", 0, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("Normal: allocated tweens (2)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("Normal: pooled tweens (2)", 1, TweenManager.debugCountPoolTweens());
		}

		//break it!
		for(int i = 0; i < 10; i++) {
			item.set(0, 0, 1).configure(0.5f, null);
			Assert.assertEquals("Break: active tweens (1)", 1, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("Break: allocated tweens (1)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("Break: pooled tweens (1)", 0, TweenManager.debugCountPoolTweens());

			TweenManager.service(200);
			Assert.assertEquals("Break: active tweens (2)", 1, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("Break: allocated tweens (2)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("Break: pooled tweens (2)", 0, TweenManager.debugCountPoolTweens());

			item.set(0, 0, 1).configure(0.5f, null);
			TweenManager.service(1000);
			Assert.assertEquals("Break: active tweens (3)", 0, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("Break: allocated tweens (3)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("Break: pooled tweens (3)", 1, TweenManager.debugCountPoolTweens());
		}
    }

    @Test public void testReuseTweenRecursive()
    {
    	RestartListener rl = new RestartListener();
    	Item item = new Item(1);
		Assert.assertEquals("active tweens (0)", 0, TweenManager.debugCountActiveTweens());
		Assert.assertEquals("allocated tweens (0)", 0, TweenManager.debugCountAllocatedTweens());
		Assert.assertEquals("pooled tweens (0)", 0, TweenManager.debugCountPoolTweens());

		// normal use
		for(int j = 0; j < 10; j++) {
			item.set(0, 0, 1).configure(0.5f, null).finish(rl, 3);
			Assert.assertEquals("active tweens (1)", 1, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("allocated tweens (1)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("pooled tweens (1)", 0, TweenManager.debugCountPoolTweens());

			for(int i = 0; i < 10; i++)	TweenManager.service(1000);
			Assert.assertEquals("active tweens (2)", 0, TweenManager.debugCountActiveTweens());
			Assert.assertEquals("allocated tweens (2)", 1, TweenManager.debugCountAllocatedTweens());
			Assert.assertEquals("pooled tweens (2)", 1, TweenManager.debugCountPoolTweens());
			Assert.assertEquals("tween count (2)", 4 * (j + 1), rl.cnt);
	    }
	}


	@Test public void testReuseNode()
    {
		Item item = new Item(1);
    	Assert.assertEquals("allocated nodes (0)", 0, TweenManager.debugCountAllocatedNodes());
		Assert.assertEquals("pooled nodes (0)", 0, TweenManager.debugCountPoolNodes());

		// normal use
		for(int i = 0; i < 10; i++) {
			item.set(0, 0, 1).configure(0.5f, null)
				.tail(2).configure(0.5f, null) // 1
				.tail(3).configure(0.5f, null) // 2
				.tail(4).configure(0.5f, null) // 3
				;
			Assert.assertEquals("Normal: allocated nodes (1)", 3, TweenManager.debugCountAllocatedNodes());
			Assert.assertEquals("Normal: pooled nodes (1)", 0, TweenManager.debugCountPoolNodes());
			for(int j = 0; j < 10; j++)	TweenManager.service(500);
			Assert.assertEquals("Normal: allocated nodes (2)", 3, TweenManager.debugCountAllocatedNodes());
			Assert.assertEquals("Normal: pooled nodes (2)", 3, TweenManager.debugCountPoolNodes());
		}

		// break it in a tween
		for(int i = 0; i < 10; i++) {
			item.set(0, 0, 1).configure(0.5f, null)
				.tail(2).configure(0.5f, null) // 1
				.tail(3).configure(0.5f, null) // 2
				.tail(4).configure(0.5f, null) // 3
				;
			TweenManager.service(10);
			item.set(0, 0, 10).configure(0.5f, null)
				.tail(20).configure(0.5f, null) // 1
				.tail(30).configure(0.5f, null) // 2
				.tail(40).configure(0.5f, null) // 3
				;

			for(int j = 0; j < 10; j++)	TweenManager.service(500);
		}

		Assert.assertEquals("Break: allocated nodes (2)", 3, TweenManager.debugCountAllocatedNodes());
		Assert.assertEquals("Break: pooled nodes (2)", 3, TweenManager.debugCountPoolNodes());

		// break it in a node
		for(int i = 0; i < 10; i++) {
			item.set(0, 0, 1).configure(0.5f, null)
				.tail(2).configure(0.5f, null) // 1
				.tail(3).configure(0.5f, null) // 2
				.tail(4).configure(0.5f, null) // 3
				;
			TweenManager.service(6000);
			item.set(0, 0, 10).configure(0.5f, null)
				.tail(20).configure(0.5f, null) // 1
				.tail(30).configure(0.5f, null) // 2
				.tail(40).configure(0.5f, null) // 3
				;

			for(int j = 0; j < 10; j++)	TweenManager.service(500);
		}
		Assert.assertEquals("Break: allocated nodes (3)", 3, TweenManager.debugCountAllocatedNodes());
		Assert.assertEquals("Break: pooled nodes (3)", 3, TweenManager.debugCountPoolNodes());
    }



    @Test public void testReuseNodeRecursive()
    {
    	RestartListener rl = new RestartListener();
    	Item item = new Item(2);
    	Assert.assertEquals("allocated nodes (0)", 0, TweenManager.debugCountAllocatedNodes());
		Assert.assertEquals("pooled nodes (0)", 0, TweenManager.debugCountPoolNodes());

		// break after a node
		for(int i = 0; i < 10; i++) {
			rl.cnt = 0;
			TweenNode tn = item.set(0, 0, 1).configure(0.5f, null);
			for(int j = 0; j < 3; j++) {
				tn = tn.tail(j + 2).configure(0.5f, null);
				if(i == j) tn.finish(rl, 8);
			}

			Assert.assertEquals("rec: allocated nodes (1)", 3, TweenManager.debugCountAllocatedNodes());
			Assert.assertEquals("rec: pooled nodes (1)", 0, TweenManager.debugCountPoolNodes());
			for(int j = 0; j < 20; j++)	TweenManager.service(500);
			Assert.assertEquals("rec: allocated nodes (2)", 3, TweenManager.debugCountAllocatedNodes());
			Assert.assertEquals("rec: pooled nodes (2)", 3, TweenManager.debugCountPoolNodes());
			if(i < 3)
				Assert.assertEquals("rec: callback count (2)", 9, rl.cnt);
		}

    }
}

