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

import waggle.data.WaggleLocationInfo;
import waggle.waggle.wagglebattery.adapter.WaggleListViewAdapter;
import waggle.utility.DownloadDataTask;

/**
 * Created by nable on 2018-01-16.
 */

public class WaggleListLayout  extends Fragment {
    View v;
    private ArrayList<WaggleLocationInfo> waggleLocationInfoList;
    private WaggleListViewAdapter adapter;

    private ContentValues[] mRes = null;
    private ContentValues mColumns = null;

    private DownloadDataTask mDownloadDataTask = new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
        @Override
        public void processFinish(Object output) {                //get One Value
            mRes = (ContentValues[]) output;

            int waggleId;
            double waggleLat, waggleLon;
            String waggleDate;
            for (int i = 0; i < mRes.length; i++) {
                waggleId = mRes[i].getAsInteger("waggle_id");
                waggleLon = mRes[i].getAsDouble("longtitude");
                waggleLat = mRes[i].getAsDouble("latitude");
                waggleDate = mRes[i].getAsString("date_created");
                WaggleLocationInfo waggleLocationInfo = new WaggleLocationInfo(waggleId, waggleLat, waggleLon, waggleDate);
                waggleLocationInfoList.add(waggleLocationInfo);
            }
            adapter.notifyDataSetChanged();
        }
    });

    Integer[] imgid={R.drawable.waggle1,R.drawable.waggle2,R.drawable.waggle3};

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.wagglelist_layout, container, false);

        // 서버에 데이터 요청을 보내야 하는 부분
        waggleLocationInfoList = new ArrayList<WaggleLocationInfo>();

        // 리스트뷰 참조
        ListView listview;
        listview = (ListView) v.findViewById(R.id.listview1);

        // Adapter 생성
        adapter = new WaggleListViewAdapter(getContext(), waggleLocationInfoList,imgid);

        // 리스트와 어댑터 연결
        listview.setAdapter(adapter);

        // 이벤트
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                // get item
//                WaggleInfo item = (WaggleInfo) parent.getItemAtPosition(position);
                //  클릭했을때 이벤트 정의
                // TODO : use item data.
                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleId",
                        waggleLocationInfoList.get(position).getWaggleId());  // 여기서 Waggle name이 primary key라고 간주했음

                startActivity(intentMain);
            }
        });

        // 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                waggleLocationInfoList.clear();

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

        //_req[0] for POST Query and the others are column names.
        //Refer to WaggleEnv

        ContentValues option = new ContentValues();
        ContentValues req = new ContentValues();

        Log.e("TER","ERE");

        option.put("url",getString(R.string.target_addr));
        option.put("ReturnType",1);

        req.put("req","WaggleLoc");

        mColumns = new ContentValues();
        mColumns.put("0","waggle_id");
        mColumns.put("1","longtitude");
        mColumns.put("2","latitude");
        mColumns.put("3","date_created");
        //Log.e("PLZ",Integer.toString(mColumns.size()));

        mDownloadDataTask.execute(option,req,mColumns);

        Toast.makeText(getContext(), "refresh", Toast.LENGTH_LONG).show();
    }
}