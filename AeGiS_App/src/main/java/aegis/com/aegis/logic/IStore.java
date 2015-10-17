package aegis.com.aegis.logic;

import android.graphics.Bitmap;

/**
 * Created by Maxwell on 10/11/2015.
 */
public interface IStore
{
    void setProfileImage(Bitmap image);
    Bitmap getProfileImage();
    boolean hasPicture();
}
