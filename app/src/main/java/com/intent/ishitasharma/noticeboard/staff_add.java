package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.util.Random;

public class staff_add extends AppCompatActivity {
    EditText name;
    Spinner dept;
    Spinner desig;
    EditText contact;
    RadioButton male;
    RadioButton female;
    Button b;
    ProgressDialog dialog;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_add);
        name = findViewById(R.id.add_name);
        dept = findViewById(R.id.spinner_add_deptt);
        desig = findViewById(R.id.spinner_add_desg);
        contact = findViewById(R.id.add_contact);
        male = findViewById(R.id.radio_male);
        female = findViewById(R.id.radio_female);
        b = findViewById(R.id.add_staff);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.length()==0 || name.getText().toString().matches("[0-9]+")){
                    name.setError("Enter a valid name");
                }
                else if(contact.length()!=10){
                    contact.setError("Enter valid contact");
                }
                if(contact.length()==10){
                    try{
                        Long.parseLong(contact.getText().toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        contact.setError("Enter a valid contact");
                        return;
                    }
                }
                if(dept.getSelectedItemPosition()==0){
                    Toast.makeText(staff_add.this,"Select Department",Toast.LENGTH_SHORT).show();
                }
                else if(desig.getSelectedItemPosition()==0){
                    Toast.makeText(staff_add.this,"Select Designation",Toast.LENGTH_SHORT).show();
                }
                else {
                    String sname = name.getText().toString();
                    String sdept = dept.getSelectedItem().toString();
                    String sdesig = desig.getSelectedItem().toString();
                    String con = contact.getText().toString();
                    String gen;
                    if (male.isChecked()) {
                        gen = "Male";
                    } else {
                        gen = "Female";
                    }
                    Random random = new Random();
                    int p1 = random.nextInt(9);
                    int p2 = random.nextInt(9);
                    int p3 = random.nextInt(9);
                    int p4 = random.nextInt(9);
                    pass = p1+""+p2+""+p3+""+p4;
                    new AddTask().execute(sname,sdept,sdesig,con,gen,pass);
                }


            }
        });

    }
    class AddTask extends AsyncTask<String,Integer,Boolean>
    {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(staff_add.this);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.host)+"ishita/noticeboard/add_staff.php");
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("name",strings[0]));
                list.add(new BasicNameValuePair("dept",strings[1]));
                list.add(new BasicNameValuePair("desig",strings[2]));
                list.add(new BasicNameValuePair("contact",strings[3]));
                list.add(new BasicNameValuePair("gender",strings[4]));
                list.add(new BasicNameValuePair("pass",strings[5]));
                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);
                String line,data="";
                while((line=br.readLine())!=null){
                    data = data + line;
                }
                Log.d("log_tag",data);
                JSONObject jsonObject = new JSONObject(data);
                if(jsonObject.getString("response").equalsIgnoreCase("ok")){
                    return true;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
            if(aBoolean) {
                AlertDialog.Builder builder = new AlertDialog.Builder(staff_add.this);
                builder.setTitle("Done");
                builder.setMessage("Staff Added Successfully\n\nPassword is: "+pass);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(staff_add.this, "Error adding staff", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
