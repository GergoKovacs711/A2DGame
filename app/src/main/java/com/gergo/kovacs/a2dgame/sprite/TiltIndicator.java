package com.gergo.kovacs.a2dgame.sprite;

import android.graphics.Color;
import android.opengl.Matrix;

import com.gergo.kovacs.a2dgame.sprite.util.Texture;

import dental.beam.gltext.GLText;

public class TiltIndicator extends Texture
{
    private float textMatrix[] = new float[16];


    private String text;
    private int textSize;
    private GLText glText;

    private float width;
    private final int defaultPaddingY = 30;
    private float color[];



    public TiltIndicator(float posX, float posY, float width, int textSize)
    {
        this.width = width;
        currentPos[0] = posX;
        currentPos[1] = posY;
        currentPos[2] = 0.1f;

        this.textSize = textSize;
        if (glText == null)
        {
            loadText();
        }
    }

    @Override
    public boolean update ()
    {
        return true;
    }

    @Override
    public void draw (float[] worldMatrix)
    {
        System.arraycopy(worldMatrix, 0, textMatrix, 0, 16);

        Matrix.translateM(textMatrix, 0, currentPos[0], currentPos[1], currentPos[2]);

        Matrix.scaleM(textMatrix, 0, 1 / width * 2, 1 / width * 2, 1);

        float x = currentPos[0] - glText.getLength(text) / 2;
        float y = currentPos[1] - (glText.getHeight() / 2);

        glText.begin(color[0], color[1], color[2], 1.0f, textMatrix);
        glText.draw(text, x, y, 0, 0);
        glText.end();
    }

    @Override
    public void reloadTexture ()
    {
        if (textSize > 0)
        {
            loadText();
        }
    }

    private void loadText(){
        glText = new GLText(context.getAssets());
        glText.load("ostrich-regular.ttf", textSize, 2, defaultPaddingY);
    }

    public void setText(String text)
    {

        this.text = text;
    }

    public void setText (String text, float yOffset, int color)
    {
        this.text = text;
        currentPos[1] += yOffset;
        this.color = new float[4];
        this.color[0] = Color.red(color) / 255f;
        this.color[1] = Color.green(color) / 255f;
        this.color[2] = Color.blue(color) / 255f;
        this.color[3] = Color.alpha(color) / 255f;
    }
}