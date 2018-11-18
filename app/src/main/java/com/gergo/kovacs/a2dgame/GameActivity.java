package com.gergo.kovacs.a2dgame;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.gergo.kovacs.a2dgame.sprite.util.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


public class GameActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener
{
    @BindView(R.id.root_layout)
    View root;

    @BindView(R.id.game_view)
    SurficeView surfaceView;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        root.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
        surfaceView.onResume();

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toggleFullscreen();
    }

    @Override
    protected void onPause ()
    {
        surfaceView.onPause();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onPause();
    }

    @Override
    protected void onDestroy ()
    {

        Texture.clearTextureCache();
        surfaceView.exitGame();
        super.onDestroy();
    }

    @OnClick(R.id.game_view)
    public void clickGame()
    {
        surfaceView.startGame();
    }


    /**
     * when the surfaceView is laid out, figure out where the icons are
     * and tell the opengl code about that location so it can convert it to opengl coordinates
     */
    @Override
    public void onGlobalLayout ()
    {
//        surfaceView.setViewLocations(getViewLocations());
    }

    private Map<Integer, Rect> getViewLocations ()
    {
        Map<Integer, Rect> locations = new HashMap<>();
        ArrayList<View> views = getAllChildren(root);
        for (View view : views)
        {
            locations.put(view.getId(), getLocationInsideRoot(view));
        }
        return locations;
    }

    private ArrayList<View> getAllChildren (View v)
    {

        if (!(v instanceof ViewGroup))
        {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    private Rect getLocationInsideRoot (View view)
    {
        Rect rootLocation = new Rect();
        root.getGlobalVisibleRect(rootLocation);

        Rect r = new Rect();
        view.getGlobalVisibleRect(r);
        r.left -= rootLocation.left;
        r.top -= rootLocation.top;
        r.right -= rootLocation.left;
        r.bottom -= rootLocation.top;
        return r;
    }

    private void toggleFullscreen ()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {

                getWindow().getDecorView()
                        .setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }
}
