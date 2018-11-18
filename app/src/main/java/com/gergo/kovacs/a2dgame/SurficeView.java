package com.gergo.kovacs.a2dgame;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.gergo.kovacs.a2dgame.engine.GameEngine;



public class SurficeView extends GLSurfaceView
{
    private com.gergo.kovacs.a2dgame.Renderer renderer;
    private GameEngine _engine;

    public SurficeView(Context context)
    {
        super(context);
        init();
    }

    public SurficeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init ()
    {

        setEGLContextClientVersion(2);

        setEGLConfigChooser(false);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        if (!isInEditMode())
        {
            renderer = new com.gergo.kovacs.a2dgame.Renderer();
            setRenderer(renderer);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        _engine = new GameEngine(getContext());
        renderer.setGameEngine(_engine);
    }

    public void exitGame ()
    {
        // run this on the glthread
        queueEvent(new Runnable()
        {
            @Override
            public void run ()
            {
                _engine.destroy();
            }
        });
    }

/*    public void setViewLocations (final Map<Integer, Rect> viewLocations)
    {
        // run this on the glthread
        queueEvent(new Runnable()
        {
            @Override
            public void run ()
            {
                _engine.setViewLocations(viewLocations);
            }
        });
    }*/

    public void startGame ()
    {
        // run this on the glthread
        queueEvent(new Runnable()
        {
            @Override
            public void run ()
            {
                _engine.startGame();
            }
        });
    }
}