package com.gergo.kovacs.a2dgame.utility;

public class RNG {
    public static float randomFloatBetween (float lower, float upper)
    {
        if (lower > upper)
        {
            float tmp = lower;
            lower = upper;
            upper = tmp;
        }

        float diff = upper - lower;
        return lower + ((float) Math.random() * diff);
    }
}
