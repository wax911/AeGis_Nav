package aegis.com.aegis.Data;

/**
 * Created by Maxwell on 11/1/2015.
 */
public interface ICommon
{
    enum TableNames {
        Favourites ,
        Location,
        Place,
        User,
        UserFeedback,
        WAP
    }

    String[] Tables = {"Favourites","Location","Place","User","UserFeedback","WAP"};
}
