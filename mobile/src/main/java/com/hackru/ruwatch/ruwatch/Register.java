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
    CheckBox m,f;
    int gen = 0;
    MobileServiceClient mClient;
    MobileServiceTable<User> userTable;
    @Override
    public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        d = (Button)findViewById(R.id.startBtn);
        sp = (Spinner)findViewById(R.id.spinner);
        a = (EditText)findViewById(R.id.editText);
        w = (EditText)findViewById(R.id.editText2);
        go = (EditText)findViewById(R.id.editText3);
        wa = (EditText)findViewById(R.id.editText4);
        m = (CheckBox)findViewById(R.id.checkbox);
        f = (CheckBox)findViewById(R.id.checkBox2);

        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m.isChecked()) {
                    m.setChecked(false);
                    f.setChecked(true);
                    gen = 2;
                }else{
                    f.setChecked(false);
                    m.setChecked(true);
                    gen = 1;
                }
            }
        });

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(f.isChecked()){
                    m.setChecked(true);
                    f.setChecked(false);
                    gen = 1;
                }else{
                    f.setChecked(true);
                    m.setChecked(false);
                    gen = 2;
                }
            }
        });



        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this,
                R.array.benchmarks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(adapter == null){
            Log.e("Oops!","adapter is null");
        }
        if(sp == null){
            Log.e("Oops!","The spinner is null");
        }

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

                                newUser = new User();
                                newUser.setId(mobileServiceUser.getUserId());
                                newUser.setGender(gen);
                                newUser.setAge(Integer.parseInt(a.getText().toString()));
                                newUser.setWeight(Integer.parseInt(w.getText().toString()));
                                newUser.setGoal(Integer.parseInt(go.getText().toString()));
                                newUser.setWager(Integer.parseInt(wa.getText().toString()));
                                newUser.setBenchmark(sp.getSelectedItemPosition() + 1);

                                userTable.insert(newUser, new TableOperationCallback<User>() {
                                    @Override
                                    public void onCompleted(User entity, Exception e, ServiceFilterResponse serviceFilterResponse) {
                                        if (e == null) {

                                            Log.d("Success", "User saved!");
                                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                            i.putExtra("id",entity.getId());
                                            startActivity(i);

                                        } else {
                                            Log.e("Error", e.getMessage());
                                        }
                                    }
                                });

                            } else {
                                        Log.e("Error",e.getMessage());
                            }
                        }
                    });
                }
            }
        });


    }



}
