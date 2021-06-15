package com.intent.ishitasharma.noticeboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class admin_management extends AppCompatActivity {
    Button add;
    Button manage;
    Button inbox;
    SharedPreferences preferences;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_management);
        add=findViewById(R.id.button_add);
        manage=findViewById(R.id.button_manage);
        inbox=findViewById(R.id.button_viewinbox);

        textView = findViewById(R.id.admin_name);
        preferences = getSharedPreferences("pref",MODE_PRIVATE);
        textView.setText(preferences.getString("name",""));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(admin_management.this,staff_add.class);
                startActivity(i);
            }
        });


        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(admin_management.this,manage_staff.class);
                startActivity(i);
            }
        });


        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(admin_management.this,home_page.class);
                startActivity(i);
            }
        });
    }
}
