package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ViewGeneral extends AppCompatActivity {

    EditText editTextSubject,editTextNote;
    TextView textViewFrom,textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_general);
        editTextNote = findViewById(R.id.editTextNote);
        editTextSubject = findViewById(R.id.editTextSubject);
        textViewDate = findViewById(R.id.custom_date);
        textViewFrom = findViewById(R.id.custom_from);
        Intent intent = getIntent();
        editTextNote.setText(intent.getStringExtra("note"));
        editTextSubject.setText(intent.getStringExtra("sub"));
        textViewFrom.setText("From\n"+intent.getStringExtra("from"));
        textViewDate.setText("Dated\n"+intent.getStringExtra("date"));
    }

}
