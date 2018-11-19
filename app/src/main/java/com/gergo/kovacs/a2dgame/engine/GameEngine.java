package com.gergo.kovacs.a2dgame.engine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.sprite.Player;
import com.gergo.kovacs.a2dgame.sprite.Text;
import com.gergo.kovacs.a2dgame.sprite.util.Texture;
import com.gergo.kovacs.a2dgame.utility.TiltCalculator;
import com.gergo.kovacs.a2dgame.utility.TiltInfo;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;


public class GameEngine
{
    Context context;

    private TiltCalculator tiltCalculator;

    //region opengl drawable objects
    private Player player;
    private Text endOfGameText;
    private Text tiltIndicator;
    private Text fpsIndicator;
    //endregion

    //region game state
    private boolean playing = false;
    private int frames;
    private float fps;
    private long startTime;
    //endregion

    //region opengl stuff
    private float ratio;
    private float width;
    private float height;

    Map<Integer, Rect> viewLocations;

    //endregion

    public GameEngine (Context context)
    {
        this.context = context;
        startGame();
    }

    public void initSprites ()
    {
        Texture.clearTextureCache();


        if (player == null)
        {
            tiltCalculator = TiltInfo.getCalculator(context);
            player = new Player(context, R.drawable.player, tiltCalculator);
        }
        else
        {
            player.reloadTexture();
        }

        if (endOfGameText == null)
        {
            endOfGameText = new Text();
            endOfGameText.setContext(context);
            endOfGameText.setText("Game Over", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        }
        else
        {
            endOfGameText.reloadTexture();
        }

        if (tiltIndicator == null)
        {
            tiltIndicator = new Text();
            tiltIndicator.setContext(context);
            tiltIndicator.setText("90", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        }
        else
        {
            tiltIndicator.reloadTexture();
        }

        if (fpsIndicator == null)
        {
            fpsIndicator = new Text();
            fpsIndicator.setContext(context);
            fpsIndicator.setText("0", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        }
        else
        {
            fpsIndicator.reloadTexture();
        }

    }

    public void setViewLocations (Map<Integer, Rect> viewLocations)
    {
        this.viewLocations = viewLocations;
        if (ratio != 0)
        {
            initHUDText(tiltIndicator, Double.toString(tiltCalculator.getTilt()),
                    Text.TEXT_NO_ALIGN, ratio, R.id.tilt_counter_text);
            initHUDText(fpsIndicator, "0",
                    Text.TEXT_NO_ALIGN, ratio, R.id.fps_text);
        }
    }

    private void initHUDText (Text text, String strText, int textAlign, float ratio, int id)
    {
        float coords[] = new float[2];

        text.init(ratio, width, Math.round(context.getResources().getDimension(R.dimen.placeholder1_text_size)));
        text.setText("asdasdasd", textAlign, 0, Color.WHITE);

        convertAndroidLocationToGL(coords, viewLocations.get(id));
        if (textAlign == Text.TEXT_NO_ALIGN)
        {
            text.getPosition()[0] = coords[0];
        }
        text.getPosition()[1] = coords[1];
    }

    private void convertAndroidLocationToGL (float glPos[], Rect androidLocation)
    {
        glPos[0] = androidLocation.left / width * 2 - 1;
        float yPercentage = androidLocation.bottom / height;
        float yPercentageGl = yPercentage * 2 * ratio;
        glPos[1] = -(yPercentageGl - ratio);
    }

    /** ratio is height/width of screen which affects where in Y coordinate to place sprites */
    public void setRatio (float ratio, float width, float height)
    {
        this.ratio = ratio;
        this.width = width;
        this.height = height;

        player.setRatio(ratio);

        endOfGameText.init(ratio, this.width, Math.round(context.getResources().getDimension(R.dimen.end_of_game_text_size)));
        endOfGameText.getPosition()[1] = 0f;

        setViewLocations(viewLocations);
    }

    public void drawFrame (float matrix[])
    {

        update();

        player.draw(matrix);
        tiltIndicator.draw(matrix);
        fpsIndicator.draw(matrix);

        if (!playing)
        {
            endOfGameText.draw(matrix);
        }

        fps = FPSCounter.logFrame();
    }

    public void update()
    {

        player.update();
        tiltIndicator.setText(Double.toString(tiltCalculator.getTilt()));
        fpsIndicator.setText(Double.toString(fps));

    }

    public void startGame()
    {
        if (playing)
        {
            return;
        }

        playing = true;
        frames = 0;
        startTime = System.currentTimeMillis();
    }

    public void gameOver()
    {
        playing = false;
    }


    public void destroy ()
    {
        if (tiltCalculator != null)
        {
            tiltCalculator.destroy();
        }
    }

}
