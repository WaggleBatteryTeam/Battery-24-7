package waggle.wagglebattery;

import android.animation.ObjectAnimator;
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
import org.apache.http.client.ClientProtocolException;
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
    int numCard = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);

        for(int i=0;i<numCard;i++){

            try {
                final int cardViewId=R.id.class.getField("card_"+i).getInt(0);
                final int tvDescId=R.id.class.getField("tv_desc_"+i).getInt(0);
                final CardView cardView = (CardView) findViewById(cardViewId);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardExpandCollapse(tvDescId,"temp_in");
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
        final TextView tv_support=(TextView) findViewById(id);
        if (tv_support.getVisibility()==View.VISIBLE) {
            //ibt_show_more.animate().rotation(0).start();
            Toast.makeText(getApplicationContext(),"Collapsing",Toast.LENGTH_SHORT).show();
            tv_support.setVisibility(View.GONE);
        }
        else {
            String res = "Load failed";
            //ibt_show_more.animate().rotation(180).start();
            Toast.makeText(getApplicationContext(),"Expanding",Toast.LENGTH_SHORT).show();

            try {
                res = httpReq(1,colname);

                //TODO: JSON PARSING!!! NOT WORKING BELOW NOW
                JSONObject jsonObject = new JSONObject(res);
                Log.d("JSON",res);
                JSONArray jsonArray = jsonObject.getJSONArray("");
                for(int i=0;i<jsonArray.length();i++){
                    Log.d("JSON","HERE");
                    JSONObject test = jsonArray.getJSONObject(i);
                    int temp=test.getInt("temp_in");
                    Log.d("JSON", Integer.toString(temp));
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Parsing JSON Object

            tv_support.setText(res);
            tv_support.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animation = ObjectAnimator.ofInt(tv_support, "maxLines", tv_support.getMaxLines());
        animation.setDuration(200).start();
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
}

