package com.gerpeace.imtestpanuwat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView tvRandom,tvResult;
    EditText etNewNum;
    ArrayList<String> numRandomList;
    int numRandom=0, newNum=0;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BindView();
        for (int i=1000;i<=9999;i++){
            numRandomList.add(i+"");
        }
        //tvRandom.setText(RandomNum()+"");
    }
    // ปุ่มเปรียบเทียบ
    public void OnCompare(View view){
        newNum = new Integer(etNewNum.getText().toString());
        if (newNum > numRandom){
            tvResult.setText(newNum+" มากกว่า เลขสุ่ม เป็นจำนวน " +(newNum-numRandom));
        }else if (newNum < numRandom){
            tvResult.setText(newNum+" น้อยกว่า เลขสุ่ม เป็นจำนวน " +(numRandom-newNum));
        }else if (newNum == numRandom){
            tvResult.setText(numRandom+" เป็นตัวเลขที่ถูกต้อง\n*ทำการสุ่มตัวเลขใหม่เรียบร้อย");
            tvRandom.setText(RandomNum()+"");
            //GetApiData();
        }
    }
    //ผูกข้อมูล
    public void BindView(){
        tvRandom = (TextView) findViewById(R.id.tv_random);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etNewNum = (EditText) findViewById(R.id.et_new_num);
        numRandomList = new ArrayList<String>();
    }
    // นำข้อมูลจาก API มาใส่ใน ArrayList
    public void GetApiData(){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("https://api.random.org/json-rpc/1/");
                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
                    httpURLConnection.setAllowUserInteraction(false);
                    httpURLConnection.setInstanceFollowRedirects(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    InputStream inputStream = null;

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        inputStream = httpURLConnection.getInputStream();
                    }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"), 8);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;

                    while ((line=bufferedReader.readLine()) != null){
                        stringBuilder.append(line+"\n");
                    }
                    inputStream.close();
                    Log.d("JSON result",stringBuilder.toString());

                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("employee_"+"data");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        numRandomList.add(jsonObj.getString("name"));
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                tvRandom.setText(RandomNum()+"");
            }
        }.execute();

    }
    //สุ่มเลข 0-8998 มาเป็น index
    public int RandomIndexNum(){
        Random rd = new Random();
        return rd.nextInt((9999-1000)-1);
    }
    //สุ่มตัวเลข
    public int RandomNum(){
        return numRandom = new Integer(numRandomList.get(RandomIndexNum()));
    }
}
