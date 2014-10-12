package com.hackru.ruwatch.ruwatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.*;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class MyActivity extends Activity implements SensorEventListener {
    private TextView mTextView;
    private SensorManager sensorManager; // Sensor
    Sensor heartRateMonitor;
    private double heartRate;

    private Handler handler = new Handler(); // Timer
    private int seconds;
    private boolean started = false;

    private int calories;

    private User user;
    private String userID; // Data, read from Facebook...
    private boolean isMale;
    private int age;
    private int weight;
    private int goalCalories;
    MobileServiceClient mClient = new MobileServiceClient("MobileServiceURL","AppKey",this);
    MobileServiceTable<ToDoItem> mToDoTable = mClient.getTable(ToDoItem.class);

    // What is called each time the Handler "ticks"
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds += 1;

            TextView txt = (TextView) findViewById(R.id.text);
            int minutes = (int) (seconds / 60);
            seconds %= 60;
            int hours = (int) (minutes / 60);
            minutes %= 60;

            DecimalFormat fmt = new DecimalFormat("00");
            txt.setText(fmt.format(hours) + ":" + fmt.format(minutes) + ":" + fmt.format(seconds));

            handler.postDelayed(this, 1000); //repeats after another 1000 ms
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) { // What is called in the beginning
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //set up heart rate sensor
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        heartRateMonitor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(this, heartRateMonitor, SensorManager.SENSOR_DELAY_NORMAL);

        // set up message receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        ListenerService listenerService= new ListenerService();
        listenerService.onMessageReceived();

        // getting the data from online using userID
        mToDoTable.where().field("userID").eq(false)
                .execute(new TableQueryCallback<ToDoItem>() {
                    public void onCompleted(List<ToDoItem> result,
                                            int count,
                                            Exception exception,
                                            ServiceFilterResponse response) {
                        if (exception == null) {
                        }
                    }
                });
    }

    // Sensor methods and stuff
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            getHeartRate(event);
        }
    }

    public void getHeartRate(SensorEvent event) {
        float[] tmp = event.values;
        heartRate = (int) event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the Heart Rate
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // Button control
    public void controlOnClick(View v) {
        if (started == false) {
            start();
            started = true;
        } else {
            end();
        }
    }

    public void start() {
        handler.postDelayed(runnable, 1000); //starts after 1000 ms
        ImageButton button = (ImageButton) findViewById(R.id.control);
        button.setImageResource(R.drawable.stop);
        TextView txt = (TextView) findViewById(R.id.text);
        txt.setText("00:00:00");
        txt.setTextSize((float) 40);
    }

    public void end() {
        handler.removeCallbacks(runnable); // Stop the handler
        TextView txt = (TextView) findViewById(R.id.text);
        ImageButton button = (ImageButton) findViewById(R.id.control);
        button.setVisibility(View.INVISIBLE); // Hide button
        txt.setTextSize((float) 24);

        if (isMale) { // Calculate the calories burned
            calories = (int) ((-55.0969 + (0.6309 * heartRate) + (0.1988 * weight) + (0.2017 * age)) / 4.184) * 60 * seconds;
        } else {
            calories = (int) ((-20.4022 + (0.4472 * heartRate) - (0.1263 * weight) + (0.074 * age)) / 4.184) * 60 * seconds;
        }
        goalCalories -= calories;
        if (goalCalories < 0) {
            txt.setText("Congratulations! You've reached your goal!");
        } else {
            txt.setText("Nice. You have " + goalCalories + " calories remaining to burn.");
        }
    }
    public class ListenerService extends WearableListenerService {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equals("/message")) {
                final String message = new String(messageEvent.getData());

                userID = message;
            } else {
                super.onMessageReceived(messageEvent);
            }
        }
    }

    public class ToDoItem {
        private String id;
        private String text;
        private Boolean complete;
        private Date due;
        private Integer duration;

    }
}