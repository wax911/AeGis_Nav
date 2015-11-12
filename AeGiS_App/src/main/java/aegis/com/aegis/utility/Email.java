package aegis.com.aegis.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lolo on 11/4/2015.
 */
public class Email
{
    private Activity app;

    public Email(Activity context)
    {
        app = context;
    }
    public void sendEmail(String subject,String message) {
        Log.i("Send email", "");
        String[] TO = {"quirin212@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try
        {
            app.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            app.finish();
            Log.i("Finished sending email...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(app.getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
