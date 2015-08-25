package edu.smu.zomdroid.map;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.Player;
import edu.smu.zomdroid.R;
import edu.smu.zomdroid.activity.Notifications;
import edu.smu.zomdroid.activity.Settings;
import edu.smu.zomdroid.activity.ZomDroid;

/**
 * Shows the user location and any zombies that are close by.
 */
public class VicinityMap extends MapActivity {
    private MapView map;
    private MapController controller;
    private MyLocationOverlay overlay;
    private Timer timer;
    private boolean started = false;

    /**
     * Initialize the map view. Looks for Google Maps API key loaded from
     * zomdroid.properties
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ZomDroid.TAG, "Initializing map");
        setContentView(R.layout.map);
        map = new MapView(this, getIntent().getStringExtra("mapKey"));
        setContentView(map, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        map.setEnabled(true);
        map.setClickable(true);
        map.setSatellite(false);
        map.setBuiltInZoomControls(true);
        controller = map.getController();
        timer = new Timer(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMyLocation();
        if(!started) {
            Log.d(ZomDroid.TAG, "Initializing VicinityTask");
            timer.schedule(new VicinityTask(), 5000, Settings.getUpdates());
            started = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overlay.disableMyLocation();
        if(started) {
            Log.d(ZomDroid.TAG, "Cancelling VicinityTask");
            timer.cancel();
            started = false;
        }
    }

    /**
     * Start tracking the position on the map.
     */
    private void initMyLocation() {
        Log.d(ZomDroid.TAG, "initMyLocation");
        overlay = new MyLocationOverlay(this, map);
        if(overlay.enableMyLocation())
            Log.d(ZomDroid.TAG, "overlay enabled");
        else
            Log.w(ZomDroid.TAG, "Could not enable overlay");
        overlay.runOnFirstFix(new Runnable() {
            public void run() {
                Log.d(ZomDroid.TAG, "Animating to my location");
                // Zoom in to current location
                controller = map.getController();
                controller.setZoom(16);
                controller.animateTo(overlay.getMyLocation());
            }
        });
    }

    @Override
    protected boolean isRouteDisplayed() {
        // Required by MapActivity
        return false;
    }

    class VicinityTask extends TimerTask {

        /**
         * Updates the map view with all zombies and humans
         */
        public void run() {
            Log.d(ZomDroid.TAG, "Running VicinityTask");
            UsersOverlay humanOverlay = new UsersOverlay(VicinityMap.this,
                    getApplicationContext().getResources().getDrawable(
                            R.drawable.humanmark));
            UsersOverlay infectedOverlay = new UsersOverlay(VicinityMap.this,
                    getApplicationContext().getResources().getDrawable(
                            R.drawable.infectedmark));
            UsersOverlay zombieOverlay = new UsersOverlay(VicinityMap.this,
                    getApplicationContext().getResources().getDrawable(
                            R.drawable.zombiemark));
            UsersOverlay medicOverlay = new UsersOverlay(VicinityMap.this,
                    getApplicationContext().getResources().getDrawable(
                            R.drawable.medicmark));
            UsersOverlay hospitalOverlay = new UsersOverlay(VicinityMap.this,
            		getApplicationContext().getResources().getDrawable(
            				R.drawable.hospitalmark));

            for(Player player : GameEngine.getInstance().getPlayers()) {
            	
                Double gpx = Double.valueOf(Location.convert(player.gpx,
                        Location.FORMAT_DEGREES)) * 1E6;
                Double gpy = Double.valueOf(Location.convert(player.gpy,
                        Location.FORMAT_DEGREES)) * 1E6;
                Log.d(ZomDroid.TAG, "gpx=" + gpx.intValue() + ", gpy="
                        + gpy.intValue());
                OverlayItem item = new OverlayItem(new GeoPoint(gpx.intValue(),
                        (gpy.intValue())), player.id, player.zhType);
                if(player.zhType.equals(GameEngine.HUMAN))
                    humanOverlay.addOverlay(item);
                else
                    if(player.zhType.equals(GameEngine.INFECTED))
                        infectedOverlay.addOverlay(item);
                    else
                        if(player.zhType.equals(GameEngine.ZOMBIE))
                            zombieOverlay.addOverlay(item);
                        else
                            if(player.zhType.equals(GameEngine.MEDIC))
                                medicOverlay.addOverlay(item);
                            else
                            	if(player.zhType.equals(GameEngine.HOSPITAL))
                            		hospitalOverlay.addOverlay(item);
                
                if(GameEngine.getInstance().stateChanged)
                	showAlert();

            }
            synchronized(GameEngine.getInstance()) {
                List<Overlay> overlays = map.getOverlays();
                overlays.clear();
                if(humanOverlay.size() > 0)
                    overlays.add(humanOverlay);
                if(infectedOverlay.size() > 0)
                    overlays.add(infectedOverlay);
                if(zombieOverlay.size() > 0)
                    overlays.add(zombieOverlay);
                if(medicOverlay.size() > 0)
                    overlays.add(medicOverlay);
                map.postInvalidate();
            }
        }
    }

    public void showZombieAlert(Context context) {
    	
    	/**
    	 * TODO Added music or vibration when being converted.
    	 */
       
        Toast.makeText(context, "YOU ARE A ZOMBIE", Toast.LENGTH_LONG).show();
    }
    
    public void showAlert(){
    	((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(3000);
    	
    	Log.v("Zomdroid", "Notification: " + GameEngine.getInstance().notifications);
    	
    
    	
    	Intent i = new Intent(this, Notifications.class);
    	i.putExtra("notifcations", GameEngine.getInstance().notifications);
    	
    	startActivity(i);
        
        GameEngine.getInstance().notifications = "";
        GameEngine.getInstance().stateChanged = false;
        
        
        
    }
}
