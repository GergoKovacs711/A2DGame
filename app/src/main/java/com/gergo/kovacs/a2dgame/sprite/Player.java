package com.gergo.kovacs.a2dgame.sprite;

import android.content.Context;

import com.gergo.kovacs.a2dgame.sprite.util.Texture;
import com.gergo.kovacs.a2dgame.utility.TiltCalculator;


public class Player extends Texture
{
    private static final float TILT_MIN = -15;
    private static final float TILT_MAX = 15;
    private static final float TILT_SCALE = 2.0f;
    private static float TILT_SLOP = 1f;
    private static float SCALE = 0.15f;

    private TiltCalculator tiltCalculator;

    private float tilt;
    private float ratio;

    public Player(Context context, int drawableResourceId, TiltCalculator tiltInfo)
    {
        super();
        setContext(context);
        setDrawable(drawableResourceId);

        this.tiltCalculator = tiltInfo;
    }

    public void setRatio (float ratio)
    {
        this.ratio = ratio;
        currentScale[0] = SCALE;
        currentScale[1] = SCALE / imageRatio;
        currentPos[0] = 0;
        currentPos[1] =  (currentScale[1] * 3 ) - this.ratio;
        currentPos[2] = 0.1f;
        vector[0] = vector[1] = vector[2] = 0;
        alive = true;
    }

    public boolean update ()
    {
        float tilt = tiltCalculator.getTilt() * TILT_SCALE;
        if (tilt < TILT_MIN) { tilt = TILT_MIN; }
        if (tilt > TILT_MAX) { tilt = TILT_MAX; }

        float tiltDiff = Math.abs(tilt - this.tilt);

        // ignore small player movements
        if (tiltDiff > TILT_SLOP)
        {
            // slow down player movement a bit
            if (tilt < this.tilt)
            {
                this.tilt -= (tiltDiff / 5f);
            }
            else
            {
                this.tilt += (tiltDiff / 5f);
            }

            if (tilt < 0)
            {
                currentPos[0] = -this.tilt / TILT_MIN;
            }
            else
            {
                currentPos[0] = this.tilt / TILT_MAX;
            }
        }

        return true;
    }
}
