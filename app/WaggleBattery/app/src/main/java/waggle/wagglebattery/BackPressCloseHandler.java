package waggle.wagglebattery;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by hyom on 2017-05-20.
 */

public class BackPressCloseHandler {

    private float backKeyPressedTime = 0;
    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){ // 2√ ∞° ¿˚¥Á«œ¥Ÿ¥œ±Ó
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, "Press twice to exit", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            activity.finish();
        }
    }
}