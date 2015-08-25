package edu.smu.zomdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import edu.smu.zomdroid.R;

public class About extends Activity {

    @Override
    /**
     * Creates an activity that displays a brief
     *  synopsis of the Zomdroid game.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

    }
}
