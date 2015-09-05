package androidhive.info.aegis.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;

import aegis.com.aegis.activity.ApolloniusSolver;
import aegis.com.aegis.activity.Circle;
import aegis.com.aegis.activity.ListWifi;
import aegis.com.aegis.activity.XYPoint;
import androidhive.info.materialdesign.R;

    public class WifiFragment extends android.support.v4.app.Fragment{
        ListView lv ;
        TextView tv;
        ArrayList<Integer> circles = new ArrayList<Integer>();
        WifiManager mainWifi;
        WifiReceiver receiverWifi;
        StringBuilder sb = new StringBuilder();
        ArrayAdapter<String> adapter;
        Context context ;
        Button b ;
        View myView ;
        private final Handler handler = new Handler();

        @Override
        public void onDestroyView() {

            if (this != null) {
                //android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(this);
                fragmentTransaction.commit();

            }

            super.onDestroyView();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment


            return inflater.inflate(R.layout.fragment__wifi, container, false);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getActivity();


            mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            receiverWifi = new WifiReceiver();
            context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if(mainWifi.isWifiEnabled()==false)
            {
                mainWifi.setWifiEnabled(true);
            }
            doInback();

        }
//menu.add(0, 0, 0, "Refresh");


        public boolean ac = true;

        public void TestWifi(){
            if(getView() != null){
                tv = (TextView) getView().findViewById(R.id.textView2);
                lv = (ListView) getView().findViewById(R.id.listView);
                //sv = (SurfaceView) findViewById(R.id.surfaceView);

                b = (Button) getView().findViewById(R.id.wifibutton);
            }

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // sv = new BubbleSurfaceView(getApplicationContext());
                        Intent myIntent = new Intent(getActivity(), ListWifi.class);
                        myIntent.putExtra("Cir", circles);
                        WifiFragment.this.startActivity(myIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            //Context c = this.getApplicationContext();
            //WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
            //wifi.startScan();

            ArrayList<String> connections;
            ArrayList<Float> Signal_Strenth;
            ArrayList<String> FullSpot;

            connections=new ArrayList<String>();
            Signal_Strenth= new ArrayList<Float>();
            FullSpot = new ArrayList<String>();
            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            if (wifiList.size() == 0)
            {
                FullSpot.add("No Wifi's In Area");
                tv.setText("Not In Sector");
            }else {
                for (int i = 0; i < wifiList.size(); i++) {
                    //connections.add(wifiList.get(i).SSID);
                    //Signal_Strenth.add((float) wifiList.get(i).level);
                    DecimalFormat df = new DecimalFormat("#.##");
                    // Log.d(TAG, wifiList.get(i).BSSID + ": "+ wifiList.get(i).level + ", d: " + df.format(calculateDistance((double) wifiList.get(i).level, wifiList.get(i).frequency)) + "m");

                    FullSpot.add("WiFi Name: " + wifiList.get(i).SSID + "\nSignal Distance: " + wifiList.get(i).BSSID + ": " + wifiList.get(i).level + ", \nDistance: " + df.format(calculateDistance((double) wifiList.get(i).level, wifiList.get(i).frequency)) + "m");

                    }
                for(int k = 0; k < wifiList.size(); k++){
//                    Toast.makeText(context,wifiList.size(), Toast.LENGTH_LONG).show();
                        String idd = wifiList.get(k).BSSID.toString();
                        //Toast.makeText(context,idd, Toast.LENGTH_LONG).show();
                        if (idd.equals("0e:8b:fd:d4:4d:b5")) {
                            //Toast.makeText(context,"In Loop Found Wifi :"+wifiList.get(k).SSID, Toast.LENGTH_LONG).show();
                            int level1 = (int) calculateDistance((double) wifiList.get(k).level, wifiList.get(k).frequency);

                            //Toast.makeText(context,"level1"+level1, Toast.LENGTH_LONG).show();
                            if (level1 <= 10) {
                            //Toast.makeText(context,"In Loop Found Wifi :"+wifiList.get(k).SSID, Toast.LENGTH_LONG).show();
                            tv.setText(wifiList.get(k).SSID + " Sector");
                            //Toast.makeText(context,"After TextSEt Toast", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }else{
                        tv.setText("No Sectors Found Around You");
                    }
                }
                doInback();
            }

            if (wifiList.size() != 0)
            {
                int level = (int) calculateDistance((double) wifiList.get(0).level, wifiList.get(0).frequency);

                circles.add(level);
            }

            //level = (int) calculateDistance((double) wifiList.get(1).level, wifiList.get(1).frequency);
            //circles.add(level);
            //level = (int) calculateDistance((double) wifiList.get(2).level, wifiList.get(2).frequency);
            //circles.add(level);
            ArrayList<String> values;
            values = FullSpot;

            if(getView() != null){
                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), R.layout.mylistcolor ,android.R.id.text1,values);

                lv.setAdapter(adapter);
            }


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int itemPosition = position;

                    String itemValue = (String) lv.getItemAtPosition(position);

                    Toast.makeText(context, "Selected: " + itemValue, Toast.LENGTH_LONG).show();
                }
            });
            lv.invalidateViews();
            tv.invalidate();
            //Intent myIntent = new Intent(MainActivity.this, ListWifi.class);
            //myIntent.putStringArrayListExtra("Wifi",FullSpot);
            //MainActivity.this.startActivity(myIntent);


        }

        public double calculateDistance(double levelInDb, double freqInMHz){
            double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
            return  Math.pow(10.0, exp);
        }

        public void doInback()
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() { // TODO Auto-generated method
                    mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    receiverWifi = new WifiReceiver();
                    context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    mainWifi.startScan();
                    TestWifi();
                    ;
                }
            }, 100);
        }



        @Override public void onPause()
        {
            context.unregisterReceiver(receiverWifi);
            super.onPause();
        }

        @Override public void onResume()
        {
            context.registerReceiver(receiverWifi, new IntentFilter( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            super.onResume();
        }



        class WifiReceiver extends BroadcastReceiver
        {

            public void onReceive(Context c, Intent intent)
            {
                ArrayList<String> connections=new ArrayList<String>();
                ArrayList<Float> Signal_Strenth= new ArrayList<Float>();
                sb = new StringBuilder();
                List<ScanResult> wifiList;
                wifiList = mainWifi.getScanResults();
                for(int i = 0; i < wifiList.size(); i++)
                {
                    connections.add(wifiList.get(i).SSID);
                }


            }
        }

        public class BubbleSurfaceView extends SurfaceView implements SurfaceHolder.Callback
        {
            private SurfaceHolder sh;
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            public BubbleSurfaceView(Context context)
            {
                super(context);
                sh = getHolder();
                sh.addCallback(this);
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
            }
            public void surfaceCreated(SurfaceHolder holder) {
                Circle c3 = new Circle(new XYPoint(0, 0), 0);
                Circle c2 = new Circle(new XYPoint(0, 0), 0);
                Circle c1 = new Circle(new XYPoint(485, 300), 5);
                ApolloniusSolver s = new ApolloniusSolver();

                Canvas canvas = sh.lockCanvas();
                canvas.drawColor(Color.WHITE);
                canvas.drawCircle(100, 200, 50, paint);
                Circle cap = s.solveApollonius(c1, c2, c3, -1, -1, -1);
                canvas.drawCircle(cap.getCenterX(),cap.getCenterY(), cap.getRadius(), paint);
                sh.unlockCanvasAndPost(canvas);
            }
            public void surfaceChanged(SurfaceHolder holder, int format, int width, 			int height) {
            }
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        }


    }