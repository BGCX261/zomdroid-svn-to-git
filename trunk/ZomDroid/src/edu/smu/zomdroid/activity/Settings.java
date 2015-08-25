package edu.smu.zomdroid.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.R;

public class Settings extends TabActivity {
    // Option names and default values
    public static final String OPT_TYPE = "type";
    public static final String OPT_TYPE_DEF = GameEngine.HUMAN;
    public static final String OPT_UPDATES = "updates";
    public static final long OPT_UPDATES_DEF = 60000;
    public static final String OPT_DEBUG = "debug";
    public static boolean OPT_DEBUG_DEF = false;
    private static int radioCheckedId = R.id.radioHuman;
    private static int position = 1;
    private static long updateInterval = OPT_UPDATES_DEF;
    private static boolean debug = OPT_DEBUG_DEF;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ZomDroid.TAG, "onCreate");
        setContentView(R.xml.preferences);
        context = getApplicationContext();

        final RadioGroup typeGroup = (RadioGroup)findViewById(R.id.radioType);
        typeGroup.check(radioCheckedId);
        typeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioCheckedId = checkedId;
            }
        });

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.updateIntervals,
                        android.R.layout.simple_spinner_item);
        
        
        adapter
                .setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        
        
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view,
                    int position, long id) {
                // TODO: We should be able to use the arrays.xml here...
                switch(position) {
                    case 0:
                        updateInterval = 10000;
                        break;
                    case 1:
                        updateInterval = 60000;
                        break;
                    case 2:
                        updateInterval = 300000;
                        break;
                    case 3:
                        updateInterval = 900000;
                        break;
                    case 4:
                        updateInterval = 1800000;
                        break;
                }
                Settings.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> spinner) {
                // Do nothing
            }
        });

        final CheckBox checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox.setChecked(debug);
        
        checkBox
                .setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        debug = isChecked;
                    }
                });

        //Initialize tabs
        final TabHost mTabHost = getTabHost();
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("settings_tab1")
                .setIndicator("General").setContent(R.id.content1));
        mTabHost.addTab(mTabHost.newTabSpec("settings_tab2").setIndicator("GPS")
                .setContent(R.id.content2));
        mTabHost.addTab(mTabHost.newTabSpec("settings_tab3").setIndicator("Debug")
                .setContent(R.id.content3));
        mTabHost.setCurrentTab(0);//open in first tab - tab_test1
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(ZomDroid.TAG, "Saving user preferences");
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
//        editor.putString(OPT_TYPE, ((RadioButton)findViewById(radioCheckedId))
//                .getText().toString());
        editor.putLong(OPT_UPDATES, updateInterval);
        editor.putBoolean(OPT_DEBUG, ((CheckBox)findViewById(R.id.checkbox))
                .isChecked());
        editor.commit();
        Log.d(ZomDroid.TAG, "type=" + getType() + ", updateInterval="
                + getUpdates() + ", debug=" + getDebug());
    }

    /** Get the current value of the type option */
    public static String getType() {
        if(context == null)
            return OPT_TYPE_DEF;
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_TYPE, OPT_TYPE_DEF);
    }

    /** Get the current value of the updates option */
    public static long getUpdates() {
        if(context == null)
            return OPT_UPDATES_DEF;
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                OPT_UPDATES, OPT_UPDATES_DEF);
    }

    /** Get the current value of the debug option */
    public static boolean getDebug() {
        if(context == null)
            return OPT_DEBUG_DEF;
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_DEBUG, OPT_DEBUG_DEF);
    }
}
