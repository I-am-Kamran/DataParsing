package com.example.marvin.dataparsing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    String myJSON;
    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> ad;
    String roll_Number,name,contact,address;
    JSONArray people=null;

    private static final String TAG_RESULTS="result";

    private static final String TAG_ROLL_NUMBER="roll_number";
    private static final String TAG_NAME="name";
    private static final String TAG_PHONE="contact";
    private static final String TAG_ADDRESS="address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=findViewById(R.id.listView);
        al=new ArrayList<String>();
        ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,al);
        lv.setAdapter(ad);
        getData();
    }

    private void getData()
    {
        class GetDataJSON extends AsyncTask<String,String,String>
        {
            ProgressDialog pd;
            @Override
            protected void onPreExecute()
            {
                pd=new ProgressDialog(MainActivity.this);
                pd.setMessage("downloading...");
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings)
            {
                DefaultHttpClient httpClienttp=new DefaultHttpClient(new BasicHttpParams());
                HttpPost httpPost=new HttpPost("https://tasktodo.000webhostapp.com/getData.php");

                //Depend on your web service
                httpPost.setHeader("Content-Type","application/json");

                InputStream inputStream=null;
                String result=null;
                try
                {
                    HttpResponse reponse= httpClienttp.execute(httpPost);
                    HttpEntity entity=reponse.getEntity();
                    inputStream=entity.getContent();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"),8);
                    StringBuilder sb=new StringBuilder();
                    String line=null;
                    while ((line=reader.readLine())!=null)
                    {
                        sb.append(line+"\n");
                    }
                    result = sb.toString();
                }
                catch (ClientProtocolException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally
                {
                    try{
                  if (inputStream!=null)

                          inputStream.close();
                      }
                      catch (IOException e) {
                          e.printStackTrace();
                      }
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON=result;
                try {
                    showList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }
        GetDataJSON g=new GetDataJSON();
        g.execute();    //After this method AsyncTask is start executing...
    }

    private void showList() throws JSONException {
        JSONObject jsonObject=new JSONObject(myJSON);
        people=jsonObject.getJSONArray(TAG_RESULTS);

        for (int i=0;i<people.length();i++)
        {
            JSONObject c=people.getJSONObject(i);
            roll_Number=c.getString(TAG_ROLL_NUMBER);
            name=c.getString(TAG_NAME);
            contact=c.getString(TAG_PHONE);
            address=c.getString(TAG_ADDRESS);
            al.add(roll_Number+"\n"+name+"\n"+contact+"\n"+address);
            ad.notifyDataSetChanged();
        }

    }
}
