
package se.tube42.lib.tweeny;

/*
 * This file contains all tween equation implementations
 */

/* package */ final class LinearEquation implements TweenEquation 
{
    public String toString() { return "Linear"; }
    
    public final float compute(float t)
    {
        return t;
    }
}

/* package */ final class DelayedEquation implements TweenEquation 
{
    public String toString() { return "Delayed"; }
    
    public final float compute(float t)
    {        
        if(t < 0.25) return t * 2;
        if(t > 0.75) return t*2 - 1;
        return 0.5f;        
    }
}



/* package */ final class DiscreteEquation implements TweenEquation 
{
    public String toString() { return "Discrete"; }
    
    public final float compute(float t)
    {
        return t < 0.5f ? 0 : 1;
    }
}

/* package */ final class QuadInEquation implements TweenEquation 
{
    public String toString() { return "Quad-in"; }
    
    public final float compute(float t)
    {
        return t*t;
    }
}

/* package */ final class QuadOutEquation implements TweenEquation 
{
    public String toString() { return "Quad-out"; }
    
    public final float compute(float t)
    {
        final float t1 = 1 - t;
        return 1 - t1 * t1;
    }
}

/* package */ final class QuadInOutEquation implements TweenEquation 
{
    public String toString() { return "Quad-inout"; }
    
    public final float compute(float t)
    {
        // I don't like in-out equations, this one is more of a tribute to Martin...
        // https://twitter.com/grapefrukt/status/380579943748739072
        
        final float t1 = t * 2;        
        if(t1 < 1) 
            return t1 * t1 * 0.5f;
        else {
            return -0.5f * ((t1-1) * (t1-3) - 1);
        }            
    }
}

/* package */ final class CubeInEquation implements TweenEquation 
{
    public String toString() { return "Cube-in"; }
    
    public final float compute(float t)
    {
        return t * t * t;
    }
}

/* package */ final class CubeOutEquation implements TweenEquation 
{
    public String toString() { return "Cube-out"; }
    
    public final float compute(float t)
    {
        final float t1 = 1 - t;
        return 1 - t1 * t1 * t1;
    }
}

/* package */ final class SinInEquation implements TweenEquation 
{
    public String toString() { return "Sin-in"; }
    
    public final float compute(float t)
    {
        return (float) Math.sin( t * 0.5f * (float) Math.PI);
    }
}

/* package */ final class SinOutEquation implements TweenEquation 
{
    public String toString() { return "Sin-out"; }
    
    public final float compute(float t)
    {
        return 1f - (float) Math.sin((1-t) * 0.5f * (float) Math.PI);
    }
}

/* package */ final class ElasticInEquation implements TweenEquation 
{
    public String toString() { return "Elastic-in"; }
    
    public final float compute(float t)
    {
        final float x0 = 1 - t;
        final float x1 = x0 * 0.5f * (float) Math.PI;
        final float p = (float) Math.sin(x1) + 
              (1 - x0) * (float) Math.sin( 5 * x0 * x0 * x1);
        return 1-p;        
    }
}

/* package */ final class ElasticOutEquation implements TweenEquation 
{
    public String toString() { return "Elastic-out"; }
    
    public final float compute(float t)
    {        
        final float x1 = t * 0.5f * (float) Math.PI;
        return (float) Math.sin(x1) + 
              (1 - t) * (float) Math.sin( 5 * t * t * x1);        
    }
}


/* package */ final class BackInEquation implements TweenEquation 
{
    public String toString() { return "Back-in"; }
    
    public final float compute(float t)
    {
        return t * t * (2.70158f * t - 1.70158f);
    }
}

/* package */ final class BackOutEquation implements TweenEquation
{
    public String toString() { return "Back-out"; }
    
    public final float compute(float t)
    {
        final float x0 = 1 - t;
        return 1-x0*x0*(2.70158f * x0 - 1.70158f);        
    }
}

/* package */ final class Tube42OneEquation implements TweenEquation 
{
    public String toString() { return "Tube42-1"; }
    
    public final float compute(final float t)
    {        
        final float t1 = 1 - t;
        final float t2 = t1 * t1;
        final float t4 = t2 * t2;
        return 1 - (2 * t2 * t1 - 1) * t2;
    }
}

/* package */ final class Tube42TwoEquation implements TweenEquation 
{
    public String toString() { return "Tube42-2"; }
    
    public final float compute(final float t)
    {        
        final float t1 = 1 - t;
        final float t2 = t1 * t1;
        final float t4 = t2 * t2;
        return 1 - 2 * t4 * t4 + t2 * t1;
    }
}


public interface TweenEquation 
{
    public static final TweenEquation 
          LINEAR = new LinearEquation(),
          DELAYED = new DelayedEquation(),
          DISCRETE = new DiscreteEquation(),
          QUAD_IN = new QuadInEquation(),
          QUAD_OUT = new QuadOutEquation(),
          QUAD_INOUT = new QuadInOutEquation(),
          CUBE_IN = new CubeInEquation(),
          CUBE_OUT = new CubeOutEquation(),          
          ELASTIC_IN = new ElasticInEquation(),
          ELASTIC_OUT = new ElasticOutEquation(),
          BACK_IN = new BackInEquation(),
          BACK_OUT = new BackOutEquation(),
          TUBE42_1 = new Tube42OneEquation(),
          TUBE42_2 = new Tube42TwoEquation(),
          SIN_IN = new SinInEquation(),
          SIN_OUT = new SinOutEquation()          
          ;
    
    public float compute(final float t);
}

