package org.starling.lghrunningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final String MESSENGER_INTENT_KEY = "msg-intent-key";
    public static final String TAG = MainActivity.class.getSimpleName();
    private IncomingMessageHandler mHandler;


    private TextView locationMsg;
    private TextView textView;
    private WebView webview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView2);

        locationMsg = findViewById(R.id.textView1);

        mHandler = new IncomingMessageHandler();


        requestPermissions();

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
                    Location obj = (Location) msg.obj;
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    Object[] timestamps = LocationUpdatesComponent.timestamps.toArray();
                    for (int i = 0; i < timestamps.length-1; i++){
                        long t2 = (long)timestamps[i+1];
                        long t1 = (long)timestamps[i];
                        timestamps[i] = t2 - t1;
                    }
                    Long[] difs = {0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L,0L};
                    for (int i = 0; i < difs.length-1; i++){
                        int x = timestamps.length -i -1;
                        if (x < 0) break;
                        difs[i] = (long) timestamps[x];

                    }


                    textView.setText("" + LocationUpdatesComponent.locations.size() + "\n"  + Arrays.toString(difs));

                    locationMsg.setText("LAT :  " + obj.getLatitude() + "\nLNG : " + obj.getLongitude() + "\n\n" + obj.toString() + " \n\n\nLast updated- " + currentDateTimeString);
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

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String invoke(String command) {
        try {
            String[] parts = command.split("\\s+");
            //first part is the method name
            //following parts must be string params
            Method method = this.getClass().getMethod(parts[0]);
            String[] args = new String[parts.length-1];
            for (int i=1; i < parts.length; i++)
                args[i] = parts[i];

            //always returns a string
            return "" + method.invoke(this, (Object[]) args);

        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
        return "";
    }
}