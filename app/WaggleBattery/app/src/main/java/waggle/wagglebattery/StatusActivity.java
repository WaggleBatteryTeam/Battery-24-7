package waggle.wagglebattery;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import waggle.waggle.wagglebattery.adapter.WaggleStatusAdapter;
import waggle.utility.DownloadDataTask;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {

    private int mWaggleId = 0 ;
    private String _target_url;
    private TextView tvWaggleId;
    private TextView tvCharging;
    private TextView tvHeater;
    private TextView tvFan;
    private TextView tvUpdateInfo;
    private ContentValues mColumns = null, mRequest = null, mOption = null, mRes = new ContentValues();
    private RecyclerView.Adapter adapter = null;
    static int temp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Integer[] imgid={R.drawable.waggle1,R.drawable.waggle2,R.drawable.waggle3};


        _target_url = getString(R.string.target_addr);

        //Get Waggle_id Value from parent Activity.
        Intent intent = getIntent();

        mWaggleId = intent.getExtras().getInt("waggleId");

        ImageView imageView = (ImageView)findViewById(R.id.iv_waggle);
        imageView.setImageResource(imgid[mWaggleId-1]);

        if (BuildConfig.DEBUG) Log.d("kss", "Variable from former screen : "+Integer.toString(mWaggleId));

        // TextView for update information.
        tvWaggleId = (TextView) findViewById(R.id.tv_waggle_id);



        tvCharging = (TextView) findViewById(R.id.tv_charging);
        tvHeater = (TextView) findViewById(R.id.tv_heater);
        tvFan = (TextView) findViewById(R.id.tv_fan);
        tvUpdateInfo = (TextView) findViewById(R.id.tv_update_info);

        mOption = new ContentValues();
        mOption.put("url", _target_url);
        mOption.put("ReturnType",0);
        //Request Data to Server.

        mRequest = new ContentValues();
        mRequest.put("req","WaggleIdLatest");
        mRequest.put("id",Integer.toString(mWaggleId));

        mColumns = new ContentValues();
        mColumns.put("0","charging");
        mColumns.put("1","heater");
        mColumns.put("2","fan");
        mColumns.put("3","updated_time");
        mColumns.put("4","remain_battery");
        mColumns.put("5","temperature");
        mColumns.put("6","humidity");

        new DownloadDataTask(new DownloadDataTask.AsyncResponse() {

            @Override
            public void processFinish(Object output) {                //get One Value
                ContentValues res = (ContentValues) output;

                // waggle 기본 정보 띄우기
                tvWaggleId.setText(String.valueOf(mWaggleId));
                tvCharging.setText(res.getAsString("charging"));
                tvHeater.setText(res.getAsString("heater"));
                tvFan.setText(res.getAsString("fan"));
                // 최종 update한 시간
                tvUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));

                mRes = new ContentValues();
                mRes.put("remain_battery",res.getAsString("remain_battery"));
                mRes.put("temperature",res.getAsString("temperature"));
                mRes.put("humidity",res.getAsString("humidity"));
                mRes.put("updated_time",res.getAsString("updated_time"));


                //RecyclerView Setting
                RecyclerView recyclerView= (RecyclerView) findViewById(R.id.rcview);
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager lim = new LinearLayoutManager(getApplicationContext());
                lim.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(lim);

                adapter = new WaggleStatusAdapter(getApplicationContext(),mWaggleId,mRes);
                recyclerView.setAdapter(adapter);

            }
        }).execute(mOption,mRequest,mColumns);
        //WaggleInfo LinearLayout Setting


        ImageView bt_refresh = findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadDataTask(new DownloadDataTask.AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        ContentValues res = (ContentValues) output;

                        // waggle 기본 정보 띄우기
                        tvWaggleId.setText(String.valueOf(mWaggleId));
                        tvCharging.setText(res.getAsString("charging"));
                        tvHeater.setText(res.getAsString("heater"));
                        tvFan.setText(res.getAsString("fan"));
                        // 최종 update한 시간
                        tvUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));

                        mRes = new ContentValues();
                        mRes.put("remain_battery",res.getAsString("remain_battery"));
                        mRes.put("temperature",res.getAsString("temperature"));
                        mRes.put("humidity",res.getAsString("humidity"));
                        mRes.put("updated_time",res.getAsString("updated_time"));

                        adapter.notifyDataSetChanged();
                    }
                }).execute(mOption,mRequest,mColumns);
            }
        });
    }

    /*
    // Refresh Battery Infomation
    private class RefreshBatteryInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            res = reqData.jsonAsContentValueForLatestData(_target_url,_req);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // waggle 기본 정보 띄우기
            tvWaggleId.setText(_req[1]);
            tvCharging.setText(res.getAsString("charging"));
            tvHeater.setText(res.getAsString("heater"));
            tvFan.setText(res.getAsString("fan"));
            tvUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));    // 최종 update한 시간

            // Inform the changed values of res to adapter
            adapter.notifyDataSetChanged();
        }
    }*/
}

