package waggle.wagglebattery;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button_view_change);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getSupportFragmentManager().beginTransaction().replace(R.id.main,new StatusFragment()).addToBackStack(null).commit();
            }
        });

    }
}
