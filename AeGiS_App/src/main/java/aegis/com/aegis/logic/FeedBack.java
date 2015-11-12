package aegis.com.aegis.logic;

import java.util.Date;

/**
 * Created by Maxwell on 11/3/2015.
 */
public class FeedBack
{
    private int id;
    private User user;
    private String comment;
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FeedBack(User user, String comment, Date date) {
        this.user = user;
        this.comment = comment;
        this.date = date;
    }

    public FeedBack(int id, User user, String comment, Date date) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.date = date;
    }
}
