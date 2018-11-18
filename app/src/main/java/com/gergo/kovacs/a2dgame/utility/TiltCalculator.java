package com.gergo.kovacs.a2dgame.utility;

import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;



public class TiltCalculator implements SensorEventListener
{
    private static final float TILT_MAX = 90.0f;

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private float[] acceleration;

    protected TiltCalculator (SensorManager sensorManager, Sensor accelerometer)
    {
        this.sensorManager = sensorManager;
        this.accelerometer = accelerometer;

        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged (SensorEvent event)
    {
        if (event.values == null) {
            return;
        }

        int sensorType = event.sensor.getType();
        switch (sensorType)
        {
            case Sensor.TYPE_ACCELEROMETER:
                acceleration = event.values;
                break;
            default:
                return;
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) { }


    public float getTilt ()
    {
        if (acceleration == null)
        {
            return 0;
        }

        // We're going to assume that the user isn't waving their phone around like a maniac so the
        // only force acting on it should be gravity. This means that the sum of the absolute values
        // of all axes should be roughly equal to 9.8 so we should be able to see how much fo that
        // is taken up by the y axis. What ever is left is available for the x/z tilt we're interested in.
        // Then it just becomes a simple ratio to see how much of the space we have left is taken up
        // by the pull of gravity on the x axis.
        float availableRange = acceleration[1] - accelerometer.getMaximumRange();
        float amountOfTile = acceleration[0] / availableRange;
        return amountOfTile * TILT_MAX;
    }

    public void destroy ()
    {
        sensorManager.unregisterListener(this);
    }
}
