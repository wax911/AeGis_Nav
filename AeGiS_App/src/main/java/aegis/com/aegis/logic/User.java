package aegis.com.aegis.logic;

import java.io.Serializable;

/**
 * Created by Maxwell on 10/11/2015.
 */
public class User implements Serializable {
    private int id;
    private String fullname;
    private String email;
    private String profile_pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public User(int id, String fullname, String email, String profile_pic) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.profile_pic = profile_pic;
    }

    public User(String fullname, String email, String profile_pic) {
        this.fullname = fullname;
        this.email = email;
        this.profile_pic = profile_pic;
    }

    public User() {
    }
}
