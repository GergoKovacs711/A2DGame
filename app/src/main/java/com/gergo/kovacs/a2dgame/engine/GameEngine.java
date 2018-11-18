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

import java.util.Map;


public class GameEngine
{
    Context context;

    private TiltCalculator tiltCalculator;

    //region opengl drawable objects
    private Player player;
    private Text endOfGameText;
    //endregion

    //region game state
    private boolean playing = false;
    private int frames;
    //endregion

    //region opengl stuff
    private float ratio;
    private float width;
    private float height;

    Map<Integer, Rect> _viewLocations;
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
    }

    /** ratio is height/width of screen which affects where in Y coordinate to place sprites */
    public void setRatio (float ratio, float width, float height)
    {
        this.ratio = ratio;
        this.width = width;
        this.height = height;

        player.setRatio(ratio);

        endOfGameText.init(ratio, this.width, Math.round(context.getResources().getDimension(R.dimen.end_of_game_text_size)));
        endOfGameText.getPosition()[1] = 0;
    }

    public void drawFrame (float matrix[])
    {

        update();

        player.draw(matrix);

        if (!playing)
        {
            endOfGameText.draw(matrix);
        }
    }

    public void update()
    {

        player.update();
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
