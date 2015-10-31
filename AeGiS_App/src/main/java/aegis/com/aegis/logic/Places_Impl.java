package aegis.com.aegis.logic;

/**
 * Created by Maxwell on 10/13/2015.
 */
public class Places_Impl extends Place_Abs
{
    public Places_Impl(String place_id, CustomLocation place_cord, String place_address, float place_rating, String place_website, String place_contact) {
        super(place_id, place_cord, place_address, place_rating, place_website, place_contact);
    }

    public Places_Impl() {
        super();

    }

    @Override
    public CustomLocation getPlace_cord() {
        return super.getPlace_cord();
    }

    @Override
    public void setPlace_cord(CustomLocation place_cord) {
        super.setPlace_cord(place_cord);
    }

    @Override
    public String getPlace_id() {
        return super.getPlace_id();
    }

    @Override
    public void setPlace_id(String place_id) {
        super.setPlace_id(place_id);
    }

    @Override
    public String getPlace_address() {
        return super.getPlace_address();
    }

    @Override
    public void setPlace_address(String place_address) {
        super.setPlace_address(place_address);
    }

    @Override
    public float getPlace_rating() {
        return super.getPlace_rating();
    }

    @Override
    public void setPlace_rating(float place_rating) {
        super.setPlace_rating(place_rating);
    }

    @Override
    public String getPlace_website() {
        return super.getPlace_website();
    }

    @Override
    public void setPlace_website(String place_website) {
        super.setPlace_website(place_website);
    }

    @Override
    public String getPlace_contact() {
        return super.getPlace_contact();
    }

    @Override
    public void setPlace_contact(String place_contact) {
        super.setPlace_contact(place_contact);
    }
}
