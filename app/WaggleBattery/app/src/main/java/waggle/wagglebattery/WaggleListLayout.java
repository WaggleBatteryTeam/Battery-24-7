package waggle.wagglebattery;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import waggle.waggle.wagglebattery.adapter.WaggleListViewAdapter;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleListLayout  extends Fragment{
    View v;
    private ArrayList<WaggleInfo> waggleInfoList;
    private WaggleListTask waggleListTask;
    private WaggleListViewAdapter adapter;
    private String url;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglelist_layout, container, false);

        // 서버에 데이터 요청을 보내야 하는 부분
        waggleInfoList = new ArrayList<WaggleInfo>();

        // URL 설정.
        url = "http://192.168.2.52:80/test.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        waggleListTask = new WaggleListTask(url, null);

        // 리스트뷰 참조
        ListView listview ;
        listview = (ListView) v.findViewById(R.id.listview1);

        // Adapter 생성
        adapter = new WaggleListViewAdapter(getContext().getApplicationContext(), waggleInfoList) ;

        // 리스트와 어댑터 연결
        listview.setAdapter(adapter);

        // 이벤트
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                WaggleInfo item = (WaggleInfo) parent.getItemAtPosition(position) ;
                //  클릭했을때 이벤트 정의
                // TODO : use item data.
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleName", waggleInfoList.get(position).getWaggleName());  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
            }
        });

        // 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                // 디비에서 wagglelist 다시 불러오기
                if(waggleListTask!=null){
                    waggleListTask=null;
                }
                waggleInfoList.clear();
                waggleListTask = new WaggleListTask(url, null);
                waggleListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        waggleListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return v;
    }

    class WaggleListTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public WaggleListTask(String url, ContentValues values) {
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
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String waggleName, waggleTime;
                double waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in;
                while(count < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    waggleName = object.getString("name");
                    waggleTime = object.getString("time");
                    waggleBattety = object.getDouble("battery");
                    waggleEnv_w = object.getDouble("env_w");
                    waggleEnv_s = object.getDouble("env_s");
                    waggleTemp_in = object.getDouble("temp_in");
                    waggleHum_in = object.getDouble("hum_in");
                    WaggleInfo waggleinfo = new WaggleInfo(waggleName, waggleTime, waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in);
                    waggleInfoList.add(waggleinfo);
                    count++;
                }
                adapter.notifyDataSetChanged();
                Toast.makeText (getContext(), "refresh", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
