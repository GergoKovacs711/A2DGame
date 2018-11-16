package com.gergo.kovacs.a2dgame.engine;

import android.content.Context;

public class GameEngine {
    Context context;

    //region game state
    private boolean playing = false;
    private int frames;
    //endregion

    private float ratio;
    private float width;
    private float height;

    public GameEngine (Context context)
    {
        this.context = context;
        startGame();
    }

    public void startGame()
    {
        if (playing)
        {
            return;
        }

        playing = true;
        frames = 0;
    }

    public void setRatio (float ratio, float width, float height)
    {

    }

}
