package com.gergo.kovacs.a2dgame.sprite.util;

import android.content.Context;

public interface PoolableSprite
{
    void setContext(Context context);
    boolean isInUse ();
    void setInUse (boolean inUse);
}

