package com.intent.ishitasharma.noticeboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class compose_page1 extends AppCompatActivity
{

    Spinner spinnerDept,spinnerDesig;
    ProgressDialog dialog;
    ListView listView;
    ArrayList<String> names;
    ArrayList<Integer> uids;
    ArrayList<Boolean> selected;
    ArrayList<Integer> selected_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_page1);
        listView = findViewById(R.id.list_users);
        spinnerDept = findViewById(R.id.spinner_dept);
        spinnerDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerDesig.getSelectedItemPosition()>0 && position>0){
                    new GetTask().execute(spinnerDept.getSelectedItem().toString(),spinnerDesig.getSelectedItem().toString());
                }
                else{
                    listView.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDesig = findViewById(R.id.spinner_des);
        spinnerDesig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerDept.getSelectedItemPosition()>0 && position>0){
                    new GetTask().execute(spinnerDept.getSelectedItem().toString(),spinnerDesig.getSelectedItem().toString());
                }
                else{
                    listView.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void proceed(View v){

        selected_ids = new ArrayList<>();

        for(int i=0;i<selected.size();i++){
            if(selected.get(i)){
                selected_ids.add(uids.get(i));
            }
        }

        if(spinnerDept.getSelectedItemPosition()>0 && spinnerDesig.getSelectedItemPosition()>0 && selected_ids.size()>0){
            Intent intent = new Intent(this,compose_page2.class);
            intent.putExtra("ids",selected_ids);
            startActivity(intent);
        }
        else{
            Toast.makeText(compose_page1.this, "Please select staff to send message to!", Toast.LENGTH_SHORT).show();
        }
    }


    class GetTask extends AsyncTask<String,Integer,Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(compose_page1.this);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.host)+"ishita/noticeboard/get_users.php");
                ArrayList<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("dept",strings[0]));
                list.add(new BasicNameValuePair("des",strings[1]));
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
                    names = new ArrayList<>();
                    uids = new ArrayList<>();
                    selected = new ArrayList<>();
                    selected_ids = new ArrayList<>();
                    JSONArray jsonArrayNames = jsonObject.getJSONArray("name");
                    JSONArray jsonArrayIds = jsonObject.getJSONArray("id");

                    for(int i=0;i<jsonArrayIds.length();i++){
                        names.add(jsonArrayNames.getString(i));
                        uids.add(jsonArrayIds.getInt(i));
                        selected.add(false);
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
            }
            else{
                Toast.makeText(compose_page1.this, "No Staff Found for this selection!", Toast.LENGTH_SHORT).show();
                listView.setAdapter(null);
            }
        }
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return uids.size();
        }

        @Override
        public Object getItem(int position) {
            return uids.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.custom_select,parent,false);
            }

            CheckBox chk = convertView.findViewById(R.id.check_list);
            chk.setText(names.get(position));
            chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selected.set(position,isChecked);
                }
            });

            return convertView;
        }
    }

}
