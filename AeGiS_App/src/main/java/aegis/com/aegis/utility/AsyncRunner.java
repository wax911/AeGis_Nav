package aegis.com.aegis.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Maxwell on 10/11/2015.
 * Background Async task to load user profile picture from url
 * use .recycle to release bitmap objects from memory
 * */
public class AsyncRunner extends AsyncTask<String, Void, Bitmap>
{
    private ImageView bmImage;
    private Context context;

    public AsyncRunner(ImageView bmImage, Context context)
    {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        Bitmap tempImg = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            tempImg = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return tempImg;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        new ImageStore(context).setProfileImage(result);
        result.recycle();
    }
}
