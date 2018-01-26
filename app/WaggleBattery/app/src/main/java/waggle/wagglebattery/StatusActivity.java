package waggle.wagglebattery;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import waggle.waggle.wagglebattery.adapter.WaggleStatusAdapter;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {

    String[] _req={"WaggleIdLatest",null,"battery","charging","heater","fan","update_time"};
    private RequestData reqData = new RequestData();

    private int waggle_id = 0 ;

    private String _target_url;

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

        ContentValues res = reqData.jsonAsContentValueForLatestData(_target_url,_req);

        //WaggleInfo LinearLayout Setting


        //RecyclerView Setting
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.rcview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);

        RecyclerView.Adapter adapter = new WaggleStatusAdapter(getApplicationContext(),waggle_id,res);
        recyclerView.setAdapter(adapter);

    }
}

