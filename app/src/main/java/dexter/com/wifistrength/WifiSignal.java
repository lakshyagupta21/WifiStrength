package dexter.com.wifistrength;

/**
 * Created by dexter on 7/4/16.
 */
public class WifiSignal {
    String SSID ;
    int level ;
    int color ;

    WifiSignal(String SSID , int color , int frequency){

        this.SSID = SSID ;
        this.color = color ;
        this.level = frequency;
    }

    public String getSSID() {
        return SSID;
    }

    public int getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
