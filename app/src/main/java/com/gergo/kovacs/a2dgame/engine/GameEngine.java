package com.gergo.kovacs.a2dgame.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;

import com.gergo.kovacs.a2dgame.R;
import com.gergo.kovacs.a2dgame.sprite.Coin;
import com.gergo.kovacs.a2dgame.sprite.Enemy;
import com.gergo.kovacs.a2dgame.sprite.KilledEnemy;
import com.gergo.kovacs.a2dgame.sprite.Player;
import com.gergo.kovacs.a2dgame.sprite.Text;
import com.gergo.kovacs.a2dgame.sprite.DestroyedCoin;
import com.gergo.kovacs.a2dgame.sprite.util.Moveable;
import com.gergo.kovacs.a2dgame.sprite.util.SpritePool;
import com.gergo.kovacs.a2dgame.sprite.util.Texture;
import com.gergo.kovacs.a2dgame.utility.RNG;
import com.gergo.kovacs.a2dgame.utility.TiltCalculator;
import com.gergo.kovacs.a2dgame.utility.TiltInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GameEngine {
    Context context;

    private TiltCalculator tiltCalculator;

    //region opengl drawable objects
    private Player player;

    private SpritePool<Coin> coinPool;
    private List<Coin> coins = new ArrayList<>(20);

    private SpritePool<Enemy> enemyPool;
    private List<Enemy> enemies = new ArrayList<>(100);

    private SpritePool<KilledEnemy> killedEnemyPool;
    private List<KilledEnemy> killedEnemies = new ArrayList<>(20);

    private SpritePool<DestroyedCoin> destroyedCoinPool;
    private List<DestroyedCoin> destroyedCoins = new ArrayList<>(5);

    private Enemy enemyIcon;
    private Text enemyCountText;

    private Text endOfGameText;
    private Text highScoreText;
    private Text yourScoreText;
    private Text touchToRestartText;
    //private Text tiltIndicator;
    //private Text fpsIndicator;
    private Text coinIndicator;
    private Coin coinIcon;

    private Text scoreIndicator;
    private Enemy scoreIcon;
    //endregion

    //region game state
    private boolean playing = false;
    private boolean gameOverSetupDone = false;

    private float restartTextTargetAlpha = 254f;
    private boolean increaseAlpha = false;

    private boolean newHighScore = false;

    private int frames;
    private float fps;
    private long startTime;

    private int currentScore = 0;
    private int framesBetweenAddingEnemies = 64;

    private long highScore;
    private boolean changeCoinSpeed = false;


    //endregion

    //region opengl stuff
    private float ratio;
    private float width;
    private float height;

    Map<Integer, Rect> viewLocations;

    //endregion

    public GameEngine(Context context) {
        this.context = context;

        coinPool = new SpritePool<>(context, 5, Coin.class);
        enemyPool = new SpritePool<>(context, 100, Enemy.class);
        killedEnemyPool = new SpritePool<>(context, 100, KilledEnemy.class);
        destroyedCoinPool = new SpritePool<>(context, 5, DestroyedCoin.class);

        startGame();
    }

    private void loadHighScore() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        highScore = sharedPref.getLong(
                context.getResources().getString(R.string.high_score_key), 0);
    }

    private void saveHighScore(long newHighScore) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_preference_data), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.high_score_key), newHighScore);
        editor.apply();
    }

    public void initSprites() {
        Texture.clearTextureCache();

        // player Init
        if (player == null) {
            tiltCalculator = TiltInfo.getCalculator(context);
            player = new Player(context, R.drawable.player, tiltCalculator);
        } else {
            player.reloadTexture();
        }

        // coins Init
        int coinsCount = coinPool.getSprites().size();
        for (int i = 0; i < coinsCount; i++) {
            Coin coin = coinPool.getSprites().get(i);
            coin.reloadTexture();
        }

       /* // destroyed coins Init
        int destroyedCoinsCount = destroyedCoinPool.getSprites().size();
        for (int i = 0; i < destroyedCoinsCount; i++) {
            DestroyedCoin destroyedCoin = destroyedCoinPool.getSprites().get(i);
            destroyedCoin.reloadTexture();
        }*/


        // enemy Init
        int currentEnemyCount = enemyPool.getSprites().size();
        for (int i = 0; i < currentEnemyCount; i++) {
            Enemy enemy = enemyPool.getSprites().get(i);
            enemy.reloadTexture();
        }

        /*// killed enemy Init
        int currentkilledEnemyCount = killedEnemyPool.getSprites().size();
        for (int i = 0; i < currentkilledEnemyCount; i++) {
            KilledEnemy killedEnemy = killedEnemyPool.getSprites().get(i);
            killedEnemy.reloadTexture();
        }*/

        // end of game text Init
        if (endOfGameText == null) {
            endOfGameText = new Text();
            endOfGameText.setContext(context);
            endOfGameText.setText("Game Over", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            endOfGameText.reloadTexture();
        }

        // highscore text Init
        if (highScoreText == null) {
            highScoreText = new Text();
            highScoreText.setContext(context);
            highScoreText.setText("High Score", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            highScoreText.reloadTexture();
        }


        if (yourScoreText == null) {
            yourScoreText = new Text();
            yourScoreText.setContext(context);
            yourScoreText.setText("Your Score", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            yourScoreText.reloadTexture();
        }

        // restart text Init
        if (touchToRestartText == null) {
            touchToRestartText = new Text();
            touchToRestartText.setContext(context);
            touchToRestartText.setText("Tap to restart", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            touchToRestartText.reloadTexture();
        }


        if (coinIndicator == null) {
            coinIndicator = new Text();
            coinIndicator.setContext(context);
            coinIndicator.setText("coinsCount", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            coinIndicator.reloadTexture();
        }

        if (coinIcon == null) {
            coinIcon = coinPool.spawn();
        } else {
            coinIcon.reloadTexture();
        }

        if (scoreIndicator == null) {
            scoreIndicator = new Text();
            scoreIndicator.setContext(context);
            scoreIndicator.setText("0", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        } else {
            scoreIndicator.reloadTexture();
        }

        if (scoreIcon == null) {
            scoreIcon = enemyPool.spawn();
            scoreIcon.setResource(R.drawable.score_icon);
        } else {
            scoreIcon.reloadTexture();
        }

        /*// tilt indicator Init
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

        // FPS indicator Init
        if (fpsIndicator == null)
        {
            fpsIndicator = new Text();
            fpsIndicator.setContext(context);
            fpsIndicator.setText("0", Text.TEXT_ALIGN_CENTER, 0, Color.WHITE);
        }
        else
        {
            fpsIndicator.reloadTexture();
        }*/

    }

    public void setViewLocations(Map<Integer, Rect> viewLocations) {
        this.viewLocations = viewLocations;
        if (ratio != 0) {
            initHUDText(coinIndicator, Integer.toString(coinPool.getSprites().size()),
                    Text.TEXT_NO_ALIGN, ratio, R.id.coin_counter);
            initHUDText(scoreIndicator, "0",
                    Text.TEXT_NO_ALIGN, ratio, R.id.score_counter);

            initHUDIcon(coinIcon, ratio, R.id.counter_icon);
            initHUDIcon(scoreIcon, ratio, R.id.score_icon);

           /* initHUDText(tiltIndicator, Double.toString(tiltCalculator.getTilt()),
                    Text.TEXT_NO_ALIGN, ratio, R.id.tilt_counter_text);
            initHUDText(fpsIndicator, "0",
                    Text.TEXT_NO_ALIGN, ratio, R.id.fps_text);*/
        }
    }

    private void initHUDText(Text text, String strText, int textAlign, float ratio, int id) {
        float coords[] = new float[2];

        text.init(ratio, width, Math.round(context.getResources().getDimension(R.dimen.counter_text_size)));
        text.setText(strText, textAlign, 0, Color.WHITE);

        convertAndroidLocationToGL(coords, viewLocations.get(id));
        if (textAlign == Text.TEXT_NO_ALIGN) {
            text.getPosition()[0] = coords[0];
        }
        text.getPosition()[1] = coords[1];
    }

    private void initHUDIcon(Moveable icon, float ratio, int id) {
        float coords[] = new float[2];
        float scale[] = new float[2];

        icon.setRatio(ratio);
        Rect position = viewLocations.get(id);
        convertAndroidLocationToGLCentered(coords, position);
        convertAndroidLocationToGLScale(scale, position);
        icon.initIcon(coords[0], coords[1], scale[0], scale[1]);
    }

    private void convertAndroidLocationToGLCentered(float glPos[], Rect androidLocation) {
        glPos[0] = androidLocation.centerX() / width * 2 - 1;
        float yPercentage = androidLocation.centerY() / height;
        float yPercentageGl = yPercentage * 2 * ratio;
        glPos[1] = -(yPercentageGl - ratio);
    }

    private void convertAndroidLocationToGL(float glPos[], Rect androidLocation) {
        glPos[0] = androidLocation.left / width * 2 - 1;
        float yPercentage = androidLocation.bottom / height;
        float yPercentageGl = yPercentage * 2 * ratio;
        glPos[1] = -(yPercentageGl - ratio);
    }

    private void convertAndroidLocationToGLScale(float glScale[], Rect androidLocation) {
        glScale[0] = androidLocation.width() / width;
        glScale[1] = androidLocation.width() / width;
    }

    /**
     * ratio is height/width of screen which affects where in Y coordinate to place sprites
     */
    public void setRatio(float ratio, float width, float height) {
        this.ratio = ratio;
        this.width = width;
        this.height = height;

        player.setRatio(ratio);

        int coinCount = coinPool.getSprites().size();
        for (int i = 0; i < coinCount; i++) {
            Coin coin = coinPool.getSprites().get(i);
            coin.setRatio(ratio);
        }

        endOfGameText.init(ratio, this.width, Math.round(context.getResources()
                .getDimension(R.dimen.end_of_game_text_size)));
        endOfGameText.getPosition()[1] = .2f;

        highScoreText.init(ratio, this.width, Math.round(context.getResources()
                .getDimension(R.dimen.high_score_text_size)));
        highScoreText.getPosition()[1] = 0f;

        yourScoreText.init(ratio, this.width, Math.round(context.getResources()
                .getDimension(R.dimen.your_score_text_size)));
        yourScoreText.getPosition()[1] = -.2f;

        touchToRestartText.init(ratio, this.width, Math.round(context.getResources()
                .getDimension(R.dimen.touch_to_restart_text_size)));
        touchToRestartText.getPosition()[1] = -.4f;

        setViewLocations(viewLocations);
    }

    public void drawFrame(float matrix[]) {

        if (playing) {
            if (coins.isEmpty()) {
                gameOver();
            } else {
                update();
                drawEnemies(matrix);
                drawCoins(matrix);

                player.draw(matrix);

                scoreIndicator.draw(matrix);
                scoreIcon.draw(matrix);

                coinIndicator.draw(matrix);
                coinIcon.draw(matrix);

            /*tiltIndicator.draw(matrix);
            fpsIndicator.draw(matrix);*/
            }
        } else {
            if (!gameOverSetupDone) {
                if (currentScore > highScore) {
                    saveHighScore(currentScore);
                    highScore = currentScore;
                    newHighScore = true;
                }
                highScoreText.setText("High Score: " + highScore);
                yourScoreText.setText("Your Score: " + currentScore);

                highScoreText.setColor(1f, 1f, 1f);
                yourScoreText.setColor(1f, 1f, 1f);
                gameOverSetupDone = true;
            }

            flashRestartText();
            touchToRestartText.setAlpha(restartTextTargetAlpha);

            if(newHighScore && frames % 20 == 0){
                changeHighScoreTextColor();
            }

            endOfGameText.draw(matrix);
            highScoreText.draw(matrix);
            yourScoreText.draw(matrix);
            touchToRestartText.draw(matrix);
            frames++;
        }

        fps = FPSCounter.logFrame();
    }

    public void update() {
        if (frames % (framesBetweenAddingEnemies / 2) == 0) {
            currentScore += 1;
        }

        updateEnemies();

        if (frames++ % framesBetweenAddingEnemies == 0) {
            Enemy enemy = spawnEnemy();
            enemy.initRandom();
            addEnemy(enemy);
            changeCoinSpeed = true;
        }

        updateCoins(changeCoinSpeed);
        changeCoinSpeed = false;
        player.update();

        coinIndicator.setText(Integer.toString(coins.size()));
        scoreIndicator.setText(Integer.toString(currentScore));
       /* tiltIndicator.setText(Double.toString(tiltCalculator.getTilt()));
        fpsIndicator.setText(Double.toString(fps));*/

        if (frames % 100 == 0 & framesBetweenAddingEnemies > 15) {
            framesBetweenAddingEnemies--;
        }


    }

    private void updateCoins(boolean changeSpeed) {
        boolean changeThisCoinsSpeed = true;

        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);

            if (RNG.randomBoolean() && changeSpeed) {
                changeThisCoinsSpeed = true;
            } else {
                changeThisCoinsSpeed = false;
            }

            if (!coin.update(changeThisCoinsSpeed)) {
                coinPool.kill(coin);
                coins.remove(i--);
            }
        }

        for (int i = 0; i < destroyedCoins.size(); i++) {
            DestroyedCoin destroyedCoin = destroyedCoins.get(i);

            if (!destroyedCoin.update()) {
                destroyedCoinPool.kill(destroyedCoin);
                destroyedCoins.remove(i--);
            }
        }
    }

    private void drawCoins(float matrix[]) {
        if (coins.size() > 0) {
            coins.get(0).batchDraw(matrix, (List<Texture>) ((Object) coins));
        }

        if (destroyedCoins.size() > 0) {
            destroyedCoins.get(0).batchDraw(matrix, (List<Texture>) ((Object) destroyedCoins));
        }

    }

    public Enemy spawnEnemy() {
        return enemyPool.spawn();
    }

    public void addEnemy(Enemy enemy) {
        enemy.setRatio(ratio);
        enemies.add(enemy);
    }

    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            boolean collided = false;
            if (enemy.collidesWith(player)) {
                collided = true;
                //enemyCountText.setText(Integer.toString(++enemyCount));
                killEnemy(enemy);
                enemyPool.kill(enemy);
                enemies.remove(i--);
                currentScore += 10;
            } else {
                for (int j = 0; j < coins.size(); j++) {
                    Coin coin = coins.get(j);
                    if (enemy.collidesWith(coin)) {
                        destroyCoin(coin, enemy);
                        coinPool.kill(coin);
                        coins.remove(j--);
                    }
                }
            }

            if (!collided && !enemy.update()) {
                enemyPool.kill(enemy);
                enemies.remove(i--);
            }
        }

        for (int i = 0; i < killedEnemies.size(); i++) {
            KilledEnemy killedEnemy = killedEnemies.get(i);
            if (!killedEnemy.update()) {
                killedEnemyPool.kill(killedEnemy);
                killedEnemies.remove(i--);
            }
        }
    }

    private void drawEnemies(float matrix[]) {
        if (enemies.size() > 0) {
            enemies.get(0).batchDraw(matrix, (List<Texture>) ((Object) enemies));
        }

        if (killedEnemies.size() > 0) {
            killedEnemies.get(0).batchDraw(matrix, (List<Texture>) ((Object) killedEnemies));
        }
    }

    private void killEnemy(Enemy enemy) {
        float origPosition[] = enemy.getPosition();
        float origVector[] = enemy.getVector();
        float origScale[] = enemy.getScale();
        float newScale = origScale[0] / 4;
        float p[] = new float[]{
                origScale[1] * 1 / 4,
                origScale[1] * 1 / 2,
                origScale[1],
                origScale[1] * 1 / 2,
                origScale[1] * 1 / 4
        };
        for (int i = 0; i < 5; i++) {
            float newPosition[] = new float[3];
            float newVector[] = new float[3];
            newPosition[1] = origPosition[1] + p[i]; //move it up a bit
            newVector[1] = (float) (origVector[1] * (0.8f + 0.2f * Math.random())); //slow it's speed a bit

            if (i < 2) {
                newPosition[0] = origPosition[0] - (float) (0.06f * Math.random());
                newVector[0] = -(float) (Math.random() * .01f);
            } else if (i == 2) {
                newPosition[0] = origPosition[0];
                newVector[0] = 0;
            } else {
                newPosition[0] = origPosition[0] + (float) (0.06f * Math.random());
                newVector[0] = (float) (Math.random() * .01f);
            }

            KilledEnemy killedEnemy = killedEnemyPool.spawn();
            killedEnemy.init(ratio, newPosition, newVector, newScale);
            killedEnemies.add(killedEnemy);
        }
    }

    private void destroyCoin(Coin coin, Enemy enemy) {

        DestroyedCoin destroyedCoin = destroyedCoinPool.spawn();
        destroyedCoin.init(ratio, coin.getPosition(), enemy.getVector(), coin.getScale()[0]);
        destroyedCoins.add(destroyedCoin);
    }

    public void startGame() {
        if (playing) {
            return;
        }

        loadHighScore();
        clearObjects();

        playing = true;
        gameOverSetupDone = false;
        newHighScore = false;
        frames = 0;
        startTime = System.currentTimeMillis();

        currentScore = 0;
        framesBetweenAddingEnemies = 64;

        for (int i = 0; i < 5; i++) {
            Coin coin = coinPool.spawn();
            coin.init();
            coin.setRatio(ratio);
            coins.add(coin);
        }
    }

    public void gameOver() {
        playing = false;
    }

    private void clearObjects() {
        if (!coins.isEmpty()) {
            coins.clear();
        }
        if (!enemies.isEmpty()) {
            enemies.clear();
        }
        if (!killedEnemies.isEmpty()) {
            killedEnemies.clear();
        }
        if (!destroyedCoins.isEmpty()) {
            destroyedCoins.clear();
        }
    }

    public void destroy() {
        if (tiltCalculator != null) {
            tiltCalculator.destroy();
        }
    }

    private void flashRestartText(){
        if(increaseAlpha){
            restartTextTargetAlpha += 3f;
            if(restartTextTargetAlpha >= 255f){
                increaseAlpha = false;
                restartTextTargetAlpha -= 20f;
            }
        }
        else {
            restartTextTargetAlpha -= 4.5f;
            if(restartTextTargetAlpha <= 80f){
                increaseAlpha = true;
                restartTextTargetAlpha += 30f;
            }
        }
    }

    private void changeHighScoreTextColor(){
        float red, green, blue;
        red = RNG.randomFloatBetween(0f, 1f);
        green = RNG.randomFloatBetween(0f, 1f);
        blue = RNG.randomFloatBetween(0f, 1f);

        highScoreText.setColor(red, green, blue);
        yourScoreText.setColor(red, green, blue);
    }

}
