package com.example.snerella.fblogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Loginactivity extends AppCompatActivity {
    private Button bLogin;
    private EditText etLEmail;
    private EditText etLPassword;
    private TextView tSignup;
    private FirebaseAuth fAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        etLEmail=(EditText) findViewById(R.id.etLEmail);
        tSignup=(TextView) findViewById(R.id.tSignup);
        etLPassword=(EditText) findViewById(R.id.etLPassword);
        bLogin=(Button) findViewById(R.id.bLogin);
        fAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loginuser();
            }
        });

        tSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ontent=new Intent(Loginactivity.this,MainActivity.class);
                startActivity(ontent);
            }
        });
    }
    private void Loginuser(){
        progressDialog.setMessage("Logging in");
        progressDialog.show();
        String email=etLEmail.getText().toString().trim();
        String password=etLPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(Loginactivity.this, "Empty Email", Toast.LENGTH_SHORT).show();

            return;
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(Loginactivity.this, "Empty Password", Toast.LENGTH_SHORT).show();
            return;
        }


        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(Loginactivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(Loginactivity.this,ListOfPdfs.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Loginactivity.this,"Login Error",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }

                });
    }}
