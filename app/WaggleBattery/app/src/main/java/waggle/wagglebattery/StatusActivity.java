package waggle.wagglebattery;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import waggle.waggle.wagglebattery.adapter.WaggleStatusAdapter;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {

    String[] _req={"WaggleIdLatest",null,"remain_battery","voltage","charging","temperature","humidity","heater","fan", "updated_time", "notice"}; //colname of Monitor
    private RequestData reqData = new RequestData();
    private int waggle_id = 0 ;
    private String _target_url;
    private TextView tvWaggleId;
    private TextView tvCharging;
    private TextView tvHeater;
    private TextView tvFan;
    private TextView tvUpdateInfo;
    private ContentValues res = null;
    private RecyclerView.Adapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        _target_url = getString(R.string.target_addr);

        //Get Waggle_id Value from parent Activity.
        Intent intent = getIntent();
        waggle_id = intent.getExtras().getInt("waggleId");
        Log.i("kss", "Variable from former screen : "+Integer.toString(waggle_id));

        _req[1] = Integer.toString(waggle_id);

        res = reqData.jsonAsContentValueForLatestData(_target_url,_req);

        //WaggleInfo LinearLayout Setting

        // TextView for update information.
        tvWaggleId = (TextView) findViewById(R.id.tv_waggle_id);
        tvCharging = (TextView) findViewById(R.id.tv_charging);
        tvHeater = (TextView) findViewById(R.id.tv_heater);
        tvFan = (TextView) findViewById(R.id.tv_fan);
        tvUpdateInfo = (TextView) findViewById(R.id.tv_update_info);

        // waggle 기본 정보 띄우기
        tvWaggleId.setText(_req[1]);
        tvCharging.setText(res.getAsString("charging"));
        tvHeater.setText(res.getAsString("heater"));
        tvFan.setText(res.getAsString("fan"));
        tvUpdateInfo.setText("Updated : " + res.getAsString("updated_time"));    // 최종 update한 시간

        //RecyclerView Setting
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.rcview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);

        adapter = new WaggleStatusAdapter(getApplicationContext(),waggle_id,res);
        recyclerView.setAdapter(adapter);

        ImageView bt_refresh = findViewById(R.id.bt_refresh);
        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefreshBatteryInfoTask refreshBatteryInfoTask = new RefreshBatteryInfoTask();
                refreshBatteryInfoTask.execute();
            }
        });
    }

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
    }
}

