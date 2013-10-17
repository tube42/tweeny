Tweeny
======

Tweeny is an extremely simple Java tweening library (http://en.wikipedia.org/wiki/Inbetweening)

Tween  what??
-------------
Assume that we have an object with two variables. This could for example be a sprite with X and Y coordinates.
If we change these variables, we will see the sprite to move immediately:

.. image:: http://tube42.github.io/tweeny/notween.gif

This might be exactly what you want. But the sudden jump from point 1 to point 2 could also be extremly boring...
Something it is much nicer to the eye to **animate** this movement from current value to goal value during a number of frames

.. image:: http://tube42.github.io/tweeny/tween.gif

And this is exactly what Tweeny does for you...


If you are still not convinced about usefulness of this, I suggest you watch `this video <http://www.youtube.com/watch?v=Fy0aCDmgnxg>`_ )


Why another library?
--------------------

Why yet another tweening library? Because I wanted to experiment with some animation techniques and couldn't get the existing ones to work to my liking.

Tweeny may not be as efficient as some other libraries out there, but it is very compact and simple. And it's mine... all mine...


Where to get it?
----------------

Source code (under GPL-lib v2):
:: 
 git clone https://github.com/tube42/tweeny.git


Binary: http://tube42.github.io/tweeny/bin/Tweeny_lib.jar 


How to use Tweeny:
------------------
To use Tweeny you must create Item objects (or your own subclasses of it) with the number of variables in the constructor:
::
    import se.tube42.lib.tweeny.*;
    . . .
    Item sprite = new Item(2); // two variables: X and Y (0 and 1)
    . . .  


For normal (boring) operations, access your variables using the methods *get()* and *setImmediate()*:
::
 // set X (variable 0) to 32.0 right now
 sprite.setImmediate(0, 320f);
 
 // get value of Y (variable 1) right now
 float y = sprite.get(1);

But if you want to be one of the cool guys and tween, you call *set()* instead:
::
 // set X to 350
 sprite.set(0, 350f);

By default, your variables are animated during one second using linear interpolation.
If you want to change that, call *configure()* immediately after set.
::
 // set variable 0 to 3.5, two seconds in future, use a quadratic interpolation.
 // ( the tweening equations are explained at http://easings.net )
 sprite.set(0, 3.5f).configure(2f, TweenEquation.QUAD_IN);

Game loop updates
~~~~~~~~~~~~~~~~~
To add Tweeny to your game, you need to call *TweenManager.service()* from the game loop. 
::
 // the game loop
 while(true) {
   long dt = frametime_in_ms();

   process_input();
   update_game_objects(dt);   
   TweenManager.service(dt)  // <-- this is all you need to add
   render();
  }

That's it!


Tail-chaining
~~~~~~~~~~~~~
Sometimes you may want to start another tweening immediately after the current one finishes.
For this you should use tail-chaining.
As an example, assume we want to tween a variable 0 from 10 to 20 in 1 second then tween it back to 10 in half a sec:
::
 // set variable 0 to 10 -> 20 (1s) -> 10 (1/2s)
 sprite.set(0, 10f, 20f).configure(1f, TweenEquation.LINEAR) // <-- first tween
    .tail(10f).configure(0.5f, TweenEquation.LINEAR);        // <-- second tween

You can basically repeat this as long as you want, but for more complex tweenings you should consider using the Animation class instead (see below)

Animations
~~~~~~~~~~
Sometimes we may have a complex set of movements that we want to perform in sequence. 
Consider for example a case where we want to move our sprite from (0, 0) to (100, 100) during one second 
and then back to (50, 50) half a second later and finally we will change the Y value to 25 after three additional seconds.

Doing this using vanilla tweening can be a bit cumbersome.
For such situation it is recommended that you use a pre-built animation sequence:
::  
 // to build animations, you will need one of these classes 
 AnimationBuilder ab = new AnimationBuilder();
 
 // movements of X: 0 -> 100 (1s) -> 50 (0.5s)
 int id0 = ab.addProperty(sprite, 0, 0); // last number is the initial value
 ab.set(id0, TweenEquation.LINEAR, 100, 1, 50, 0.5f);

 // movements of Y: 0 -> 100 (1s) -> 50 (0.5s) -> 25 (3s)
 int id1 = ab.addProperty(sprite, 1, 0);
 ab.set(id1, TweenEquation.LINEAR, 100, 1, 0, 0.5f, 25, 3);
 
 // finally, we build the animation object to be used in 
 // our game/program and save it for later use
 Animation anim = ab.build(null);


To play the animation, at any time you just do
::
 anim.start();

If your animation is already running, it will simply restart.

I need more information!
~~~~~~~~~~~~~~~~~~~~~~~~
From the source tree, do this to build the API docs
::
 ant doc

If you want to see some examples, take look at these directories
:: 
 src/se/tube42/example/ease1        - demonstrates the different ease equations
 src/se/tube42/example/demo1        - demonstrates basics of tweening and animations
 src/se/tube42/example/demo2        - demonstrates use of deltas to modify animations
 src/se/tube42/example/demo3        - demonstrates use of markers to modify animations
 src/se/tube42/example/demo4        - demonstrates use of tail() to creates chains of tweens


Advanced topics
---------------
If you are a n00b, you can safely ignore this part...

Animation deltas
~~~~~~~~~~~~~~~~
For more information about animation deltas, take a look at demo2.

Animation markers
~~~~~~~~~~~~~~~~~
For more information about animation markers, take a look at demo3.

Note that markers are mostly to be used with third-party tools such as animation editors. 
Normally, developers should not need to touch them.


Garbage collection
~~~~~~~~~~~~~~~~~~
If you are afraid of the big-bad-garbage-collector, avoid using AnimationBuilder in your game 
loop as it is quite heavyweight and allocates a lot of memory. 
The marker operations are the exception to this rule, you can use them as much as you want :)

Other tweening operations use memory pools and should not generate garbage when used.
