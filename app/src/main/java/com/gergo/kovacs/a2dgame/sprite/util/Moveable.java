package com.gergo.kovacs.a2dgame.sprite.util;

public abstract class Moveable extends Texture
{
    protected float ratio;

    public void setRatio (float ratio)
    {
        this.ratio = ratio;
    }

    public abstract void initIcon (float posX, float posY, float scaleX, float scaleY);

    public boolean update ()
    {
        if (!super.alive)
        {
            return false;
        }

        // move sprite
        super.currentPos[0] = super.currentPos[0] + super.vector[0];
        super.currentPos[1] = super.currentPos[1] + super.vector[1];

        // if sprite moved off bottom of screen we can remove sprite
        if (super.currentPos[1] < -ratio)
        {
            super.alive = false;
        }

        return true;
    }
}
