package edu.smu.zomdroid.service;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.HttpLocUtil;
import edu.smu.zomdroid.Player;
import edu.smu.zomdroid.activity.Settings;
import edu.smu.zomdroid.activity.ZomDroid;

/**
 * A periodic network service that uses GPS to determine position and send
 * updates. This service requires network <b>and</b> GPS to be enabled.
 */
public class LocationService extends Service {
    private LocationManager mgr;
    private Timer timer;
    HttpLocUtil httpLocUtil;
    private final String[] cities = { "Umphrey Lee Center", "Ford Stadium",
          "Meadows Museum", "Expressway Tower", "Moody Coliseum", "Dedman",
          "Embrey", "Junkins", "Owen Arts Center", "Fondren Science Building" };
    private final double[] lats = { 32.844621, 32.839807, 32.839807, 32.842187,
            32.839915, 32.84664, 32.844585, 32.844062, 32.843755, 32.848226 };
    private final double[] longs = { -96.785624, -96.78189, -96.785088,
            -96.775904, -96.780539, -96.782877, -96.781676, -96.781676,
            -96.784573, -96.781569 };
    private boolean started = false;

    /**
     * Binding service not implemented
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Sets up private members and LocationManager instance
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ZomDroid.TAG, "onCreate");
        timer = new Timer(true);
        httpLocUtil = new HttpLocUtil();
        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);
    }

    /**
     * Stops the TimerTask from sending any more location updates to the server
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ZomDroid.TAG, "onDestroy");
        if(started) {
            Log.d(ZomDroid.TAG, "Stopping UpdateTask");
            timer.cancel();
            started = false;
        }
    }

    /**
     * Schedules a TimerTask to periodically send location updates to the
     * server.
     * 
     * @param intent
     *            Not currently used
     * @param startId
     *            Not currently used
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(ZomDroid.TAG, "onStart");
        if(!started) {
            Log.d(ZomDroid.TAG, "Starting UpdateTask");
            timer.schedule(new UpdateTask(), 0, Settings
                    .getUpdates());
            started = true;
        }
    }

    /**
     * Inner class for scheduling location updates.
     */
    class UpdateTask extends TimerTask {

        /**
         * If we're in debug mode, randomly change the location to one of the
         * ten sample places. Otherwise, get our last known location. Sends
         * update to HttpLocUtil, which updates the GameEngine.
         */
        public void run() {
            Log.d(ZomDroid.TAG, "Running UpdateTask");
            Player myPlayer = GameEngine.getInstance().myPlayer;
            if(Settings.getDebug()) {
                int index = new Random().nextInt(10);
                Location testLocation = new Location(
                        LocationManager.GPS_PROVIDER);
                testLocation.setLatitude(lats[index]);
                testLocation.setLongitude(longs[index]);
                testLocation.setTime(System.currentTimeMillis());
                Log.d(ZomDroid.TAG, "Setting Location to " + cities[index]
                        + "(" + index + "), Lat=" + testLocation.getLatitude()
                        + ", Long=" + testLocation.getLongitude());
                mgr.setTestProviderLocation(LocationManager.GPS_PROVIDER,
                        testLocation);
                myPlayer.gpx = lats[index];
                myPlayer.gpy = longs[index];
            }
            else {
                final Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                Location lastKnownLoc = mgr.getLastKnownLocation(mgr
                        .getBestProvider(criteria, true));
                if(lastKnownLoc != null) {
                    myPlayer.gpx = lastKnownLoc.getLatitude();
                    myPlayer.gpy = lastKnownLoc.getLongitude();
                }
            }
            httpLocUtil.sendUpdate(myPlayer);
        }
    }
}
