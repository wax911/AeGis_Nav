package aegis.com.aegis.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import aegis.com.aegis.logic.IStore;

/**
 * Created by Maxwell on 10/11/2015.
 */
public class ImageStore implements IStore,Serializable
{

    private final String path;
    private final String folder = "/AeGis/Resources/Images/";
    private final String fname = "user_img.png";
    private File store;
    private File dest;
    private Bitmap image;
    private boolean issuccess;
    private Context app;

    private SharedPreferences applicationSettings;
    private SharedPreferences.Editor _editor;

    @Override
    public void setProfileImage(Bitmap image) {

        store.mkdirs();
        if(store.exists())
        {
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream out = new FileOutputStream(dest);
                image.compress(Bitmap.CompressFormat.PNG, 95, out);
                out.flush();
                out.close();
                issuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                store.mkdir();
                dest.mkdirs();
                dest.createNewFile();
                FileOutputStream out = new FileOutputStream(dest);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                issuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (issuccess)
        {
            _editor.putBoolean("stored_info", true);
            _editor.commit();
            new Notifier(app).Notify("Picture Saved", "Profile picture has been saved to your device, under the folder in AeGis in the internal storage. Please don't delete that folder to avoid issues");
        }
        image.recycle();
    }

    @Override
    public Bitmap getProfileImage() {
        if(store.exists())
        {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                image = BitmapFactory.decodeFile(dest.getPath(),options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    public ImageStore(Context app)
    {
        this.path = Environment.getExternalStorageDirectory().getPath();
        store = new File(this.path+folder);
        applicationSettings = PreferenceManager.getDefaultSharedPreferences(app);
        this.app = app;
        //allows us to inject values into the editor
        _editor = applicationSettings.edit();
        dest = new File(store.getAbsolutePath(), fname);
    }
}
