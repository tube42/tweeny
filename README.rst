Tweeny
======

Tweeny is an extremely simple Java tweening library (http://en.wikipedia.org/wiki/Inbetweening)

Tween what??
------------
Assume that we have an object with two variables. This could for example be a sprite with X and Y coordinates.
If we change these variables, we will see the sprite to move immediately:

.. image:: http://tube42.github.io/tweeny/notween.gif

This might be exactly what you want. But the sudden jump from point 1 to point 2 could also be extremly boring...
Something it is much nicer to the eye to **animate** this movement from current value to goal value during a number of frames

.. image:: http://tube42.github.io/tweeny/tween.gif

And this is exactly what Tweeny does for you...


  (BTW, if you still don't understand what this is all about, I suggest you watch `this video <http://www.youtube.com/watch?v=Fy0aCDmgnxg>`_ )


How to use it Tweeny:
---------------------
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


That's basically all there is to it. The only other thing you need to remember is to call the *TweenManager.service(long dt)* in your game loop (the dt parameter is the frame time in milliseconds). 

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
 
 // movements of X: 0 -> 100 (1 sec) -> 50 (0.5 sec)
 int id0 = ab.addProperty(sprite, 0, 0); // last number is the initial value
 ab.set(id0, TweenEquation.LINEAR, 100, 1, 50, 0.5f);

 // movements of Y: 0 -> 100 (1 sec) -> 50 (0.5 sec) -> 25 (3 sec)
 int id1 = ab.addProperty(sprite, 1, 0);
 ab.set(id1, TweenEquation.LINEAR, 100, 1, 0, 0.5f, 25, 3);
 
 // finally, we build the animation object to be used in 
 // our game/program and save it for later use
 Animation anim = ab.build(null);


To play the animation, at any time you just do
::
 anim.start();

If your animation is already running, it will simply restart.


Where to get it?
----------------

Source code: 
:: 
 git clone https://github.com/tube42/tweeny.git


Binary: http://tube42.github.io/tweeny/bin/Tweeny_lib.jar 


I need more information!
~~~~~~~~~~~~~~~~~~~~~~~~
From the source tree, do this to build the API docs
::
 ant doc

If you want to see some examples, take look at these directories
:: 
 src/se/tube42/example/tweeny
 src/se/tube42/demo/tweeny

Why?
~~~~
Why yet another tweening library? Because I wanted to experiment with some animation techniques and couldn't get the existing ones to work to my liking.

Tweeny may not be as efficient as some other libraries out there, but it is very compact and simple. And it's mine...

