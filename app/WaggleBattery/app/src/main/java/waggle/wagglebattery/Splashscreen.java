package waggle.wagglebattery;

/**
 * Created by eumellee on 2018. 2. 2..
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import waggle.wagglebattery.activity.MainActivity;
import waggle.wagglebattery.activity.StatusActivity;


public class Splashscreen extends Activity {
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    Thread splashTread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        StartAnimations();


    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                    // 푸쉬 메세지를 클릭해서 들어온 경우라면
                    if(getIntent().getExtras() != null) {
                        int value = 0;
                        for (String key : getIntent().getExtras().keySet()) {

                            if (key.equals("profile")) {
                                // getIntent()에 걸리는 일반적인 앱 실행 - 그냥 앱을 실행해도 getIntent()에 걸리는 경우가 있더라고...
                                // 일반적으로 앱을 실행한 경우
                                if (BuildConfig.DEBUG)
                                    Log.d("kss", "일반 intent 실행");
                                Intent intent = new Intent(Splashscreen.this,
                                        MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                Splashscreen.this.finish();
                                break;
                            }
                            else if(key.equals("waggleId")) {
                                // waggleId값 저장
                                value = Integer.parseInt(getIntent().getExtras().getString("waggleId"));
                                break;
                            }
                        }
                        if(value>0) {
                            // 푸쉬 알림으로 실행
                            if (BuildConfig.DEBUG)
                                Log.d("kss", "푸쉬 intent 실행");
                            Intent intent = new Intent ( Splashscreen.this,
                                    StatusActivity.class);
                            intent.setFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("waggleId", value);
                            startActivity (intent);
                            Splashscreen.this.finish ();
                        }
                    } else {
                        // 일반적으로 앱을 실행한 경우
                        if (BuildConfig.DEBUG)
                            Log.d("kss", "일반 intent 실행");
                        Intent intent = new Intent(Splashscreen.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Splashscreen.this.finish();
                    }


                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }
            }
        };
        splashTread.start();
    }
}
