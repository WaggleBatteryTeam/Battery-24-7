package waggle.wagglebattery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        final Button button = (Button) findViewById(R.id.button_view_change);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intentMain = new Intent(getApplicationContext(), StatusActivity.class);
                startActivity(intentMain);
                //Log.i("Content "," Main layout ");
                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.view_status);
                //getSupportFragmentManager().beginTransaction().replace(R.id.main,new StatusActivity()).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }
}
