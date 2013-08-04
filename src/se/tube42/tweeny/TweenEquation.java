
package se.tube42.tweeny;

/*
 * This file contains all tween equation implementations
 */

/* package */ final class LinearEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        ip.vc = ip.v0 + ip.vd * dt * ip.duration_inv; 
    }
}


/* package */ final class QuadInEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float f0 = dt * ip.duration_inv;
        final float f1 = f0 * f0;
        ip.vc = ip.v0 + ip.vd * f1; 
    }
}

/* package */ final class QuadOutEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float f0 = (ip.duration - dt) * ip.duration_inv;
        final float f1 = f0 * f0;
        ip.vc = ip.v0 + ip.vd - ip.vd * f1; 
    }
}

/* package */ final class CubeInEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float f0 = dt * ip.duration_inv;
        final float f1 = f0 * f0 * f0;
        ip.vc = ip.v0 + ip.vd * f1; 
    }
}

/* package */ final class CubeOutEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float f0 = (ip.duration - dt) * ip.duration_inv;
        final float f1 = f0 * f0 * f0;
        ip.vc = ip.v0 + ip.vd - ip.vd * f1; 
    }
}

/* package */ final class ElasticInEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float x0 = dt * ip.duration_inv;
        final float x1 = x0 * 0.5f * (float) Math.PI;
        final float p = (float) Math.sin(x1) + 
              (1 - x0) * (float) Math.sin( 5 * x0 * x0 * x1);
        ip.vc = ip.v0 + ip.vd * p;
    }
}

/* package */ final class ElasticOutEquation implements TweenEquation 
{
    public final void update(ItemProperty ip, float dt)
    {
        final float x0 = 1 - dt * ip.duration_inv;
        final float x1 = x0 * 0.5f * (float) Math.PI;
        final float p = (float) Math.sin(x1) + 
              (1 - x0) * (float) Math.sin( 5 * x0 * x0 * x1);
        ip.vc = ip.v1 - ip.vd * p;
    }
}

public interface TweenEquation 
{
    public static final TweenEquation 
          LINEAR = new LinearEquation(),
          QUAD_IN = new QuadInEquation(),
          QUAD_OUT = new QuadOutEquation(),
          CUBE_IN = new CubeInEquation(),
          CUBE_OUT = new CubeOutEquation(),          
          ELASTIC_IN = new ElasticInEquation(),
          ELASTIC_OUT = new ElasticOutEquation()
          ;
          
    public void update(ItemProperty ip, float dt);
}

