package com.gergo.kovacs.a2dgame.utility;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class TiltInfo {
    public static TiltCalculator getCalculator(Context context) {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
           return new TiltCalculator(manager, accelerometer);
        } else {
            return null;
        }
    }
}