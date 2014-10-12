package com.hackru.ruwatch.ruwatch;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.microsoft.windowsazure.mobileservices.*;
import android.content.*;
import java.net.*;
import android.util.*;
import android.view.View;
import android.widget.*;
import com.hackru.ruwatch.pojos.*;

public class MyActivity extends ActionBarActivity {
    private MobileServiceClient mClient;
     private MobileServiceTable<User> userTable;
    Context ctx;
    User newUser;
    Button si,su;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ctx = this;
        try{
            mClient = new MobileServiceClient("https://doordonate.azure-mobile.net/","iKCHzhhVbBrqXpsAJZCReWLvKYkNNY53",ctx);
            userTable = mClient.getTable(User.class);
        }
        catch(MalformedURLException e){

        }
        su = (Button)findViewById(R.id.button);
        su.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ctx.startActivity(new Intent(ctx,Register.class));
            }

                              });
        si = (Button)findViewById(R.id.button2);
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void authenticate(){

        mClient.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
            @Override
            public void onCompleted(MobileServiceUser user, Exception exception, ServiceFilterResponse response){
                if(exception == null){
                    Log.d("Success","User was logged in via facebook");


                }
                else{
                    Log.e("Error",exception.getMessage());
                }
            }

        });
    }
}
