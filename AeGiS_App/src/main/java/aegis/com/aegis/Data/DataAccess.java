package aegis.com.aegis.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import aegis.com.aegis.logic.Places_Impl;

/**
 * Created by Maxwell on 11/1/2015.
 */
public class DataAccess implements ICommon
{
    private DBHelper dbh;

    public ArrayList<Places_Impl> GetAll(TableNames name)
    {
        dbh.openDataBase(true);
        Cursor curF = dbh.SelectItems(Tables[name.ordinal()]);
        ArrayList<Places_Impl> result = new ArrayList<>();
        if (curF.moveToFirst())
        {
            do
            {
                result.add(new Places_Impl(curF.getString(0), curF.getString(1), null, curF.getString(2), curF.getFloat(4), curF.getString(5), curF.getString(6)));
            }while(curF.moveToNext());
        }
        dbh.close();
        if(result.size() > 0)
            return result;
        return null;
    }

    public void AddHistory(TableNames name, Places_Impl place)
    {
        dbh.openDataBase(true);
        ContentValues cv = new ContentValues();
        cv.put("PlaceID",place.getPlace_id());
        cv.put("Name",place.getPalce_name());
        cv.put("Address",place.getPlace_address());
        cv.put("Map","null");
        cv.put("Rating",place.getPlace_rating());
        cv.put("Phone", place.getPlace_contact());
        cv.put("Website", place.getPlace_website());
        if (dbh.InsertItems(Tables[name.ordinal()], cv))
            Log.i("DBWritting","Success");
        else
            Log.e("DBWritting", "Error");

        dbh.close();
    }

    public void Update(TableNames name, Places_Impl place)
    {

    }

    public void Remove(TableNames name, Places_Impl places)
    {

    }

    /*
    * Default constructor for helper class*/
    public DataAccess(Context c)
    {
        this.dbh = new DBHelper(c);
    }
}
