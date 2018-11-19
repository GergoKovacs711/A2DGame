package com.gergo.kovacs.a2dgame.engine;

import timber.log.Timber;

public class FPSCounter {
    static private long lastFrame = System.nanoTime();
    static public float FPS = 0;

    public static float logFrame() {
        long time = (System.nanoTime() - lastFrame);
        FPS = 1/(time/1000000000.0f);
        lastFrame = System.nanoTime();
        Timber.d(Double.toString(FPS));
        return FPS;
    }

}
