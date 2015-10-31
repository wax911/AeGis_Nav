package aegis.com.aegis.logic;

import java.io.Serializable;

/**
 * Created by Maxwell on 10/2/2015.
 */
public class CustomLocation implements Serializable
{
    private String name;
    //because latlng is not serializable i think
    private double lng;
    private double lat;

    public CustomLocation(String place, double lat, double lng) {
        name = place;
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }
}