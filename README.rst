Tweeny
======

Tweeny is an extremely simple Java tweening library (http://en.wikipedia.org/wiki/Inbetweening)

It may not be as efficient as some other libraries out there, but it is very compact and simple.


How to use it:
--------------
Assume we have two variables we would like to tween. For this we will create an Item object with two members
::
 import se.tube42.libe.tweeny.*;
 . . .
 Item item = new Item(2);

To access these variables, you simply do
::
 // set variable 0 to 1.0 right now
 item.setImmediate(0, 1.0f);
 
 // get value of variable 0 right now
 float v0 = item.get(0);

Now, with tweening you can set the value of something in future and Tweeny will animate your 
variable towards that value within the given time
::
 // set variable 0 to 3.5, two seconds in future:
 item.setDuration(0, 2.0f);
 item.set(0, 3.5f);

That's basically all there is to it. The only other thing you need to remember is to call the *TweenManager.service(long dt)* in your game loop (the dt parameter is the frame time in milliseconds).

Tween equations
~~~~~~~~~~~~~~~
Optionally, you can set the transition equation (the "ease" function) for each variable. Here is an example
::
 item.setEquation(0, TweenEquation.QUAD_IN);
 
These euqation are explained at http://easings.net .

Animations
~~~~~~~~~~
If you have a lot of complex movements, you might want to bundle them into an animation sequence. Here is how it is done
::
 // objective: tween variables 0 and 1 with 1 second movements.
 // variable 0 is initially set to 33 and then to [80, 10, 73] 
 // variable 1 is initially set to 200 and then to [100, 0]
 AnimationBuilder ab = new AnimationBuilder();
 int id0 = ab.addProperty(item, 0, 33); // last number is the initial value
 ab.set(id0, TweenEquation.LINEAR, 80, 1, 10, 1, 73, 1 );
 
 int id1 = ab.addProperty(item, 1, 200);
 ab.set(id1, TweenEquation.LINEAR, 100, 1, 0, 1);
 
 Animation anim = ab.build(null);
 
To play the animation, at any time you just do
::
 anim.start();

If your animation is already running, it will simply restart.
 
I need more information!
~~~~~~~~~~~~~~~~~~~~~~~~
Take a look at the example located in src/se/tube42/example/tweeny

Why?
----
Why another tweening library? Because I wanted to experiment with some animation techniques and couldn't get the existing ones to work to my liking.

