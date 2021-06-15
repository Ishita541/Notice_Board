package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class compose_page2 extends AppCompatActivity {

    EditText editTextSubject,editTextNote;
    ProgressDialog dialog;
    ArrayList<Integer> list;
    String id;
    SharedPreferences preferences;
    int serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_page2);
        editTextNote = findViewById(R.id.editTextNote);
        editTextSubject = findViewById(R.id.editTextSubject);
        list = getIntent().getIntegerArrayListExtra("ids");
        preferences = getSharedPreferences("pref",MODE_PRIVATE);
        id = preferences.getString("staff_id","");
        serial = preferences.getInt("serial",0);
    }

    public void proceed(View v){
        if(editTextSubject.length()==0){
            editTextSubject.setError("Enter a subject!");
        }
        else if(editTextNote.length()==0){
            editTextNote.setError("Enter a note");
        }
        else {

            new AddTask().execute(id,editTextSubject.getText().toString(),editTextNote.getText().toString());
        }

    }

    public void attach(View v){

    }

    class AddTask extends AsyncTask<String,Integer,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(compose_page2.this);
            dialog.setMessage("Sending Message");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{

                String ids = list.get(0)+"";
                for(int i=1;i<list.size();i++){
                    ids = ids + "," + list.get(i);
                }
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.host)+"ishita/noticeboard/send.php");
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("rid",ids));
                list.add(new BasicNameValuePair("sid",""+serial));
                list.add(new BasicNameValuePair("sub",strings[1]));
                list.add(new BasicNameValuePair("message",strings[2]));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(compose_page2.this);
                builder.setTitle("Done");
                builder.setMessage("Message Sent");
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
                Toast.makeText(compose_page2.this, "Error adding staff", Toast.LENGTH_SHORT).show();

            }
        }
    }


}
