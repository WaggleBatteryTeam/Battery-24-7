package waggle.wagglebattery.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import waggle.waggle.wagglebattery.adapter.WaggleStatusAdapter;
import waggle.utility.DownloadDataTask;
import waggle.wagglebattery.BuildConfig;
import waggle.wagglebattery.R;

/**
 * Created by parksanguk on 1/16/18.
 */

/**
 * This class is executed for showing Waggle status.
 * This activity is started when the user clicked Waggle in WaggleList or WaggleMap.
 */
public class StatusActivity extends AppCompatActivity {
    private static final String TAG = StatusActivity.class.getSimpleName();


    private int mWaggleId = 0;
    private TextView mTextViewWaggleId;
    private TextView mTextViewCharging, mTextViewHeater,
            mTextViewFan, mTextViewUpdateInfo;
    private ContentValues mColumns = new ContentValues(), mRequest = new ContentValues(),
            mOption = new ContentValues(), mRes = new ContentValues();
    private RecyclerView.Adapter mAdapter = null;
    private RecyclerView recyclerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // < button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);

        Integer[] imgid = {R.drawable.waggle1,
                R.drawable.waggle2, R.drawable.waggle3};

        //Get Waggle_id Value from parent Activity.
        Intent intent = getIntent();
        mWaggleId = intent.getExtras().getInt("waggleId");

        if (BuildConfig.DEBUG) Log.d(TAG,
                "Variable from former screen : " + Integer.toString(mWaggleId));

        // Find ImageView for waggle image and Set it.
        ImageView imageView = (ImageView) findViewById(R.id.iv_waggle);
        imageView.setImageResource(imgid[(mWaggleId - 1)%3]);

        // Find TextView
        mTextViewWaggleId = (TextView) findViewById(R.id.tv_waggle_id);
        mTextViewCharging = (TextView) findViewById(R.id.tv_charging);
        mTextViewHeater = (TextView) findViewById(R.id.tv_heater);
        mTextViewFan = (TextView) findViewById(R.id.tv_fan);
        mTextViewUpdateInfo = (TextView) findViewById(R.id.tv_update_info);

        //Request Data to Server.
        mOption.put("url", getString(R.string.target_addr));
        mOption.put("ReturnType", 0);


        mRequest.put("req", "WaggleIdLatest");
        mRequest.put("id", Integer.toString(mWaggleId));

        // Below is the column name of BatteryStatus.
        mColumns.put("0", "charging");
        mColumns.put("1", "heater");
        mColumns.put("2", "fan");
        mColumns.put("3", "updated_time");
        mColumns.put("4", "voltage");
        mColumns.put("5", "temperature");
        mColumns.put("6", "humidity");

        new DownloadDataTask(new DownloadDataTask.AsyncResponse() {

            @Override
            public void processFinish(Object output) {                //get One Value
                ContentValues res = (ContentValues) output;

                // waggle 기본 정보 띄우기
                mTextViewWaggleId.setText(String.valueOf(mWaggleId));
                mTextViewCharging.setText(res.getAsString("charging"));
                mTextViewHeater.setText(res.getAsString("heater"));
                mTextViewFan.setText(res.getAsString("fan"));
                // 최종 update한 시간
                mTextViewUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));

                mRes = new ContentValues();
                mRes.put("voltage", res.getAsString("voltage"));
                mRes.put("temperature", res.getAsString("temperature"));
                mRes.put("humidity", res.getAsString("humidity"));
                mRes.put("updated_time", res.getAsString("updated_time"));

                //RecyclerView Setting
                recyclerView = (RecyclerView) findViewById(R.id.rcview);
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
                lim.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(lim);

                mAdapter = new WaggleStatusAdapter(getApplicationContext(), mWaggleId, mRes);
                recyclerView.setAdapter(mAdapter);

            }
        }).execute(mOption, mRequest, mColumns);


        final SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.swipe_layout_status);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(false);

                new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        ContentValues res = (ContentValues) output;

                        // waggle 기본 정보 띄우기
                        mTextViewWaggleId.setText(String.valueOf(mWaggleId));
                        mTextViewCharging.setText(res.getAsString("charging"));
                        mTextViewHeater.setText(res.getAsString("heater"));
                        mTextViewFan.setText(res.getAsString("fan"));
                        // 최종 update한 시간
                        mTextViewUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));

                        mRes = new ContentValues();
                        mRes.put("voltage", res.getAsString("voltage"));
                        mRes.put("temperature", res.getAsString("temperature"));
                        mRes.put("humidity", res.getAsString("humidity"));
                        mRes.put("updated_time", res.getAsString("updated_time"));

                        mAdapter.notifyDataSetChanged();
                    }
                }).execute(mOption, mRequest, mColumns);

                // Alert refreshing.
                Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_LONG).show();

            }
        });

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}

