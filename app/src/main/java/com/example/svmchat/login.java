package com.example.svmchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    Button button;
    EditText email,password;
    TextView logsignup;
    FirebaseAuth auth;
    String epat="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    android.app.ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("KHUL RHA...");
        progressDialog.setCancelable(false);
        auth=FirebaseAuth.getInstance();
        email=findViewById(R.id.editTextLogEmail);
        password=findViewById(R.id.editTextLogPassword);
        button=findViewById(R.id.logbutton);
        logsignup=findViewById(R.id.logsignup);
        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,registration.class);
                startActivity(intent);
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString();
                String pass=password.getText().toString();
                if(TextUtils.isEmpty(Email)){
                    progressDialog.dismiss();
                    Toast.makeText(login.this,"Email Toh Daalo",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(pass)){
                    progressDialog.dismiss();
                    Toast.makeText(login.this,"Password Toh Daalo",Toast.LENGTH_SHORT).show();
                }
                else if(!Email.matches(epat)){
                    progressDialog.dismiss();
                    email.setError("Email Address Shi Se Daalo");
                }
                else if (password.length()<6){
                    progressDialog.dismiss();
                    password.setError("Password length issue");
                    Toast.makeText(login.this,"ITNA CHOTA!!!!",Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.show();
                                try{
                                    Intent intent=new Intent(login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                catch (Exception e){
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}