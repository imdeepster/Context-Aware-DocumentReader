package com.example.snerella.fblogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button stop;
    private Button start;

    private Button bRegister;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tSignin;
    private FirebaseAuth fAuth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.main_activity);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();

        etEmail=(EditText) findViewById(R.id.etEmail);
        tSignin=(TextView) findViewById(R.id.tSignin);
        etPassword=(EditText) findViewById(R.id.etPassword);
        bRegister=(Button) findViewById(R.id.bRegister);
        fAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        bRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser();
            }
        });
        tSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inte=new Intent(MainActivity.this,Loginactivity.class);
                startActivity(inte);

            }
        });


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

//                info.setText(
//                        "User ID: "
//                                + loginResult.getAccessToken().getUserId());
////                                + "\n" +
////                                "Auth Token: "
////                                + loginResult.getAccessToken().getToken()
////                );




            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //Call to ListPdf class to list all pdfs
        Intent intent=new Intent(this,com.example.snerella.fblogin.ListOfPdfs.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void registerUser(){
        progressDialog.setMessage("Registering");
        progressDialog.show();
        String email=etEmail.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this, "Empty Email", Toast.LENGTH_SHORT).show();

            return;
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this, "Empty Password", Toast.LENGTH_SHORT).show();
            return;
        }


        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(MainActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            Intent inten=new Intent(MainActivity.this,Loginactivity.class);
                            startActivity(inten);
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }

                });}
}
