package aegis.com.aegis.logic;

/**
 * Created by Maxwell on 11/3/2015.
 */
public class Wap
{
    private int id;
    private Places_Impl loc;
    private String macAdd;
    private String SSID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Places_Impl getLoc() {
        return loc;
    }

    public void setLoc(Places_Impl loc) {
        this.loc = loc;
    }

    public String getMacAdd() {
        return macAdd;
    }

    public void setMacAdd(String macAdd) {
        this.macAdd = macAdd;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public Wap(int id, Places_Impl loc, String macAdd, String SSID) {
        this.id = id;
        this.loc = loc;
        this.macAdd = macAdd;
        this.SSID = SSID;
    }

    public Wap(Places_Impl loc, String macAdd, String SSID) {
        this.loc = loc;
        this.macAdd = macAdd;
        this.SSID = SSID;
    }
}
