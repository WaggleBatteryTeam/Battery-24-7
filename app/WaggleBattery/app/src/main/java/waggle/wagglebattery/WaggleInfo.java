package waggle.wagglebattery;

import android.graphics.drawable.Drawable;

/**
 * Created by SeungSoo on 2018-01-19.
 */

public class WaggleInfo {
    private String waggleName;
    private String waggleTime;
    private double waggleBattety, waggleEnv_w, waggleEnv_s, waggleTemp_in, waggleHum_in;

    public WaggleInfo(String waggleName, String waggleTime, double waggleBattety, double waggleEnv_w, double waggleEnv_s, double waggleTemp_in, double waggleHum_in) {
        this.waggleName = waggleName;
        this.waggleTime = waggleTime;
        this.waggleBattety = waggleBattety;
        this.waggleEnv_w = waggleEnv_w;
        this.waggleEnv_s = waggleEnv_s;
        this.waggleTemp_in = waggleTemp_in;
        this.waggleHum_in = waggleHum_in;
    }

    public String getWaggleName() {
        return waggleName;
    }

    public void setWaggleName(String waggleName) {
        this.waggleName = waggleName;
    }

    public String getWaggleTime() {
        return waggleTime;
    }

    public void setWaggleTime(String waggleTime) {
        this.waggleTime = waggleTime;
    }

    public double getWaggleBattety() {
        return waggleBattety;
    }

    public void setWaggleBattety(double waggleBattety) {
        this.waggleBattety = waggleBattety;
    }

    public double getWaggleEnv_w() {
        return waggleEnv_w;
    }

    public void setWaggleEnv_w(double waggleEnv_w) {
        this.waggleEnv_w = waggleEnv_w;
    }

    public double getWaggleEnv_s() {
        return waggleEnv_s;
    }

    public void setWaggleEnv_s(double waggleEnv_s) {
        this.waggleEnv_s = waggleEnv_s;
    }

    public double getWaggleTemp_in() {
        return waggleTemp_in;
    }

    public void setWaggleTemp_in(double waggleTemp_in) {
        this.waggleTemp_in = waggleTemp_in;
    }

    public double getWaggleHum_in() {
        return waggleHum_in;
    }

    public void setWaggleHum_in(double waggleHum_in) {
        this.waggleHum_in = waggleHum_in;
    }
}
