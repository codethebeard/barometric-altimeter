package com.codethebeard.barometricaltimeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private String TAG = "BarometricAltimeter";

    private SensorManager mSensorManager;
    private Sensor mPressure;

    private float pressure;
    private double altSetting = 29.93;
    private double temp = 20.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView meterTextView = findViewById(R.id.altitude);
        EditText altSettingText = findViewById(R.id.altsetting);
        EditText tempSettingText = findViewById(R.id.temperature);

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE){
            pressure = event.values[0];
        } else {
            // TODO: Show dialog when barometric sensor isn't available.
            Log.e(TAG, "NO PRESSURE SENSOR");
        }

        if(!altSettingText.getText().toString().isEmpty()){
            altSetting = Double.parseDouble(altSettingText.getText().toString());

            if(altSetting <= 0){
                altSetting = 29.93;
            }
        }

        if(!tempSettingText.getText().toString().isEmpty()){
            temp = Double.parseDouble(tempSettingText.getText().toString());
        }

        double altitude = getAltitude(altSetting, pressure, temp);
        meterTextView.setText(String.format("%.1f ft" , metersToFeet(altitude)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private double inHgTohPa(double inHg){
        return inHg / 0.02953;
    }

    private double metersToFeet(double meters){
        return meters * 3.28084;
    }

    /**
     * Uses the Hypsometric equation to calculate the altitude
     *
     * @link https://physics.stackexchange.com/questions/333475/how-to-calculate-altitude-from-current-temperature-and-pressure/338016#338016
     * @param altimeterSetting in inches of Mercury
     * @param pressure The current barometric pressure in Hectopascals
     * @param temperature The current ambient temperature in Celsius
     * @return Calculated Altitude in Meters.
     */
    private double getAltitude(double altimeterSetting, double pressure, double temperature) {
        return ((pow((inHgTohPa(altimeterSetting) / pressure), 1/5.257) - 1.0) * (temperature + 273.15)) / 0.0065;
    }
}
