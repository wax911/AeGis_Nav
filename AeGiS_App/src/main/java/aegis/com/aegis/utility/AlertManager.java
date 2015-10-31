package aegis.com.aegis.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import aegis.com.aegis.R;

/**
 * Created by Maxwell on 10/31/2015.
 */
public class AlertManager {
    private AlertDialog.Builder builder;
    private Activity active;
    private DialogInterface.OnClickListener event;

    public AlertManager(Activity active, DialogInterface.OnClickListener event) {
        this.active = active;
        this.event = event;
    }

    public void Create(String header, String body) {
        // Build the alert dialog
        builder = new AlertDialog.Builder(active);
        builder.setTitle(header);
        builder.setMessage(body);
        builder.setIcon(R.drawable.navigation_icon);
        builder.setPositiveButton(R.string.ok, event);
        Build();
    }

    private void Build() {
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
