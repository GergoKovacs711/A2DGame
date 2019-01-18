package com.gergo.kovacs.a2dgame.sprite;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.sprite.util.Moveable;
import com.gergo.kovacs.a2dgame.utility.RNG;

public class Coin extends Moveable {

    protected static final float IMAGE_RATIO = 1f;
    private static float SCALE = 0.1f;
    private final static float vectorLimit = 0.02f;

    public Coin ()
    {
        super.drawableResourceId = R.drawable.coin;
    }

    public void init ()
    {
        super.textureDataHandle = loadGLTexture(super.drawableResourceId);
        super.currentScale[0] = SCALE;
        super.currentScale[1] = super.currentScale[0] / IMAGE_RATIO;
        super.currentPos[0] = RNG.randomFloatBetween(-.99f + currentScale[0], .99f - currentScale[0]);
        super.currentPos[1] = 0;
        super.currentPos[1] = 0;
        super.currentPos[2] = 0.2f;

        super.vector[0] = RNG.randomFloatBetween(-vectorLimit, vectorLimit);
        super.alive = true;
    }

    public void setRatio (float ratio)
    {
        super.setRatio(ratio);

        // position at bottom of screen
        super.currentPos[1] = -super.ratio + 2 * super.currentScale[1];
    }

    @Override
    public boolean update ()
    {
        super.update();

        // if hit wall move back opposite direction
        if ((currentPos[0] - currentScale[0]) <= -1)
        {
            super.vector[0] = -super.vector[0];
        }
        else if ((currentPos[0] + currentScale[0]) > 1)
        {
            super.vector[0] = -super.vector[0];
        }

        return true;
    }
}
