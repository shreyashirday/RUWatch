package com.hackru.ruwatch.ruwatch;
import com.microsoft.windowsazure.mobileservices.*;
import android.app.*;
import android.util.*;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.os.*;
import com.hackru.ruwatch.pojos.*;
import java.net.MalformedURLException;

/**
 * Created by shreyashirday on 10/11/14.
 */
public class Register extends Activity {
    Spinner sp;
    EditText a,w,go,wa;
    Button d;
    User newUser;
    ArrayAdapter<CharSequence> adapter;
    MobileServiceClient mClient;
    MobileServiceTable<User> userTable;
    @Override
    public void onCreate(Bundle savedInstanceState){

        d = (Button)findViewById(R.id.button);
        sp = (Spinner)findViewById(R.id.spinner);
        a = (EditText)findViewById(R.id.editText);
        w = (EditText)findViewById(R.id.editText2);
        go = (EditText)findViewById(R.id.editText3);
        wa = (EditText)findViewById(R.id.editText4);
       adapter = ArrayAdapter.createFromResource(this,
                R.array.benchmarks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        try {
            mClient = new MobileServiceClient("https://doordonate.azure-mobile.net/", "iKCHzhhVbBrqXpsAJZCReWLvKYkNNY53", this);
            userTable = mClient.getTable(User.class);
        } catch(MalformedURLException e){
            mClient = null;
            Log.e("Error",e.getMessage());
        }

        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClient!=null) {
                    mClient.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
                        @Override
                        public void onCompleted(MobileServiceUser mobileServiceUser, Exception e, ServiceFilterResponse serviceFilterResponse) {
                            if (e == null) {
                                newUser = new User(mobileServiceUser.getUserId());
                                newUser.setAge(Integer.parseInt(a.getText().toString()));
                                newUser.setWeight(Integer.parseInt(w.getText().toString()));
                                newUser.setGoal(Integer.parseInt(go.getText().toString()));
                                newUser.setWager(Integer.parseInt(wa.getText().toString()));
                                newUser.setBenchmark(sp.getSelectedItemPosition() + 1);
                                userTable.insert(newUser, new TableOperationCallback<User>() {
                                    @Override
                                    public void onCompleted(User entity, Exception e, ServiceFilterResponse serviceFilterResponse) {
                                        if(e==null){

                                        }
                                        else{
                                            Log.e("Error",e.getMessage());
                                        }
                                    }
                                });

                            } else {

                            }
                        }
                    });
                }
            }
        });


    }



}
