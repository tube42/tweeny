
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
        final float x1 = t * 0.5f * (float) Math.PI;
        return (float) Math.sin(x1) + 
              (1 - t) * (float) Math.sin( 5 * t * t * x1);
    }
}

/* package */ final class ElasticOutEquation implements TweenEquation 
{
    public String toString() { return "Elastic-out"; }
    
    public final float compute(float t)
    {
        final float x0 = 1 - t;
        final float x1 = x0 * 0.5f * (float) Math.PI;
        final float p = (float) Math.sin(x1) + 
              (1 - x0) * (float) Math.sin( 5 * x0 * x0 * x1);
        return 1-p;        
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


public interface TweenEquation 
{
    public static final TweenEquation 
          LINEAR = new LinearEquation(),
          DISCRETE = new DiscreteEquation(),
          QUAD_IN = new QuadInEquation(),
          QUAD_OUT = new QuadOutEquation(),
          CUBE_IN = new CubeInEquation(),
          CUBE_OUT = new CubeOutEquation(),          
          ELASTIC_IN = new ElasticInEquation(),
          ELASTIC_OUT = new ElasticOutEquation(),
          BACK_IN = new BackInEquation(),
          BACK_OUT = new BackOutEquation(),
          SIN_IN = new SinInEquation(),
          SIN_OUT = new SinOutEquation()          
          ;
    
    public float compute(final float t);
}

