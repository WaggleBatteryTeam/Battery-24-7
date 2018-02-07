package waggle.data;

/**
 * Created by SeungSoo on 2018-01-19.
 */

public class WaggleInfo {
    private String mWaggleName, mWaggleTime;
    private double mWaggleBattery, mWaggleEnvW, mWaggleEnvS, waggleTemp_in, waggleHum_in;

    public WaggleInfo(String waggleName, String waggleTime, double waggleBattery,
                      double waggleEnv_w, double waggleEnv_s, double waggleTemp_in,
                      double waggleHum_in) {
        this.mWaggleName = waggleName;                      // Waggle Id
        this.mWaggleTime = waggleTime;                      // Time when the data was recorded.
        this.mWaggleBattery = waggleBattery;                // Battery Percent
        this.mWaggleEnvW = waggleEnv_w;                     // Energy from Wind
        this.mWaggleEnvS = waggleEnv_s;                     // Energy from Sum
        this.waggleTemp_in = waggleTemp_in;                 // Temperature inside the battery box.
        this.waggleHum_in = waggleHum_in;                   // Humidity inside the battery box.
    }

    public String getmWaggleName() {
        return mWaggleName;
    }

    public void setmWaggleName(String mWaggleName) {
        this.mWaggleName = mWaggleName;
    }

    public String getmWaggleTime() {
        return mWaggleTime;
    }

    public void setmWaggleTime(String mWaggleTime) {
        this.mWaggleTime = mWaggleTime;
    }

    public double getmWaggleBattery() {
        return mWaggleBattery;
    }

    public void setmWaggleBattery(double mWaggleBattery) { this.mWaggleBattery = mWaggleBattery; }

    public double getmWaggleEnvW() {
        return mWaggleEnvW;
    }

    public void setmWaggleEnvW(double mWaggleEnvW) {
        this.mWaggleEnvW = mWaggleEnvW;
    }

    public double getmWaggleEnvS() {
        return mWaggleEnvS;
    }

    public void setmWaggleEnvS(double mWaggleEnvS) {
        this.mWaggleEnvS = mWaggleEnvS;
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
