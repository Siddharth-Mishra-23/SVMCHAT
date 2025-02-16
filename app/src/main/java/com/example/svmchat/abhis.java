package com.example.svmchat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class abhis extends AppCompatActivity {

    Button btnmmm;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abhis);
        btnmmm=findViewById(R.id.btnmmm);
        btnmmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(abhis.this, "SUBMITTED", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(abhis.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}