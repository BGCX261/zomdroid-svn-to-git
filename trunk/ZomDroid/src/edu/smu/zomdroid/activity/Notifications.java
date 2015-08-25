package edu.smu.zomdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import edu.smu.zomdroid.R;

public class Notifications extends Activity {

    @Override
    /**
     * Creates an activity that displays a brief
     *  synopsis of the Zomdroid game.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        TextView notify = (TextView)findViewById(R.id.notificationText);
        
        Bundle extras = getIntent().getExtras();
        if( !extras.getString("nofication").equals(null) )
        	notify.setText(extras.getString("notification"));
        	
        
        
    }
}
