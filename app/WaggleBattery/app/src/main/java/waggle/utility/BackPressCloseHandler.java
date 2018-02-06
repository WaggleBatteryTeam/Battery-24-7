package waggle.utility;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by hyom on 2017-05-20.
 */

public class BackPressCloseHandler {

    private float backKeyPressedTime = 0; //first back key pressed time
    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){ 
        	// press back key twice but when gap of that time is over 2 seconds
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, "Press twice to exit", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            activity.finish(); // application bye bye 
        }
    }
}