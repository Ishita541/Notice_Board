package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView name,id;
    SharedPreferences preferences;
    ListView listView;
    ArrayList<String> subjects,from,date,message;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        listView = findViewById(R.id.list_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton fab =  findViewById(R.id.compose_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(home_page.this,compose_page1.class);
                startActivity(i);
           }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        name = navigationView.getHeaderView(0).findViewById(R.id.staff_name);
        id = navigationView.getHeaderView(0).findViewById(R.id.staff_id);
        preferences = getSharedPreferences("pref",MODE_PRIVATE);
        name.setText(preferences.getString("name",""));
        id.setText(preferences.getString("staff_id",""));
        new GetTask().execute(preferences.getInt("serial",0)+"");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Handle navigation view item clicks here.
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class GetTask extends AsyncTask<String,Integer,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(home_page.this);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.host)+"ishita/noticeboard/get_messages.php");
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("id",strings[0]));
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
                    from = new ArrayList<>();
                    subjects = new ArrayList<>();
                    date = new ArrayList<>();
                    message = new ArrayList<>();
                    JSONArray jsonArrayFrom = jsonObject.getJSONArray("from");
                    JSONArray jsonArrayDate = jsonObject.getJSONArray("date");
                    JSONArray jsonArraySub = jsonObject.getJSONArray("sub");
                    JSONArray jsonArrayNote = jsonObject.getJSONArray("note");

                    for(int i=0;i<jsonArrayFrom.length();i++){
                        from.add(jsonArrayFrom.getString(i));
                        subjects.add(jsonArraySub.getString(i));
                        date.add(jsonArrayDate.getString(i));
                        message.add(jsonArrayNote.getString(i));
                    }
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
                MyAdapter adapter = new MyAdapter();
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(home_page.this,ViewGeneral.class);
                        intent.putExtra("sub",subjects.get(position));
                        intent.putExtra("note",message.get(position));
                        intent.putExtra("date",date.get(position).split(" ")[0]);
                        intent.putExtra("from",from.get(position));
                        startActivity(intent);
                    }
                });
            }
            else{
                Toast.makeText(home_page.this, "No messages to display!", Toast.LENGTH_SHORT).show();
                listView.setAdapter(null);
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return from.size();
        }

        @Override
        public Object getItem(int position) {
            return from.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.custom_messages,parent,false);
            }

            TextView textViewSub = convertView.findViewById(R.id.custom_sub);
            TextView textViewfrom = convertView.findViewById(R.id.custom_from);

            TextView textViewDate = convertView.findViewById(R.id.custom_date);
            textViewSub.setText("Subject: "+subjects.get(position));
            textViewfrom.setText("From: "+from.get(position));
            textViewDate.setText(date.get(position).split(" ")[0]);


            return convertView;
        }
    }

}
