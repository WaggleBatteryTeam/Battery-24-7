package waggle.wagglebattery.layout;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import waggle.wagglebattery.R;
import waggle.wagglebattery.activity.StatusActivity;

/**
 * Created by nable on 2018-01-16.
 */

/**
 * This class is executed for showing Waggle List.
 * This activity is started when the user clicked wagglelist on Navigation Bar.
 */
public class WaggleListLayout extends Fragment {
    private static final String TAG = StatusActivity.class.getSimpleName();

    private View mView;
    private ArrayList<WaggleLocationInfo> mWaggleLocationInfo;
    private WaggleListViewAdapter mAdapter;

    private ContentValues mColumns = new ContentValues();


    private Integer[] mImgId = {R.drawable.waggle1, R.drawable.waggle2, R.drawable.waggle3};

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.wagglelist_layout, container, false);

        // 서버에 데이터 요청을 보내야 하는 부분
        mWaggleLocationInfo = new ArrayList<WaggleLocationInfo>();

        // 리스트뷰 참조
        ListView listview;
        listview = (ListView) mView.findViewById(R.id.listview1);

        // Adapter 생성
        mAdapter = new WaggleListViewAdapter(getContext(), mWaggleLocationInfo, mImgId);

        // 리스트와 어댑터 연결
        listview.setAdapter(mAdapter);

        // 이벤트
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                // 클릭했을때 이벤트 정의

                Intent intentMain = new Intent(v.getContext(), StatusActivity.class);
                intentMain.putExtra("waggleId",
                        mWaggleLocationInfo.get(position).getmWaggleId());  // 여기서 Waggle name이 primary key라고 간주했음
                startActivity(intentMain);
            }
        });

        // 새로고침
        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) mView.findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

                mWaggleLocationInfo.clear();

                addNewWaggleInfo();
                // Alert refreshing.
                Toast.makeText(getContext(), "Refresh", Toast.LENGTH_LONG).show();

            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        addNewWaggleInfo();

        return mView;
    }

    private void addNewWaggleInfo() {

        ContentValues option = new ContentValues(), req = new ContentValues();

        option.put("url", getString(R.string.target_addr));
        option.put("ReturnType", 1);

        req.put("req", "WaggleLoc");

        mColumns = new ContentValues();
        mColumns.put("0", "waggle_id");
        mColumns.put("1", "longtitude");
        mColumns.put("2", "latitude");
        mColumns.put("3", "date_created");

        new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
            @Override
            public void processFinish(Object output) {                //get One Value
                ContentValues[] res = (ContentValues[]) output;

                int waggleId;
                double waggleLat, waggleLon;
                String waggleDate;

                for (int i = 0; i < res.length; i++) {
                    waggleId = res[i].getAsInteger("waggle_id");
                    waggleLon = res[i].getAsDouble("longtitude");
                    waggleLat = res[i].getAsDouble("latitude");
                    waggleDate = res[i].getAsString("date_created");
                    WaggleLocationInfo waggleLocationInfo = new WaggleLocationInfo(waggleId, waggleLat, waggleLon, waggleDate);
                    mWaggleLocationInfo.add(waggleLocationInfo);
                }
                mAdapter.notifyDataSetChanged();
            }
        }).execute(option, req, mColumns);

    }
}