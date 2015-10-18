package aegis.com.aegis.utility;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Maxwell on 10/13/2015.
 */
public final class DismissKeyboard
{
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
