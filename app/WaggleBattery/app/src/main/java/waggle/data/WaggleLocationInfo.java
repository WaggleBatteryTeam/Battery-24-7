package waggle.data;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by SeungSoo on 2018-01-19.
 */

public class WaggleLocationInfo{
    private int mWaggleId;
    private double mWaggleLat, mWaggleLon, mBatteryRemain;
    private String mWaggleDate;
    private boolean mBatteryCharging;

    public WaggleLocationInfo(int waggleId, double waggleLat, double waggleLon,
                              String waggleDate) {
        this.mWaggleId = waggleId;                           // Waggle Id
        this.mWaggleLat = waggleLat;                         // Waggle position : Latitude
        this.mWaggleLon = waggleLon;                         // Waggle position : Longitude
        this.mWaggleDate = waggleDate;                       // GPS Update date
    }

    public WaggleLocationInfo(int mWaggleId, double mBatteryRemain, boolean mBatteryCharging, double mWaggleLat, double mWaggleLon) {
        this.mWaggleId = mWaggleId;
        this.mWaggleLat = mWaggleLat;
        this.mWaggleLon = mWaggleLon;
        this.mBatteryRemain = mBatteryRemain;
        this.mBatteryCharging = mBatteryCharging;
    }

    public int getmWaggleId() {
        return mWaggleId;
    }

    public void setmWaggleId(int mWaggleId) {
        this.mWaggleId = mWaggleId;
    }

    public double getmWaggleLat() {
        return mWaggleLat;
    }

    public void setmWaggleLat(double mWaggleLat) {
        this.mWaggleLat = mWaggleLat;
    }

    public double getmWaggleLon() {
        return mWaggleLon;
    }

    public void setmWaggleLon(double mWaggleLon) {
        this.mWaggleLon = mWaggleLon;
    }

    public double getmBatteryRemain() {
        return mBatteryRemain;
    }

    public void setmBatteryRemain(double mBatteryRemain) {
        this.mBatteryRemain = mBatteryRemain;
    }

    public String getmWaggleDate() {
        return mWaggleDate;
    }

    public void setmWaggleDate(String mWaggleDate) {
        this.mWaggleDate = mWaggleDate;
    }

    public boolean ismBatteryCharging() {
        return mBatteryCharging;
    }

    public void setmBatteryCharging(boolean mBatteryCharging) {
        this.mBatteryCharging = mBatteryCharging;
    }
}



