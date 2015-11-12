package aegis.com.aegis.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Maxwell on 11/1/2015.
 */
public class DBHelper extends SQLiteOpenHelper
{

    private static final String dbName = "AeGisDB.db";
    private static final String dbPath = "/data/data/aegis.com.aegisnav/databases/";
    private SQLiteDatabase appDB;
    private final Context appContext;
    private Cursor columnGetter;

    public synchronized boolean InsertItems(String Table, ContentValues values)
    {
        boolean result;
        result = appDB.insert(Table,"default",values) > 0;
        return result;
    }

    public synchronized boolean UpdateItems(String Table, ContentValues values)
    {
        boolean result;
        openDataBase(true);
        //return something besides 0 if the query worked
        result = appDB.update(Table,values,filterColumn(Table)+" = ?",new String[]{"1"}) > 0;
        return result;
    }

    public synchronized Cursor SelectItems(String Table)
    {
        Cursor cursor = appDB.query(Table,new String[]{"*"},null,null,null,null,null);
        return cursor;
    }

    public synchronized boolean DeleteItems(String Table, String id)
    {
        boolean result;
        openDataBase(true);
        result = appDB.delete(Table,filterColumn(Table)+" = ?",new String[]{id}) > 0;
        close();
        return result;
    }

    /*
    * Gets the column name at position 0 which is the id of any table
    * That will be used when updating or deleting
    * */
    private String filterColumn(String table)
    {
        openDataBase(false);
        columnGetter = appDB.rawQuery("SELECT * FROM "+table+" LIMIT 1",null);
        String result = columnGetter.getColumnName(0);
        close();
        return result;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public synchronized void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(!dbExist)
        {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }
        else
            openDataBase(false);
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private synchronized boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = dbPath + dbName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private synchronized void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream is = appContext.getAssets().open(dbName);

        // Path to the just created empty db
        String outFileName = dbPath + dbName;

        //Open the empty db as the output stream
        OutputStream os = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer))>0){
            os.write(buffer, 0, length);
        }

        //Close the streams
        os.flush();
        os.close();
        is.close();

    }

    public synchronized void openDataBase(boolean mode) throws SQLException {
        //Open the database
        String myPath = dbPath + dbName;
        if(mode)
            appDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        else
            appDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {

        if(appDB != null)
            appDB.close();

        super.close();

    }


    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //super.onUpgrade(db);
    }

    /*
    * Default constructor which will take current application context
    * @param context
    * */
    public DBHelper(Context context)
    {
        super(context,dbName,null,1);
        appContext = context;
    }
}
