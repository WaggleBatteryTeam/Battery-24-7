package waggle.wagglebattery;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
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

import java.util.ArrayList;

import waggle.waggle.wagglebattery.adapter.WaggleListViewAdapter;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleListLayout  extends Fragment {
    View v;
    private ArrayList<WaggleInfo> waggleInfoList;
    private WaggleListViewAdapter adapter;
    private String _target_url;
    private RequestData reqData = new RequestData();

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglelist_layout, container, false);

        // 서버에 데이터 요청을 보내야 하는 부분
        waggleInfoList = new ArrayList<WaggleInfo>();

        // URL 설정.
        _target_url=getString(R.string.target_addr);

        // 리스트뷰 참조
        ListView listview;
        listview = (ListView) v.findViewById(R.id.listview1);

        // Adapter 생성
        adapter = new WaggleListViewAdapter(getContext().getApplicationContext(), waggleInfoList);

        // 리스트와 어댑터 연결
        listview.setAdapter(adapter);

        // 이벤트
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                WaggleInfo item = (WaggleInfo) parent.getItemAtPosition(position);
                //  클릭했을때 이벤트 정의
                // TODO : use item data.
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleName",
                        waggleInfoList.get(position).getWaggleName());  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
            }
        });

        // 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                waggleInfoList.clear();

                addNewWaggleInfo();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        addNewWaggleInfo();
        return v;
    }

    private void addNewWaggleInfo() {
        ContentValues[] resHttpReq;
        String[] _req={"WaggleInfo","name","time","battery","env_w","env_s","temp_in","hum_in"};

        resHttpReq = reqData.jsonAsContentValues(_target_url, _req);

        String waggleName, waggleTime;
        double waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in;
        for (int i = 0; i < resHttpReq.length; i++) {

            waggleName = resHttpReq[i].getAsString("name");
            waggleTime = resHttpReq[i].getAsString("time");
            waggleBattety = resHttpReq[i].getAsDouble("battery");
            waggleEnv_w = resHttpReq[i].getAsDouble("env_w");
            waggleEnv_s = resHttpReq[i].getAsDouble("env_s");
            waggleTemp_in = resHttpReq[i].getAsDouble("temp_in");
            waggleHum_in = resHttpReq[i].getAsDouble("hum_in");
            WaggleInfo waggleinfo = new WaggleInfo(waggleName, waggleTime, waggleBattety,
                    waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in);
            waggleInfoList.add(waggleinfo);
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "refresh", Toast.LENGTH_LONG).show();
    }
}