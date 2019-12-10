package com.example.myfinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorMan;
    private Sensor accelerometer;
    private Chronometer timer;

    private boolean running;
    private long pauseOffSet;

    private float[] gravity;
    private float acceleration;
    private float currentA;
    private float lastA;

    private int resetVisibility = -1;
    private int volumeVisibility = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        acceleration = 0.00f;
        currentA = SensorManager.GRAVITY_EARTH;
        lastA = SensorManager.GRAVITY_EARTH;
        final Switch on = findViewById(R.id.On);
        final Button reset = findViewById(R.id.Reset);
        final ProgressBar volume = findViewById(R.id.Volume);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    }

    //from https://stackoverflow.com/questions/14574879/how-to-detect-movement-of-an-android-device
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onResume() {
        super.onResume();
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }
    protected void onPause() {
        super.onPause();
        sensorMan.unregisterListener(this);
    }
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            gravity = event.values.clone();
            // Shake detection
            float x = gravity[0];
            float y = gravity[1];
            float z = gravity[2];
            lastA = currentA;
            currentA = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = currentA - lastA;
            acceleration = acceleration * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(acceleration > 3){
                startTimer();
            } else  if (acceleration < 0 && acceleration > -9.81) {
                stop();
            } else {
                stopTimer();
            }
        }
    }
    //


    //from https://www.youtube.com/watch?v=RLnb4vVkftc
    public void startTimer() {
        timer = findViewById(R.id.Timer);
        if (!running) {
            timer.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
            timer.start();
            running = true;
        }
    }
    public void stopTimer() {
        timer = findViewById(R.id.Timer);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (running) {
            timer.stop();
            pauseOffSet = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
            final ProgressBar volume = findViewById(R.id.Volume);
            int finalVol = Math.abs((int)pauseOffSet);
            int x = (int) Math.round(finalVol * 0.03);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(x/6.7), (int) Math.round(x/6.7));
            volume.setProgress(x);
        }
    }
    public void resetTimer() {
        timer = findViewById(R.id.Timer);
        timer.setBase(SystemClock.elapsedRealtime());
        pauseOffSet = 0;
    }
    public void stop() {
        timer = findViewById(R.id.Timer);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (running) {
            timer.stop();
            pauseOffSet = SystemClock.elapsedRealtime() - timer.getBase();
            running = false;
            final ProgressBar volume = findViewById(R.id.Volume);
            int finalVol = Math.abs((int)pauseOffSet);
            int x = (int) Math.round(finalVol * 0.03);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(x/6.7), (int) Math.round(x/6.7));
            volume.setProgress(x);
        }
    }
    //
}

