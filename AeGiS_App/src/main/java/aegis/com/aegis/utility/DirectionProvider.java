package aegis.com.aegis.utility;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Maxwell on 10/29/2015.
 */
public final class DirectionProvider implements SensorEventListener {
    // record the compass picture angle turned
    private static float currentDegree = 0f;

    // device sensor manager
    private static SensorManager mSensorManager;

    public DirectionProvider(Activity ac) {
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) ac.getSystemService(Context.SENSOR_SERVICE);
    }


    public void onResume() {
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                                        SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    public float getRotation() {
        return currentDegree;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = event.values[0];
        currentDegree = degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
