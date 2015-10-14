package aegis.com.aegis.logic;

import java.io.Serializable;

/**
 * Created by Maxwell on 10/13/2015.
 */
public abstract class Place_Abs implements Serializable
{
    private String place_id;
    private Location place_cord;
    private String place_address;
    private float place_rating;
    private String place_website;
    private String place_contact;

    public Location getPlace_cord() {
        return place_cord;
    }

    public void setPlace_cord(Location place_cord) {
        this.place_cord = place_cord;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public float getPlace_rating() {
        return place_rating;
    }

    public void setPlace_rating(float place_rating) {
        this.place_rating = place_rating;
    }

    public String getPlace_website() {
        return place_website;
    }

    public void setPlace_website(String place_website) {
        this.place_website = place_website;
    }

    public String getPlace_contact() {
        return place_contact;
    }

    public void setPlace_contact(String place_contact) {
        this.place_contact = place_contact;
    }

    public Place_Abs(String place_id, Location place_cord, String place_address, float place_rating, String place_website, String place_contact) {
        this.place_id = place_id;
        this.place_cord = place_cord;
        this.place_address = place_address;
        this.place_rating = place_rating;
        this.place_website = place_website;
        this.place_contact = place_contact;
    }

    public Place_Abs() {
    }
}
