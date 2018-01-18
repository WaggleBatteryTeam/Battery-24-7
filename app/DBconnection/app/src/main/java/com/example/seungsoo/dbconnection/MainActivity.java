package com.example.seungsoo.dbconnection;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private TextView tv_outPut;

    private ArrayList<WaggleInfo> WaggleInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaggleInfoList = new ArrayList<WaggleInfo>();

        // URL 설정.
        String url = "http://192.168.2.52:80/wagglelist.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            try{
                Log.i("kss", "result : "+result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String waggleName, waggleTime;
                double waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in;
                while(count < jsonArray.length()){
                    Log.i("aaa", "while sentence");
                    JSONObject object = jsonArray.getJSONObject(count);
                    waggleName = object.getString("name");
                    waggleTime = object.getString("time");
                    waggleBattety = object.getDouble("battery");
                    waggleEnv_w = object.getDouble("env_w");
                    waggleEnv_s = object.getDouble("env_s");
                    waggleTemp_in = object.getDouble("temp_in");
                    waggleHum_in = object.getDouble("hum_in");
                    WaggleInfo waggleinfo = new WaggleInfo(waggleName, waggleTime, waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in);
                    WaggleInfoList.add(waggleinfo);
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            /*//doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            tv_outPut.setText(result);*/
        }
    }
}