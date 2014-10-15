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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import  com.microsoft.windowsazure.mobileservices.*;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyActivity extends Activity implements SensorEventListener {
    private TextView mTextView;
    private SensorManager sensorManager; // Sensor
    Sensor heartRateMonitor;
    private double heartRate;
    String nodeId;

    private Handler handler = new Handler(); // Timer
    private int seconds;
    private boolean started = false;

    private int calories;

    //private User user;
    private String userID; // Data, read from Facebook...
    private int gender;
    private int age;
    private int weight;
    private int goalCalories;


    //MobileServiceClient mClient = new MobileServiceClient("MobileServiceURL","AppKey",this);
    //MobileServiceTable<ToDoItem> mToDoTable = mClient.getTable(ToDoItem.class);

    // What is called each time the Handler "ticks"
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seconds += 1;

            TextView txt = (TextView) findViewById(R.id.text);
            int minutes = (int) (seconds / 60) % 60;
            int hours = (int) (minutes / 60) % 60;
            int sec = (int)(seconds % 60);

            DecimalFormat fmt = new DecimalFormat("00");
            txt.setText(fmt.format(hours) + ":" + fmt.format(minutes) + ":" + fmt.format(sec));

            handler.postDelayed(this, 1000); //repeats after another 1000 ms
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) { // What is called in the beginning
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        // mTextView.setText("My homie");
        //set up heart rate sensor
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        heartRateMonitor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //sensorManager.registerListener(this, heartRateMonitor, SensorManager.SENSOR_DELAY_NORMAL);

        // set up message receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        ListenerService listenerService = new ListenerService();


        // getting the data from online using userID
       /* mToDoTable.where().field("userID").eq(false)
                .execute(new TableQueryCallback<ToDoItem>() {
                    public void onCompleted(List<ToDoItem> result,
                                            int count,
                                            Exception exception,
                                            ServiceFilterResponse response) {
                        if (exception == null) {
                        }
                    }
                });
                */

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
        //Log.d("Sensor Event returns " , event.values.length + " values");
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
        // Hide button
        //button.setVisibility(View.INVISIBLE);
        started = false;
        button.setImageResource(R.drawable.start);
        Toast.makeText(getApplicationContext(),"Good Job!",Toast.LENGTH_SHORT).show();

        txt.setTextSize((float) 11);

        if (gender == 1) { // Calculate the calories burned
            calories = (int) ((-55.0969 + (0.6309 * heartRate) + (0.1988 * weight) + (0.2017 * age)) / 4.184) * 60 * seconds;
        } else {
            calories = (int) ((-20.4022 + (0.4472 * heartRate) - (0.1263 * weight) + (0.074 * age)) / 4.184) * 60 * seconds;
        }
        if(calories < 0) {
            goalCalories += calories;
        }
        else{
            goalCalories -= calories;
        }
        if (goalCalories < 0) {
            txt.setText("Congratulations! You've reached your goal!");
        } else {
            txt.setText("Nice. You have " + goalCalories + " calories remaining to burn.");
        }

        seconds = 0;

        sendToast();
    }


    public class ListenerService extends WearableListenerService {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            if (messageEvent.getPath().equals("/message")) {
                IntBuffer intBuffer = ByteBuffer.wrap(messageEvent.getData()).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                int[] values = new int[intBuffer.remaining()];
                intBuffer.get(values);
                age = values[0];
                weight = values[1];
                goalCalories = values[2];
                gender = values[3];
                Log.d("age is ", "" + age);

            } else {
                super.onMessageReceived(messageEvent);
            }
        }
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
    }

    private void retrieveDeviceNode() {
        final GoogleApiClient client = getGoogleApiClient(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(1000, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result = Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendToast() {
        final GoogleApiClient client = getGoogleApiClient(this);
        final byte[] bytes = ByteBuffer.allocate(4).putInt(calories).array();

        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, "/response", bytes);
                    client.disconnect();
                }
            }).start();
        }
    }
}