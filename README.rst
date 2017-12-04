Tweeny
======

Tweeny is an extremely simple Java tweening library (http://en.wikipedia.org/wiki/Inbetweening)

If you don't know why it is useful, I suggest you watch `this video <http://www.youtube.com/watch?v=Fy0aCDmgnxg>`_ 


Where to get it?
----------------

Source code (under GPL-lib v2):
:: 
 git clone https://github.com/tube42/tweeny.git


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

You can basically repeat this as long as you want.

Pauses
~~~~~~
You can add a pause inside a chain. For example we can add a 2 second pause in the middle of the previous example
::
    // set variable 0 to 10 -> 20 (1s) -> (pause for 2s) -> 10 (1/2s)
    sprite.set(0, 10f, 20f).configure(1f, TweenEquation.LINEAR) // <-- first tween
        .pause(2f)                                               // <-- pause
        .tail(10f).configure(0.5f, TweenEquation.LINEAR);        // <-- second tween

You can even start a tween with a pause:
::
 // set variable 0 to 10 -> (pause for 2s) -> 20 (1s)  -> 10 (1/2s)
 sprite.pause(0, 10, 2f)                                     // <-- initial pause
    .tail(20f).configure(1f, TweenEquation.LINEAR)           // <-- first tween
    .tail(10f).configure(0.5f, TweenEquation.LINEAR);        // <-- second tween

Pauses are as expensive as tweens, so don't use a tons of pauses if you dont really have to.     

I need more information!
~~~~~~~~~~~~~~~~~~~~~~~~
From the source tree, do this to build the API docs
::
 ./gradlew javadoc

If you want to see some examples, take look at the examples folder
::
 ./gradlew run


Advanced topics
---------------
If you are a n00b, you can safely ignore this part...


Thread safety
~~~~~~~~~~~~~
Tweeny is NOT thread safe. Modifying tweens from multiple threads will result in weird crashes :(

In fact, you can crash most examples by 

Garbage collection
~~~~~~~~~~~~~~~~~~
Don't worry, we take care of GC for you by using memory pools internally ;)

Building
~~~~~~~~
Build requirements in addition to the usual Java and Android stuff are ant & JUnit 4, 
which can be a bit tricky to install on some systems (i.e. apt-get wont suffice).
 
When that is done, to build the code and run the tests

::
 ./gradlew build
 ./gradlew test

You can build the .jar library by executing
::
 ./gradlew jar
