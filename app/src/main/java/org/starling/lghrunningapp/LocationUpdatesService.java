package org.starling.lghrunningapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
//import androidx.NotificationCompat;
import android.util.Log;

import static org.starling.lghrunningapp.MainActivity.MESSENGER_INTENT_KEY;


/**
 * location update service continues to running and getting location information
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationUpdatesService extends JobService implements LocationUpdatesComponent.ILocationProvider {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();
    public static final int LOCATION_MESSAGE = 9999;
    private static final int NOTIFICATION_ID = 1;
    private static final String SERVICE_CHANNEL_ID = "SERVICE_CHANNEL_GPS";
    private final NotificationChannel test = createServiceChannel("test");

    private Messenger mActivityMessenger;

    private LocationUpdatesComponent locationUpdatesComponent;
    //public static List<Location> locations = new ArrayList<>();

    public LocationUpdatesService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob....");
//        Utils.scheduleJob(getApplicationContext(), LocationUpdatesService.class);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob....");

        locationUpdatesComponent.onStop();

        return false;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "created...............");

        locationUpdatesComponent = new LocationUpdatesComponent(this);

        locationUpdatesComponent.onCreate(this);
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand Service started");
        if (intent != null) {
            mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        }
        //hey request for location updates
        locationUpdatesComponent.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startAsForeground();
        else
            startForeground(1, new Notification());

        return START_STICKY;
    }

    private void startAsForeground(){
        String NOTIFICATION_CHANNEL_ID = this.getClass().getCanonicalName();
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                //.setSmallIcon(R.drawable.icon_1)
                .setContentTitle("LGHRunningApp is watching GPS")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel createServiceChannel(CharSequence name) {
        NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
        channel.enableVibration(false);
        return channel;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy....");

    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private void sendMessage(int messageID, Location location) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }

        Message m = Message.obtain();
        m.what = messageID;
        m.obj = location;
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
    }

    @Override
    public void onLocationUpdate(Location location) {

        sendMessage(LOCATION_MESSAGE, location);
    }
}