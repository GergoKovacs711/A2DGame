package com.gergo.kovacs.a2dgame.sprite;

import android.graphics.Color;
import android.opengl.Matrix;

import com.gergo.kovacs.a2dgame.sprite.util.Texture;

import dental.beam.gltext.GLText;

public class Text extends Texture
{
    public static final int TEXT_ALIGN_LEFT = 1;
    public static final int TEXT_ALIGN_CENTER = 2;
    public static final int TEXT_ALIGN_RIGHT = 3;
    public static final int TEXT_NO_ALIGN = 4;

    private final int defaultPaddingY = 30;
    private String text;
    private int textAlign;
    private int textSize;
    private GLText glText;
    private float width;
    private float margin;
    private float color[];

    public void setText (String text, int textAlign, float yOffset, int color)
    {
        this.text = text;
        setTextAlign(textAlign);
        currentPos[1] += yOffset;
        this.color = new float[4];
        this.color[0] = Color.red(color) / 255f;
        this.color[1] = Color.green(color) / 255f;
        this.color[2] = Color.blue(color) / 255f;
        this.color[3] = Color.alpha(color) / 255f;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void init (float ratio, float width, int textSize)
    {
        this.width = width;
        margin = width / 30f;
        currentPos[0] = 0;
        currentPos[1] = ratio;
        currentPos[2] = 0.1f;

        alive = false;

        this.textSize = textSize;
        if (glText == null)
        {
            loadText();
        }
    }

    private float textMatrix[] = new float[16];

    @Override
    public boolean update ()
    {
        return true;
    }

    @Override
    public void draw (float[] mvpMatrix)
    {
        System.arraycopy(mvpMatrix, 0, textMatrix, 0, 16);

        Matrix.translateM(textMatrix, 0, currentPos[0], currentPos[1], currentPos[2]);
        Matrix.scaleM(textMatrix, 0, 1 / width * 2, 1 / width * 2, 1);

        float x = 0;
        if (textAlign == TEXT_NO_ALIGN)
        {
            x = 0;
        }
        else if (textAlign == TEXT_ALIGN_LEFT)
        {
            x = -width / 2 + margin;
        }
        else if (textAlign == TEXT_ALIGN_RIGHT)
        {
            x = width / 2 - glText.getLength(text) - margin;
        }
        else if (textAlign == TEXT_ALIGN_CENTER)
        {
            x = -glText.getLength(text) / 2;
        }

        glText.begin(color[0], color[1], color[2], color[3], textMatrix);
        glText.draw(text, x, 0, 0, 0);
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

    public void setTextAlign (int textAlign)
    {
        this.textAlign = textAlign;
        if (this.textAlign != TEXT_NO_ALIGN)
        {
            currentPos[0] = 0;
        }
    }

    private void loadText(){
        glText = new GLText(context.getAssets());
        glText.load("ostrich-regular.ttf", textSize, 2, defaultPaddingY);
    }

    public void setAlpha(float alpha) throws IllegalArgumentException{

        if(alpha < 0f || alpha > 255f){
            throw new IllegalArgumentException("Alpha was: " + alpha);
        }

        this.color[3] = alpha / 255f;
    }

    public void setColor(float red, float green, float blue){
        this.color[0] = red;
        this.color[1] = green;
        this.color[2] = blue;

    }
}