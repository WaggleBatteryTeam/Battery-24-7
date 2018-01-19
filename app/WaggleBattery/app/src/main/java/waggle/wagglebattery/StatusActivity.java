package waggle.wagglebattery;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {
    //TODO: isExpanded must exist as same number as cardviews
    private int numCard = 5;
    private String[] colName = {"battery","temp_in","hum_in","env_w","env_s"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // ############################ 승수 수정 ###########################################
         /*TODO : waggleName값에 해당하는 열(튜플?)을 DB에서 불러와서 화면에 뿌려주는 작업
         * WaggleListLayout.java에서 waggleList의 한 항목을 클릭했을때 해당 waggle에 해당하는 waggleName값이 매개변수로 넘어옴
         * 따라서, 이에 해당하는 열을 DB에서 찾아와야함
         */
        Intent intent = getIntent();
        String waggleName = intent.getExtras().getString("waggleName");
        Log.i("kss", "Varialbe from former screen : "+waggleName);
        // ##################################################################################

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

        }
    }

    private void cardExpandCollapse(int id,String colname) {
        final TextView tv_desc=(TextView) findViewById(id);
        if (tv_desc.getVisibility()==View.VISIBLE) {
            //ibt_show_more.animate().rotation(0).start();
            Toast.makeText(getApplicationContext(),"Collapsing",Toast.LENGTH_SHORT).show();
            tv_desc.setVisibility(View.GONE);
        }
        else {
            //ibt_show_more.animate().rotation(180).start();
            Toast.makeText(getApplicationContext(),"Expanding",Toast.LENGTH_SHORT).show();

            //Get Data
            final String str_desc=jsonParseColname(colname);

            tv_desc.setText(str_desc);
            tv_desc.setVisibility(View.VISIBLE);
        }
        //ObjectAnimator animation = ObjectAnimator.ofInt(tv_desc, "maxLines", tv_desc.getMaxLines());
        //animation.setDuration(200).start();
    }

    private String httpReq(final int id, final String colname) throws ExecutionException, InterruptedException {
        //Http Req
        final String target_url="http://192.168.2.52/test.php?id=" + Integer.toString(id) + "&col=" + colname;


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws IOException {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(new HttpGet(target_url));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    String responseString = out.toString();
                    out.close();
                    //..more logic
                    return responseString;

                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            }
        };

        Future<String> future = executor.submit(callable);
        String res = future.get();
        return res;
    }

    private String jsonParseColname(final String colname){
        String res = "Load failed";

        try {
            res = httpReq(1,colname);

            //JSON parsing
            JSONObject jsonObject = new JSONObject(res);
            Log.d("JSON",res);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            String[] resArr = new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++){
                Log.d("JSON","HERE");
                JSONObject test = jsonArray.getJSONObject(i);
                resArr[i]=test.getString(colname);
                //int temp=test.getInt("temp_in");
                Log.d("JSON", resArr[i]);
            }

            //TODO: which data you want?
            res=resArr[0];


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }
}

