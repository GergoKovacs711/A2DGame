package com.gergo.kovacs.a2dgame.sprite;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.sprite.util.Moveable;
import com.gergo.kovacs.a2dgame.utility.RNG;

public class Enemy extends Moveable {
    public static final float MAX_SCALE = 0.25f;
    public static final float MIN_SCALE = 0.08f;
    protected static final float IMAGE_RATIO = 1f;

    public float rotationDelta;

    public Enemy ()
    {
        super.drawableResourceId = R.drawable.enemy;
    }

    public void initRandom ()
    {
        super.textureDataHandle = loadGLTexture(super.drawableResourceId);
        super.currentScale[0] = RNG.randomFloatBetween(MIN_SCALE, MAX_SCALE);
        super.currentScale[1] = super.currentScale[0] / IMAGE_RATIO;
        super.currentPos[0] = RNG.randomFloatBetween(-1 + currentScale[0],
                                                1 - currentScale[0]);
        super.currentPos[1] = 0;
        super.currentPos[2] = 0.1f;

        super.vector[0] = RNG.randomFloatBetween(-0.006f, 0.006f);
        super.vector[1] = RNG.randomFloatBetween(-0.03f, -0.02f);
        super.rotationz = RNG.randomFloatBetween(-90, 90);
        rotationDelta = RNG.randomFloatBetween(-2, 2);
        super.alive = true;
    }

    @Override
    public void initIcon (float posX, float posY, float scaleX, float scaleY)
    {
        super.textureDataHandle = loadGLTexture(super.drawableResourceId);
        super.currentScale[0] = scaleX;
        super.currentScale[1] = scaleY;
        super.currentPos[0] = posX;
        super.currentPos[1] = posY;
        super.currentPos[2] = 0.2f;

        super.vector[0] = super.vector[1] = super.vector[2] = 0;
        super.rotationz = 0;
        super.alive = true;
    }

    @Override
    public void setRatio (float ratio)
    {
        super.setRatio(ratio);

        // start asteroid at top of screen
        super.currentPos[1] = super.ratio + super.currentScale[1];
    }

    @Override
    public boolean update ()
    {
        rotationz += rotationDelta;

        if ((currentPos[0] - currentScale[0]) <= -1)
        {
            super.vector[0] = -super.vector[0];
        }
        else if ((currentPos[0] + currentScale[0]) > 1)
        {
            super.vector[0] = -super.vector[0];
        }

        return super.update();
    }

    public void setResource(int resourceId){
        this.drawableResourceId = resourceId;
    }
}
