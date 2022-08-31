package com.example.tools.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tools.R;

public class InterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inter);
        findViewById(R.id.inter_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterActivity.this,LoginActivity.class));

            }
        });
        findViewById(R.id.inter_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InterActivity.this,RegisterActivity.class);
                intent.putExtra("type",1);
                InterActivity.this.startActivity(intent);
            }
        });
    }
}