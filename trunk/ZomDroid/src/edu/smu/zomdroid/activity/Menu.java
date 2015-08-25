package edu.smu.zomdroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.Player;
import edu.smu.zomdroid.R;
import edu.smu.zomdroid.service.LocationService;

public class Menu extends Activity implements OnClickListener {
    private boolean newGame = false;

    /**
     * Creates an activity with the following buttons:
     * 1. New Game
     * 2. Join Game
     * 3. About Game
     * 4. Exit Game
     * 
     * It also has a menu inflator for displaying the game settings.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        View NewButton = this.findViewById(R.id.newButton);
        NewButton.setOnClickListener(this);

        View JoinButton = this.findViewById(R.id.joinButton);
        JoinButton.setOnClickListener(this);

        View AboutButton = this.findViewById(R.id.aboutButton);
        AboutButton.setOnClickListener(this);

        View ExitButton = this.findViewById(R.id.exitButton);
        ExitButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent i;

        switch(v.getId()) {
            case R.id.newButton:
                i = new Intent(this, Registration.class);
                newGame = true;
                i.putExtra("newGame", newGame);
                startActivity(i);
                break;
            case R.id.joinButton:
                i = new Intent(this, Registration.class);
                newGame = false;
                i.putExtra("newGame", newGame);
                startActivity(i);
                break;
            case R.id.aboutButton:
                i = new Intent(this, About.class);
                startActivity(i);
                break;
            case R.id.exitButton:
                stopService(new Intent(this, LocationService.class));
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ZomDroid.TAG, "Reading GameEngine parameters");
        GameEngine.getInstance().elapsedTime = getElapsedTime(getApplicationContext());
        GameEngine.getInstance().infectedBy = getInfectedBy(getApplicationContext());
        Log.d(ZomDroid.TAG, "elapsedTime="
                + getElapsedTime(getApplicationContext()) + ", infectedBy="
                + getInfectedBy(getApplicationContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ZomDroid.TAG, "Saving GameEngine parameters");
        Editor editor = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).edit();
        editor.putString(GameEngine.INFECTED_BY,
                GameEngine.getInstance().infectedBy);
        editor.putInt(GameEngine.ELAPSED_TIME,
                GameEngine.getInstance().elapsedTime);
        editor.commit();
        Log.d(ZomDroid.TAG, "elapsedTime="
                + getElapsedTime(getApplicationContext()) + ", infectedBy="
                + getInfectedBy(getApplicationContext()));
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(GameEngine.MY_PLAYER,
                GameEngine.getInstance().myPlayer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        GameEngine.getInstance().myPlayer = (Player)bundle
                .getSerializable(GameEngine.MY_PLAYER);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                return true;
        }
        return false;
    }

    /**
     * Get the current value of the elapsed time
     */
    public static int getElapsedTime(Context context) {
        if(context == null)
            return GameEngine.ELAPSED_TIME_DEF;
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                GameEngine.ELAPSED_TIME, GameEngine.ELAPSED_TIME_DEF);
    }

    /** Get the current value of infected by */
    public static String getInfectedBy(Context context) {
        if(context == null)
            return GameEngine.INFECTED_BY_DEF;
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(GameEngine.INFECTED_BY, GameEngine.INFECTED_BY_DEF);
    }
}
