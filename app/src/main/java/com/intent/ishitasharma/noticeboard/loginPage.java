package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class loginPage extends AppCompatActivity {
    Button b;
    EditText e1;
    EditText e2;
    ProgressDialog dialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String id,name;
    int serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        b=findViewById(R.id.login_user);
        e1=findViewById(R.id.editText_uid);
        e2=findViewById(R.id.editText_pass);
        preferences = getSharedPreferences("pref",MODE_PRIVATE);


        setTitle("Staff Login");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(e1.length()==0){
                    e1.setError("Enter ID!");
                }
                else if(e2.length()==0){
                    e2.setError("Enter Password");
                }
                else {

                    id = e1.getText().toString();
                    String pass = e2.getText().toString();
                    LoginTask task = new LoginTask();
                    task.execute(id, pass);
                }
            }
        });


    }


    class LoginTask extends AsyncTask<String,Integer,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(loginPage.this);
            dialog.setMessage("Logging In");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.host)+"ishita/noticeboard/staff_login.php");
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("id",strings[0]));
                list.add(new BasicNameValuePair("pass",strings[1]));
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
                    name = jsonObject.getString("name");
                    serial = jsonObject.getInt("serial");
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
                editor = preferences.edit();
                editor.putString("staff_id",id);
                editor.putString("name",name);
                editor.putInt("serial",serial);
                editor.apply();
                Intent i= new Intent(loginPage.this,home_page.class);
                startActivity(i);
            }
            else{
                Toast.makeText(loginPage.this, "Enter your correct id or password", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i=new Intent(loginPage.this,admin_login.class);
        startActivity(i);
        return true;
    }
}



