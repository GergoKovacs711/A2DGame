package com.gergo.kovacs.a2dgame.sprite.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SpritePool<T extends PoolableSprite>
{
    private Class<T> clazz;
    private List<T> pool;
    private Context context;
    private int lastSpawnedIndex = 0;
    private int maxSprites = 0;

    public SpritePool (Context context, int maxSprites, Class<T> clazz)
    {
        this.context = context;
        this.clazz = clazz;
        this.maxSprites = maxSprites;
        pool = new ArrayList<T>(maxSprites);

        for (int i = 0; i < maxSprites; i++)
        {
            pool.add(createSprite(context));
        }
    }

    private T createSprite (Context context)
    {
        try
        {
            T t = clazz.cast(clazz.newInstance());
            t.setContext(context);
            return t;
        }
        catch (InstantiationException e)
        {
            Timber.e("InstantiationException");
        }
        catch (IllegalAccessException e)
        {
            Timber.e("IllegalAccessException");
        }
        return null;
    }

    public T spawn ()
    {
        for (int i = lastSpawnedIndex + 1; i < maxSprites; i++)
        {
            T sprite = pool.get(i);
            if (!sprite.isInUse())
            {
                sprite.setInUse(true);
                lastSpawnedIndex = i;
                return sprite;
            }
        }
        for (int i = 0; i < lastSpawnedIndex; i++)
        {
            T sprite = pool.get(i);
            if (!sprite.isInUse())
            {
                sprite.setInUse(true);
                lastSpawnedIndex = i;
                return sprite;
            }
        }

        //expand pool
        T t = createSprite(context);
        t.setInUse(true);
        lastSpawnedIndex = pool.size();
        pool.add(t);
        return t;
    }

    public void kill (PoolableSprite sprite)
    {
        sprite.setInUse(false);
    }

    public List<T> getSprites ()
    {
        return pool;
    }

    public void clear ()
    {
        for (int i = 0; i < pool.size(); i++)
        {
            pool.get(i).setInUse(false);
        }
    }
}

