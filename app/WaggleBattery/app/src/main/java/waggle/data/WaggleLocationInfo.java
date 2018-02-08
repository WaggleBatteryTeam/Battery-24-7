package waggle.data;

/**
 * Created by SeungSoo on 2018-01-19.
 */

public class WaggleLocationInfo {
    private int waggleId;
    private double waggleLat, waggleLon;
    private String waggleDate;

    public WaggleLocationInfo(int waggleId, double waggleLat, double waggleLon,
                              String waggleDate) {
        this.waggleId = waggleId;                           // Waggle Id
        this.waggleLat = waggleLat;                         // Waggle position : Latitude
        this.waggleLon = waggleLon;                         // Waggle position : Longitude
        this.waggleDate = waggleDate;                       // GPS Update date
    }

    public int getWaggleId() {
        return waggleId;
    }

    public void setWaggleId(int waggleId) {
        this.waggleId = waggleId;
    }

    public double getWaggleLat() {
        return waggleLat;
    }

    public void setWaggleLat(double waggleLat) {
        this.waggleLat = waggleLat;
    }

    public double getWaggleLon() {
        return waggleLon;
    }

    public void setWaggleLon(double waggleLon) {
        this.waggleLon = waggleLon;
    }

    public String getWaggleDate() {
        return waggleDate;
    }

    public void setWaggleDate(String waggleDate) {
        this.waggleDate = waggleDate;
    }
}
