package aegis.com.aegis.utility;

/**
 * Created by Maxwell on 10/13/2015.
 * Set all intent keys to use in here
 */
public interface IntentNames {
    String MAP_INTENT_KEY = "com.aegis.map.location.data";
    String Search_View_KEY = "com.aegis.view.search.query";
    int Places_Request_Code = 109;
    // intent request code to handle updating play services if needed.
    int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    int RC_HANDLE_CAMERA_PERM = 2;
}
