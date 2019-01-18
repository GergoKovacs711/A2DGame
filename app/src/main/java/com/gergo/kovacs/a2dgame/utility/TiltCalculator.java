package com.gergo.kovacs.a2dgame.utility;

import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;

import timber.log.Timber;


public class TiltCalculator implements SensorEventListener
{
    private static final float TILT_SCALER = 100.0f;

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

        float amountOfTilt = acceleration[0] / accelerometer.getMaximumRange();
        return - (amountOfTilt * TILT_SCALER);
    }

    public void destroy ()
    {
        sensorManager.unregisterListener(this);
    }
}
