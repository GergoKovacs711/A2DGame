package com.gergo.kovacs.a2dgame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gergo.kovacs.a2dgame.engine.GameEngine;
import com.gergo.kovacs.a2dgame.sprite.util.Texture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;

public class Renderer implements GLSurfaceView.Renderer
{
    private GameEngine gameEngine;

    private final float[] worldMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private long fpsTime = System.nanoTime();
    private int frames;

    @Override
    public void onSurfaceCreated (GL10 gl, EGLConfig config)
    {

        GLES20.glClearColor(0.09019f, 0.10588f, 0.33333f, 0.0f);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        Texture.initGlState();
        gameEngine.initSprites();
    }

    /**
     * Called when width/height are known
     * Use it to setup our coordinate range
     *  x-axis goes from -1=left edge to 1=right edge
     *  y-axis -X=bottom edge to X=top edge,
     *  	where X is the ratio of height/width.
     */
    @Override
    public void onSurfaceChanged (GL10 unused, int width, int height)
    {

        // Adjust the viewport
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) height / width;
        gameEngine.setRatio(ratio, width, height);

        Matrix.orthoM(projectionMatrix, 0, -1, 1, -ratio, ratio, 1, -1);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(worldMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame (GL10 unused)
    {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        gameEngine.drawFrame(worldMatrix);

        if (System.nanoTime() - fpsTime >= 1000000000)
        {
            frames = 0;
            fpsTime = System.nanoTime();
        }
        else
        {
            frames++;
        }
    }


    public void setGameEngine (GameEngine engine)
    {
        gameEngine = engine;
    }

    public static int loadShader (int type, String shaderCode)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
