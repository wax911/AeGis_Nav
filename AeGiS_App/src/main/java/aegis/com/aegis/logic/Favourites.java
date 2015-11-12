package aegis.com.aegis.logic;

/**
 * Created by Maxwell on 11/3/2015.
 */
public class Favourites {
    private int id;
    private Places_Impl favouritePlace;

    public Places_Impl getFavouritePlace() {
        return favouritePlace;
    }

    public void setFavouritePlace(Places_Impl favouritePlace) {
        this.favouritePlace = favouritePlace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Favourites(int id, Places_Impl favouritePlace) {
        this.id = id;
        this.favouritePlace = favouritePlace;
    }

    public Favourites(Places_Impl plc)
    {
        this.favouritePlace = plc;
    }
}
