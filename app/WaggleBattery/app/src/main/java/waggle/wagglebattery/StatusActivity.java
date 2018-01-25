package waggle.wagglebattery;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import waggle.waggle.wagglebattery.adapter.WaggleStatusAdapter;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {
    //TODO: isExpanded must exist as same number as cardviews
    private int numCard = 5;
    private String[] colName = {"battery","temp_in","hum_in","env_w","env_s"};
    String[] _req={"WaggleIdLatest",null,"battery","env_w","env_s","temp_in","hum_in"};
    private RequestData reqData = new RequestData();

    private int waggle_id = 0 ;

    private String _target_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_re);

        _target_url = getString(R.string.target_addr);

        // ############################ 승수 수정 ###########################################
         /*TODO : waggleName값에 해당하는 열(튜플?)을 DB에서 불러와서 화면에 뿌려주는 작업
         * WaggleListLayout.java에서 waggleList의 한 항목을 클릭했을때 해당 waggle에 해당하는 waggleName값이 매개변수로 넘어옴
         * 따라서, 이에 해당하는 열을 DB에서 찾아와야함
         */
        Intent intent = getIntent();
        waggle_id = intent.getExtras().getInt("waggleId");
        Log.i("kss", "Variable from former screen : "+Integer.toString(waggle_id));

        //TODO: Change Value
        _req[1] = Integer.toString(waggle_id);

        ContentValues res = reqData.jsonAsContentValueForLatestData(_target_url,_req);
        // ##################################################################################


        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.rcview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lim);

        RecyclerView.Adapter adapter = new WaggleStatusAdapter(res);
        recyclerView.setAdapter(adapter);



        /*
        for(int i=0;i<numCard;i++){

            try {
                final int cardViewId=R.id.class.getField("card_"+i).getInt(0);
                final int tvDescId=R.id.class.getField("tv_desc_"+i).getInt(0);
                final String colFind=colName[i];
                final CardView cardView = (CardView) findViewById(cardViewId);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardExpandCollapse(tvDescId,colFind);
                    }
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }*/
    }
}

