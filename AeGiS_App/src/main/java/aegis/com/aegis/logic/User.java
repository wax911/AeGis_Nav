package aegis.com.aegis.logic;

import com.google.android.gms.plus.model.people.Person;

import java.io.Serializable;

/**
 * Created by Maxwell on 10/11/2015.
 */
public class User implements Serializable {

    private String fullname;
    private String email;
    private String cover_pic;
    private String profile_pic;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCover_pic() {
        return cover_pic;
    }

    public void setCover_pic(String cover_pic) {
        this.cover_pic = cover_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
