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

/**
 * Created by parksanguk on 1/16/18.
 */

public class StatusActivity extends AppCompatActivity {
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
                cardExpandCollapse();
            }
        });

        //cardView.setVisibility(View.GONE);
    }

    private void cardExpandCollapse() {
        TextView tv_support=(TextView) findViewById(R.id.tv_desc);
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
