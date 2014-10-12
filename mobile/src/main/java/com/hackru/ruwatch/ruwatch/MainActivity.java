package com.hackru.ruwatch.ruwatch;
import android.os.*;
import android.app.*;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.wearable.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import  com.microsoft.windowsazure.mobileservices.*;
import com.hackru.ruwatch.pojos.*;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import android.content.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by shreyashirday on 10/12/14.
 */
public class MainActivity extends Activity {
    MobileServiceClient mClient;
    MobileServiceTable<User> userTable;
    Button startBtn;
    User myUser;
    Date finalDate = new Date();
    TextView t,t2,t3;
    boolean start,startValue;
    int benchmark;
    String nodeId,message;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        startBtn = (Button) findViewById(R.id.startBtn);
        t = (TextView)findViewById(R.id.textView);
        t2 = (TextView)findViewById(R.id.textView2);
        t3 = (TextView)findViewById(R.id.textView3);
        Bundle extras = getIntent().getExtras();
        try{
            mClient = new MobileServiceClient("https://doordonate.azure-mobile.net/","iKCHzhhVbBrqXpsAJZCReWLvKYkNNY53",this);
            userTable = mClient.getTable(User.class);
        } catch(MalformedURLException err){
            Log.e("Error",err.getMessage());
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date finalDate = null;
                if(!startValue) {
                    start = true;
                    Calendar initialCal = Calendar.getInstance();
                    Calendar finalCal = Calendar.getInstance();
                    if (benchmark == 1) {
                        finalCal.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    if (benchmark == 2) {
                        finalCal.add(Calendar.WEEK_OF_YEAR, 1);
                    }
                    if (benchmark == 3) {
                        finalCal.add(Calendar.MONTH, 1);
                    }
                    finalDate = finalCal.getTime();
                }
                else{
                    start = false;
                }

                if(myUser!=null){
                    if(finalDate!=null){
                        myUser.setFinalDate(finalDate);
                    }
                    myUser.setStart(start);
                    userTable.update(myUser,new TableOperationCallback<User>() {
                        @Override
                        public void onCompleted(User user, Exception e, ServiceFilterResponse serviceFilterResponse) {
                            if(e == null){
                                Log.d("Success!","User updated successfully");
                            }else{
                                Log.e("Error",e.getMessage());
                            }
                        }
                    });
                }

            }
        });
        if(extras != null){
            String token = extras.getString("id");
            if(mClient!=null){
                userTable.where().field("id").eq(token).execute(new TableQueryCallback<User>() {
                    @Override
                    public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
                           myUser = users.get(0);
                        message = myUser.getId();
                            int burned = myUser.getBurned();
                            t.setText("Calories Burned " + burned);
                            int donated = myUser.getDonated();
                            t2.setText("Money Donated " + donated);
                            startValue = myUser.getStart();
                        if(!startValue){
                            startBtn.setText("Stop");
                        }

                    }
                });
            }
        }
        else{
            Log.e("Oops!","Bundle is null");
        }
    }

    private GoogleApiClient getGoogleApiClient(Context context){
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
                if(nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendToast() {
        final GoogleApiClient client = getGoogleApiClient(this);
        if (nodeId !=null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(1000,TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client,nodeId,"/message",message.getBytes());
                    client.disconnect();
                }
            }).start();
        }
    }
}
