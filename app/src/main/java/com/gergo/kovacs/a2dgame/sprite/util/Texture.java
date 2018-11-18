package com.gergo.kovacs.a2dgame.sprite.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.gergo.kovacs.a2dgame.Renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This sprite object draws a square with a texture
 */
public abstract class Texture implements PoolableSprite
{
    //opengl es

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 aTextureCoordinate;" +
                    "varying vec2 vTexCoordinate;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * uMVPMatrix;" +
                    "  vTexCoordinate = uMVPMatrix;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform sampler2D uTexture;" +
                    "varying vec2 vTexCoordinate;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTexture, vTexCoordinate); " +
                    "}";

    /** Mapping coordinates for the texture */
    private static final float TEXTURE_COORDINATES[] =
            {
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f
            };

    /** number of coordinates per vertex in this array */
    private static final int COORDS_PER_VERTEX = 3;

    /** Mapping coordinates for the sprite square */
    private static float SQUARE_COORDINATES[] =
            {
                    -1.0f, -1.0f, 0.0f,
                    1.0f, -1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    -1.0f, 1.0f, 0.0f
            };

    /** Order to draw the sprite square */
    private static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };

    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private static FloatBuffer _vertexBuffer;
    private static ShortBuffer _drawListBuffer;
    private static FloatBuffer _textureBuffer;

    protected static int _programHandle;

    protected static int positionHandle = -1;
    protected static int mvpMatrixHandle = -1;
    protected static int textureCoordinateHandle = -1;

    protected int textureDataHandle;
    protected int drawableResourceId;

    private static Map<Integer, Integer> drawableToTextureMap = new HashMap<>();
    private static Map<Integer, Integer> bitmapToTextureMap = new HashMap<>();

    //endregion

    // region sprite state
    protected float currentPos[] = new float[3];
    protected float vector[] = new float[3];

    /**
     * the center of a sprite can be adjusted for collision detection
     * values should be between -1 and 1
     */
    protected float center[] = new float[2];
    /**
     * the radius of a sprite can be adjusted for collision detection
     * values should be between 0 and 1
     */
    protected float radius = 1;

    protected float currentScale[] = new float[] { 1.0f, 1.0f };
    protected float rotationz = 0.0f;

    protected boolean alive = true;
    protected float imageRatio;

    protected boolean inUse;

    private float scratchMatrix[] = new float[16];
    // endregion

    protected Context context;

    protected Texture()
    {
    }

    public void setDrawable(int drawableResourceId)
    {
        this.drawableResourceId = drawableResourceId;
        textureDataHandle = loadGLTexture(drawableResourceId);
    }

    public void setContext (Context context)
    {
        this.context = context;
    }

    /** the buffers never need to change from sprite to sprite so share them statically */
    public static void initGlState ()
    {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(SQUARE_COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        _vertexBuffer = bb.asFloatBuffer();
        _vertexBuffer.put(SQUARE_COORDINATES);
        _vertexBuffer.position(0);

        // initialize texture byte buffer for texture coordinates
        bb = ByteBuffer.allocateDirect(TEXTURE_COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        _textureBuffer = bb.asFloatBuffer();
        _textureBuffer.put(TEXTURE_COORDINATES);
        _textureBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        _drawListBuffer = dlb.asShortBuffer();
        _drawListBuffer.put(DRAW_ORDER);
        _drawListBuffer.position(0);

        int vertexShader = Renderer.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
        _programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(_programHandle, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(_programHandle, fragmentShader); // add the fragment shader to program

        GLES20.glBindAttribLocation(_programHandle, 0, TEXTURE_COORDINATE_PARAM);
        GLES20.glLinkProgram(_programHandle);

        positionHandle = GLES20.glGetAttribLocation(_programHandle, POSITION_PARAM);
        textureCoordinateHandle = GLES20.glGetAttribLocation(_programHandle, TEXTURE_COORDINATE_PARAM);
        mvpMatrixHandle = GLES20.glGetUniformLocation(_programHandle, MVPMATRIX_PARAM);
    }

    /**
     * Update the state of the sprite every draw cycle
     *
     * @return true if sprite wants to live, false if it is done and is ready to die
     */
    public abstract boolean update ();

    public void batchDraw (float[] mvpMatrix, List<Texture> sprites)
    {
        // Add program to OpenGL environment
        GLES20.glUseProgram(_programHandle);

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, _vertexBuffer);

        // set texture
        _textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, _textureBuffer);

        int size = sprites.size();
        for (int i = 0; i < size; i++)
        {
            System.arraycopy(mvpMatrix, 0, scratchMatrix, 0, 16);

            Texture sprite = sprites.get(i);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sprite.textureDataHandle);

            // translate the sprite to it's current position
            Matrix.translateM(scratchMatrix, 0, sprite.currentPos[0], sprite.currentPos[1], sprite.currentPos[2]);
            // rotate the sprite
            Matrix.rotateM(scratchMatrix, 0, sprite.rotationz, 0, 0, 1.0f);
            // scale the sprite
            Matrix.scaleM(scratchMatrix, 0, sprite.currentScale[0], sprite.currentScale[1], 1);

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratchMatrix, 0);

            // Draw the sprite
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, _drawListBuffer);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
    }

    public void draw (float[] mvpMatrix)
    {
        System.arraycopy(mvpMatrix, 0, scratchMatrix, 0, 16);

        // Add program to OpenGL environment
        GLES20.glUseProgram(_programHandle);

        // Enable a handle to the vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, _vertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);

        _textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, _textureBuffer);

        // translate the sprite to it's current position
        Matrix.translateM(scratchMatrix, 0, currentPos[0], currentPos[1], currentPos[2]);
        // rotate the sprite
        Matrix.rotateM(scratchMatrix, 0, rotationz, 0, 0, 1.0f);
        // scale the sprite
        Matrix.scaleM(scratchMatrix, 0, currentScale[0], currentScale[1], 1);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, scratchMatrix, 0);

        // Draw the sprite
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, _drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
    }

    /**
     * Simple collision detection which considers each sprite a circle
     * Does calculation to see if the distance between two sprites is less than sum of the two sprites radii
     *
     * By default a sprite has a collision circle centered in the sprite with radius 1 but you can
     * adjust that if you want a smaller or offset collision circle
     * Note: Adjusts the center and radius using the scale of the sprite IN THE X DIRECTION
     */
    public boolean collidesWith (Texture other)
    {
        float dist = distance(
                currentPos[0] + center[0] * currentScale[0],
                currentPos[1] + center[1] * currentScale[0],
                other.currentPos[0] + other.center[0] * other.currentScale[0],
                other.currentPos[1] + other.center[1] * other.currentScale[1]);
        if (dist < radius * currentScale[0] + other.radius * other.currentScale[0])
        {
            return true;
        }
        return false;
    }

    float distance (float x1, float y1, float x2, float y2)
    {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static void clearTextureCache ()
    {
        drawableToTextureMap.clear();
        bitmapToTextureMap.clear();
    }

    /**
     * Load a texture from an android drawable
     */
    protected int loadGLTexture (int drawableResourceId)
    {
        Integer cachedTextureId = drawableToTextureMap.get(drawableResourceId);
        if (cachedTextureId != null)
        {
            return cachedTextureId;
        }

        // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
        int handle = loadGLTexture(bitmap);

        drawableToTextureMap.put(drawableResourceId, handle);

        // Clean up
        bitmap.recycle();

        return handle;
    }

    protected int loadGLTexture (Bitmap bitmap)
    {
        Integer cachedTextureId = bitmapToTextureMap.get(bitmap.hashCode());
        if (cachedTextureId != null)
        {
            return cachedTextureId;
        }

        imageRatio = bitmap.getWidth() / ((float) bitmap.getHeight());

        // generate one texture pointer and bind it to our handle
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // create nearest filtered texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);

        bitmapToTextureMap.put(bitmap.hashCode(), textureHandle[0]);

        return textureHandle[0];
    }

    public void reloadTexture ()
    {
        if (drawableResourceId != 0)
        {
            textureDataHandle = loadGLTexture(drawableResourceId);
        }
    }

    public boolean isInUse ()
    {
        return inUse;
    }

    public void setInUse (boolean inUse)
    {
        this.inUse = inUse;
    }

    public boolean isAlive ()
    {
        return alive;
    }

    public void setAlive (boolean alive)
    {
        this.alive = alive;
    }

    public float[] getPosition ()
    {
        return currentPos;
    }

    public float[] getVector ()
    {
        return vector;
    }

    public float[] getScale ()
    {
        return currentScale;
    }
}