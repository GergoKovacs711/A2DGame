package com.gergo.kovacs.a2dgame.sprite;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.sprite.util.Moveable;
import com.gergo.kovacs.a2dgame.utility.RNG;

public class Coin extends Moveable {

    protected static final float IMAGE_RATIO = 1f;
    private static float SCALE = 0.1f;
    private final static float vectorLimit = 0.02f;

    public Coin() {
        super.drawableResourceId = R.drawable.coin;
    }

    public void init() {
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

    @Override
    public void initIcon(float posX, float posY, float scaleX, float scaleY) {
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
    public void setRatio(float ratio) {
        super.setRatio(ratio);

        // position at bottom of screen
        super.currentPos[1] = -super.ratio + 2 * super.currentScale[1];
    }

    public boolean update(boolean changeSpeed) {
        super.update();


        if (changeSpeed) {
            float speedChangeAmount = .0f;
            if (RNG.randomBoolean()) {
                speedChangeAmount = (float)(vectorLimit * Math.random() * .1);
            } else {
                speedChangeAmount = (float) -(vectorLimit * Math.random() * .1);
            }

            final float productVector =  super.vector[0] + speedChangeAmount;
            if (Math.abs(productVector) <= vectorLimit){
                super.vector[0] += speedChangeAmount;
            }
            else {
               if (productVector < 0) {
                    super.vector[0] = -vectorLimit;
                } else {
                    super.vector[0] = +vectorLimit;
                }
            }

        }

        // if hit wall move back opposite direction
        if ((currentPos[0] - currentScale[0]) <= -1) {
            super.vector[0] = -super.vector[0];
        } else if ((currentPos[0] + currentScale[0]) > 1) {
            super.vector[0] = -super.vector[0];
        }

        return true;
    }
}
