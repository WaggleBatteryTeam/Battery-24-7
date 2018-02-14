package waggle.utility;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by hyom on 2017-05-20.
 */

public class BackPressCloseHandler {
    private static final String     TAG = BackPressCloseHandler.class.getSimpleName();
    private float                   mBackKeyPressedTime = 0; //first back key pressed time
    private Activity                mActivity;

    public BackPressCloseHandler(Activity context){
        this.mActivity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > mBackKeyPressedTime + 2000){
        	// press back key twice but when gap of that time is over 2 seconds
            mBackKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(mActivity, "Press twice to exit", Toast.LENGTH_SHORT).show();
            return ;
        }
        else if(System.currentTimeMillis() <= mBackKeyPressedTime + 2000){

            mActivity.finish(); // application bye bye
        }
    }
}