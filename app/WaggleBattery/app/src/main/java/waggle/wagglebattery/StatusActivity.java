package waggle.wagglebattery;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {
    //TODO: isExpanded must exist as same number as cardviews
    Boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_status);
        final CardView cardView = (CardView) findViewById(R.id.card_1);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TransitionManager.beginDelayedTransition(cardView);
                cardExpandCollapse(R.id.tv_desc);
            }
        });
        final CardView cardView2=(CardView) findViewById(R.id.card_2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardExpandCollapse(R.id.tv_desc_2);
            }
        });

        //cardView.setVisibility(View.GONE);
    }

    private void cardExpandCollapse(int id) {
        final TextView tv_support=(TextView) findViewById(id);
        if (isExpanded) {
            //ibt_show_more.animate().rotation(0).start();
            Toast.makeText(getApplicationContext(),"Collapsing",Toast.LENGTH_SHORT).show();
            isExpanded = false;
            tv_support.setVisibility(View.GONE);
        }
        else {
            //ibt_show_more.animate().rotation(180).start();
            Toast.makeText(getApplicationContext(),"Expanding",Toast.LENGTH_SHORT).show();
            isExpanded = true;

            //Http Req
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    //code to do the HTTP request
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response = httpclient.execute(new HttpGet("http://192.168.2.52/test.php?id=1"));
                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            String responseString = out.toString();
                            out.close();
                            //..more logic
                            tv_support.setText(responseString);

                        } else {
                            //Closes the connection.
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            tv_support.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animation = ObjectAnimator.ofInt(tv_support, "maxLines", tv_support.getMaxLines());
        animation.setDuration(200).start();
    }
}

/*
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_status, container, false);
        final CardView cardView = (CardView) view.findViewById(R.id.card_1);
        //final TextView list = (TextView) view.findViewById(R.id.item_description);
        //list.setVisibility(View.GONE);

        //cardView.setVisibility(View.GONE);
        cardView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getContext(),"Hello",Toast.LENGTH_LONG).show();
                //TransitionManager.beginDelayedTransition(cardView);
                //list.setVisibility(View.VISIBLE);
                //ObjectAnimator animation = ObjectAnimator.ofInt(mItemDescription, "maxLines", mItemDescription.getMaxLines());
                //animation.setDuration(200).start();
            }
        });

        return view;
    }
}*/
