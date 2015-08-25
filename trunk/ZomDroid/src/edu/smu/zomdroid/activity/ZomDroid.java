package edu.smu.zomdroid.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.Player;
import edu.smu.zomdroid.R;
import edu.smu.zomdroid.database.ZomdroidState;
import edu.smu.zomdroid.map.VicinityMap;
import edu.smu.zomdroid.service.LocationService;

/**
 * Main class for ZomDroid game. Shows score and (eventually) inventory.
 * Includes button to show VicinityMap
 */
public class ZomDroid extends TabActivity implements OnClickListener {
    public static final String TAG = "ZomDroid";
    public static final String BASE_SERVICE_URL = "http://lyle.smu.edu/~coyle/php/zomdroid/";
    private static final String PROPS_FILE = "/sdcard/zomdroid.properties";
    private String phoneNumber;
    private String mapKey;
    private TabHost mTabHost;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);

        // Look for zomdroid.properties for cell number and api key
        initProperties();

        // Bundle extras = getIntent().getExtras();
        // debug = extras.getBoolean("isTest");
        if(!Settings.getDebug()) {
            TelephonyManager phoneMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            // The Google Ion phones given out at the Developer Conference don't
            // have phone numbers.
            if(phoneMgr.getLine1Number() == null)
                Log.d(TAG, "line1Number is empty");
            else
                phoneNumber = phoneMgr.getLine1Number();
            Log.d(TAG, "phoneNumber=" + phoneNumber);
        }
        if(GameEngine.getInstance().myPlayer.phoneNumber.equals("")
                || GameEngine.getInstance().myPlayer.phoneNumber
                        .equals("15555218135"))
            GameEngine.getInstance().myPlayer.phoneNumber = phoneNumber;

        Intent i;
        i = new Intent(this, VicinityMap.class);
        i.putExtra("mapKey", mapKey);

        // Initialize tabs.
        mTabHost = getTabHost();
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator("Map")
                .setContent(i)); // passing VicinityMap intent for tab_1
        // content.
        mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator("Scores")
                .setContent(R.id.gameContent2));
        mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator("Status")
                .setContent(R.id.gameContent3));
        mTabHost.setCurrentTab(0); // open with first tab displayed - tab_1

        mTabHost.setOnTabChangedListener(new TabHandler());

        /*
         * TODO:Send scores to arrays.xml and show scores in tab 2 with
         * arrays.xml
         */

    }

    private class TabHandler implements OnTabChangeListener {

        TabHandler() {
        }

        @Override
        public void onTabChanged(String tabId) {
            updateScoreScreen();
            updateStatusScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Put interval and debug in intent
        startService(new Intent(this, LocationService.class));
        updateStatusScreen();

        // This creates the database and puts all the users in a table. We might
        // want to wait until we make sure the network call succeeds before
        // calling though...
        ZomdroidState state = new ZomdroidState();
        state.addPlayer(GameEngine.getInstance().getPlayers(), this);
    }

    @Override
    public void onClick(View v) {
        /*
         * Log.d(TAG, "onClick"); Intent i; switch(v.getId()) { case
         * R.id.mapButton: i = new Intent(this, VicinityMap.class);
         * i.putExtra("mapKey", mapKey); startActivity(i); break; }
         */
    }

    private void updateStatusScreen() {
        TextView tv = (TextView)findViewById(R.id.status);
        String temp = new String("Status: ");
        temp = temp.concat(GameEngine.getInstance().myPlayer.zhType);
        tv.setText(temp);
        mTabHost.refreshDrawableState();
    }

    private void updateScoreScreen() {
        TextView temp;

        temp = (TextView)findViewById(R.id.scoreRow0User);
        temp.setText("userName");
        temp = (TextView)findViewById(R.id.scoreRow0Score);
        temp.setText("Score");

        temp = (TextView)findViewById(R.id.scoreRow1User);
        temp.setText("userName");
        temp = (TextView)findViewById(R.id.scoreRow1Score);
        temp.setText("Score");

        temp = (TextView)findViewById(R.id.scoreRow2User);
        temp.setText("userName");
        temp = (TextView)findViewById(R.id.scoreRow2Score);
        temp.setText("Score");

        temp = (TextView)findViewById(R.id.scoreRow3User);
        temp.setText("userName");
        temp = (TextView)findViewById(R.id.scoreRow3Score);
        temp.setText("Score");

        temp = (TextView)findViewById(R.id.scoreRow4User);
        temp.setText("userName");
        temp = (TextView)findViewById(R.id.scoreRow4Score);
        temp.setText("Score");

        mTabHost.refreshDrawableState();
    }
    
    @SuppressWarnings("unchecked")
	private class ScoreSort implements java.util.Comparator{
		@Override
		public int compare(Object object1, Object object2) {
			return ((Player)(object2)).score - ((Player)(object1)).score;
		}
    }
   
    private void initProperties() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(PROPS_FILE));
            Log.d(TAG, "props=" + props.toString());
            phoneNumber = props.getProperty("phone");
            mapKey = props.getProperty("mapkey");
        }
        catch(FileNotFoundException e) {
            Log.e(TAG, "Could not find " + PROPS_FILE, e);
        }
        catch(IOException e) {
            Log.e(TAG, "Problem reading " + PROPS_FILE, e);
        }
    }
}