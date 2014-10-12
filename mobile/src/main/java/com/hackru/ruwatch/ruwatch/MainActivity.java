package com.hackru.ruwatch.ruwatch;
import android.os.*;
import android.app.*;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import  com.microsoft.windowsazure.mobileservices.*;
import com.hackru.ruwatch.pojos.*;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import java.util.Calendar;

/**
 * Created by shreyashirday on 10/12/14.
 */
public class MainActivity extends Activity {
    MobileServiceClient mClient;
    MobileServiceTable<User> userTable;
    Button startBtn;
    Date finalDate = new Date();
    boolean start;
    int benchmark;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        startBtn = (Button) findViewById(R.id.startBtn);
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
                start = true;
                Calendar initialCal = Calendar.getInstance();
                Calendar finalCal = Calendar.getInstance();
                if (benchmark == 1){
                    finalCal.add(Calendar.DAY_OF_MONTH, 1);
                }
                if (benchmark == 2){
                    finalCal.add(Calendar.WEEK_OF_YEAR, 1);
                }
                if (benchmark == 3){
                    finalCal.add(Calendar.MONTH, 1);
                }
                Date finalDate = finalCal.getTime();
            }
        });
        if(extras != null){
            String token = extras.getString("id");
            if(mClient!=null){
                userTable.where().field("id").eq(token).execute(new TableQueryCallback<User>() {
                    @Override
                    public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
                           User myUser = users.get(0);
                            int burned = myUser.getBurned();
                            int donated = myUser.getDonated();
                            //TODO: get time left!!!! and display the above information in TextViews
                    }
                });
            }
        }
        else{
            Log.e("Oops!","Bundle is null");
        }
    }


}
