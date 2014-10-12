package com.hackru.ruwatch.ruwatch;
import android.os.*;
import android.widget.*;
import android.app.*;
import android.view.*;
import android.util.Log;
import com.microsoft.windowsazure.mobileservices.*;
import com.hackru.ruwatch.pojos.*;

import java.net.MalformedURLException;
import java.util.List;


/**
 * Created by Ben on 10/12/14.
 */
public class Settings extends Activity {
    Button bDone;
    EditText age;
    EditText weight;
    EditText goal;
    EditText wager;

    MobileServiceClient mClient = null;
    MobileServiceTable<User> userTable;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Bundle extras = getIntent().getExtras();
        final Settings that = this;

        bDone = (Button)findViewById(R.id.startBtn);
        age = (EditText)findViewById(R.id.editText);
        weight = (EditText)findViewById(R.id.editText2);
        goal = (EditText)findViewById(R.id.editText3);
        wager = (EditText)findViewById(R.id.editText4);

        if(extras == null){
            //close this thing.
        }


        try {
            mClient = new MobileServiceClient("https://doordonate.azure-mobile.net/", "iKCHzhhVbBrqXpsAJZCReWLvKYkNNY53", that);
            userTable = mClient.getTable(User.class);
        } catch (MalformedURLException err) {
            Log.e("Error", err.getMessage());
            //close this thing.
        }

        final String token = extras.getString("id");
        bDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mClient != null) {
                    userTable.where().field("id").eq(token).execute(new TableQueryCallback<User>() {
                        @Override
                        public void onCompleted(List<User> users, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
                            User myUser = users.get(0);
                            myUser.setAge(Integer.parseInt(age.getText().toString()));
                            myUser.setWeight(Integer.parseInt(weight.getText().toString()));
                            myUser.setGoal(Integer.parseInt(goal.getText().toString()));
                            myUser.setWager(Integer.parseInt(wager.getText().toString()));
                            //myUser.setBenchmark(sp.getSelectedItemPosition() + 1);
                            userTable.update(myUser, new TableOperationCallback<User>() {
                                @Override
                                public void onCompleted(User entity, Exception e, ServiceFilterResponse serviceFilterResponse) {
                                    //????????
                                }
                            });
                        }
                    });
                }
            }
        });


    }
}