package aegis.com.aegis.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

public class WiFiScanReceiver extends BroadcastReceiver implements Serializable {

    private static final String TAG = "WiFiScanReceiver";
    private static final int samples = 30;
    private WiFiDemo wifiDemo;
    private double totaldistance;
    private ScanResult bestSignal = null;
    private List<ScanResult> results;

    public WiFiScanReceiver(WiFiDemo wifiDemo) {
        super();
        this.wifiDemo = wifiDemo;
    }

    @Override
    public void onReceive(Context c, Intent intent) {

        for (int i = 0; i < samples; i++) {
            if (results == null) {
                results = wifiDemo.wifi.getScanResults();
                for (ScanResult result : results) {
                    if (bestSignal == null
                            || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
                        bestSignal = result;
                    totaldistance += calculateDistance(bestSignal.level, bestSignal.frequency);
                }
            }
        }


        String message = String.format("%s networks found. %s is the strongest. Distance:" + totaldistance + "M",
                                       results.size(), bestSignal.SSID);

        Log.d(TAG, "onReceive() message: " + message);
        new Notifier(c).Notify("WIFI", message);
    }


    public double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}

