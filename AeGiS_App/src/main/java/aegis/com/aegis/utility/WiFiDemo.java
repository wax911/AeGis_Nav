package aegis.com.aegis.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiDemo {
    private static final String TAG = "Wifi";
    private static boolean originalState = false;
    public WifiManager wifi;
    public BroadcastReceiver receiver;
    private Context application;

    //Set our constructor
    public WiFiDemo(Context context) {
        if (context != null) {
            application = context;
            // Setup WiFi
            wifi = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        }
    }

    public void setWiFiStatus() {
        if (!wifi.isWifiEnabled())
            wifi.setWifiEnabled(true);
        originalState = wifi.isWifiEnabled();


        // Register Broadcast Receiver
        if (receiver == null)
            receiver = new WiFiScanReceiver(this);

        application.registerReceiver(receiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Log.d(TAG, "onCreate()");
    }

    public void onStop() {
        application.unregisterReceiver(receiver);
        if (wifi.isWifiEnabled() != originalState)
            wifi.setWifiEnabled(originalState);
    }

    public void onResume() {
        application.registerReceiver(receiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //Initiate our scan
    public void onClick() {
        Log.d(TAG, "onClick() wifi.startScan()");
        wifi.startScan();
    }

}