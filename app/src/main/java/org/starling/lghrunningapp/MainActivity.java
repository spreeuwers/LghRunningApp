package org.starling.lghrunningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final String MESSENGER_INTENT_KEY = "msg-intent-key";
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RUNNING = 1;
    private static final int STOPPED = 0;
    private static final int PAUSED = 2;
    private IncomingMessageHandler mHandler;


    private TextView locationMsg;
    private TextView textView;
    private WebView webview1;

    private int activityState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        activityState = MainActivity.STOPPED;

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);

        locationMsg = findViewById(R.id.textView1);

        mHandler = new IncomingMessageHandler();

        checkGPSEnabled();
        requestPermissions();
        addServiceWorkerSupport();
        webview1 = findViewById(R.id.webview1);
        webview1.setWebViewClient(new WebViewClient1());
        webview1.setWebChromeClient(new WebChromeClient1());
        WebSettings webSettings = webview1.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webview1.clearCache(true);
        webview1.loadUrl("https://loopgroephouten.nl/lghrun");

        //actIntent = new Intent(this, Activity2.class);
        //ApplicationClass.context = this;

        webview1.addJavascriptInterface(this, "Android");
    }

    private void addServiceWorkerSupport(){
        ServiceWorkerController swController = ServiceWorkerController.getInstance();
        swController.setServiceWorkerClient(new ServiceWorkerClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebResourceRequest request) {
                Log.e(TAG, "in service worker. isMainFrame:"+request.isForMainFrame() +": " + request.getUrl());
                return null;
            }
        });
        swController.getServiceWorkerWebSettings().setAllowContentAccess(true);



    }

    public void clickTextView(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // can be schedule in this way also
                //  Utils.scheduleJob(this, LocationUpdatesService.class);
                //doing this way to communicate via messenger
                // Start service and provide it a way to communicate with this class.
                Intent startServiceIntent = new Intent(this, LocationUpdatesService.class);
                Messenger messengerIncoming = new Messenger(mHandler);
                startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
                startService(startServiceIntent);
            } else {
                // Permission denied.
                finish();
            }
        }
    }

    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage..." + msg.toString());

            super.handleMessage(msg);

            switch (msg.what) {
                case LocationUpdatesService.LOCATION_MESSAGE:
                    Location loc = (Location) msg.obj;

                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    Object[] timestamps = LocationUpdatesComponent.timestamps.toArray();
                    for (int i = 0; i < timestamps.length - 1; i++) {
                        long t2 = (long) timestamps[i + 1];
                        long t1 = (long) timestamps[i];
                        timestamps[i] = t2 - t1;
                    }
                    Long[] difs = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
                    for (int i = 0; i < difs.length - 1; i++) {
                        int x = timestamps.length - i - 1;
                        if (x < 0) break;
                        difs[i] = (long) timestamps[x];

                    }


                    textView.setText("" + LocationUpdatesComponent.size() + "\n" + Arrays.toString(difs));

                    locationMsg.setText("LAT :  " + loc.getLatitude() + "\nLNG : " + loc.getLongitude() + "\n\n" + loc.toString() + " \n\n\nLast updated- " + currentDateTimeString);
                    if (MainActivity.RUNNING == activityState) {
                        onLocationChanged(loc);
                    }
                    break;
            }
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private void checkGPSEnabled() {
       LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();

        }

    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String invoke(String command) {
        try {
            Log.e(TAG,"invoke:"  + command);
            String[] parts = command.split("\\s+");
            String methodName = parts[0];
            //first part is the method name
            //following parts must be string params
            Method method = null;

            Method[] methods = this.getClass().getDeclaredMethods();
            for (int i=0; i < methods.length; i++){
               if (methods[i].getName().equals(methodName)){
                   method = methods[i];
                   break;
               }
            }
            // Method method = this.getClass().getMethod(parts[0]);
            String[] args = new String[parts.length-1];
            for (int i=1; i < parts.length; i++)
                args[i-1] = parts[i];

            //always returns a string
            return "" + method.invoke(this, (Object[]) args);

        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
        return "";
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void onLocationChanged(Location location) {
        //Toast.makeText(this,"Location changed!",Toast.LENGTH_SHORT).show();
        if (location == null){
            Log.d(TAG, "location is null!");
            return;

        }

        RoutePoint rp = new RoutePoint(
                round(location.getLatitude(),6),
                round(location.getLongitude(),6),
                location.getTime(), location.getSpeed());
        rp.nrOfPoints = LocationUpdatesComponent.size();//getNrofPoints();
        Gson gson = new Gson();
        String json = gson.toJson(rp);
        String url = "javascript:onLocationChanged(" + json + ")";
        //Toast.makeText(this,url,Toast.LENGTH_SHORT).show();
        webview1.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String intentName = ".LocationUpdatesService";
        Intent i=new Intent(intentName);
        this.stopService(i);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }

    public void start(){
        activityState = MainActivity.RUNNING;
        LocationUpdatesComponent.reset();
        toast("started");
    }

    public void stop(){
        toast("stopped");
        activityState = MainActivity.STOPPED;
    }

    public void pause(){
        toast("paused");
        activityState = MainActivity.PAUSED;
    }

    public void reload(){
        webview1.clearCache(true);
        webview1.loadUrl("https://loopgroephouten.nl/lghrun");
   }

   public String getRouteJson(){
        String result = LocationUpdatesComponent.getRouteJson();
        return result;
    }

   public String email(String recipient, String subject, String body){
        try {
            Log.e(TAG, "email:" + subject + " " + body);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipient, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(emailIntent, "Send as email"));
            return "ok";
        } catch(Exception e){
            return e.getMessage();
        }
   }


   public void exit(){
        finish();
   }
}