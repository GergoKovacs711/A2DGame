package com.gergo.kovacs.a2dgame.sprite;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.utility.RNG;

public class KilledEnemy extends Enemy
{
    private static final float SCALE_DELTA = 0.0005f;

    public void init (float ratio, float position[], float vector[], float scale)
    {
        super.textureDataHandle = loadGLTexture(R.drawable.killed_enemy);
        super.ratio = ratio;
        super.currentScale[0] = scale;
        super.currentScale[1] = super.currentScale[0] / IMAGE_RATIO;
        super.currentPos = position;
        super.vector = vector;
        super.rotationz = RNG.randomFloatBetween(-90, 90);
        super.rotationDelta = RNG.randomFloatBetween(-2, 2);
        super.alive = true;
    }

    @Override
    public boolean update ()
    {
        // broken asteroids move down and scale down to 0 to make them look like they are moving away
        super.currentScale[0] -= SCALE_DELTA;
        super.currentScale[1] -= SCALE_DELTA;
        if (super.currentScale[0] <= 0 || super.currentScale[1] <= 0)
        {
            return false;
        }
        else
        {
            return super.update();
        }
    }
}

