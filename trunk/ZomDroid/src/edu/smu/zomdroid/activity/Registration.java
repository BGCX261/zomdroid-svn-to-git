package edu.smu.zomdroid.activity;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.smu.zomdroid.GameEngine;
import edu.smu.zomdroid.HttpLocUtil;
import edu.smu.zomdroid.R;

public class Registration extends Activity implements OnClickListener {

	/**
	 * Creates an activity that allows users to register for a game.
	 * 
	 * If the user selected "New Game" from the main screen, the activity will
	 * contain an editText  for creating a new game.
	 * 
	 * If the user selected "Join Game" from the main screen, the activity will
	 * contain a spinner for selecting a previously created game.
	 */
	
	RadioGroup radioG;
	@SuppressWarnings("unchecked")
	ArrayAdapter adapter;
	  
    @SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        Bundle extras = getIntent().getExtras();
        
        //Variable to determine if this is a new game.
        boolean newGame = extras.getBoolean("newGame"); 

        View playButton = this.findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        
        View refreshButton = this.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);
        
        EditText phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        String phoneNum =  
        	((TelephonyManager)getSystemService
        			(Context.TELEPHONY_SERVICE)).getLine1Number();
        
        Spinner gameIDSpinner = (Spinner)findViewById(R.id.gameIDSpinner);

        if(newGame)	
        {
        	gameIDSpinner.setVisibility(4);
        	refreshButton.setVisibility(4);
        }
        else
        {
          String rawGames = (new HttpLocUtil()).getGames();
          StringTokenizer tkn = new StringTokenizer(rawGames.trim(), ",");
          ArrayList<String> games = new ArrayList();
          
          while(tkn.hasMoreElements()){
        	  games.add(tkn.nextToken());
          }
                    
          adapter = new ArrayAdapter(this, 
        		android.R.layout.simple_spinner_item, games.toArray());
          		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          		  
          adapter.setNotifyOnChange(false); //List will not refresh unless refresh button is clicked
        	        
          gameIDSpinner.setAdapter(adapter);
          TextView txt = (TextView)findViewById(R.id.gameIDText);
          txt.setText("Choose a Game");
          
          EditText editText = (EditText)findViewById(R.id.gameID);
          editText.setVisibility(4); // makes editText invisible

        }
        
        //If the cell phone doesn't have a phone number
        //or if the using the emulator
        if( phoneNum.equals("") || phoneNum.equals("15555218135")){
        	phoneNumber.setText("");        	
        }
        else{
        	phoneNumber.setEnabled(false);
        	phoneNumber.setText(phoneNum);
        	
        }
        
        radioG = (RadioGroup)findViewById(R.id.zhType);
        
    }

    public void onClick(View v) {
        Intent i;
        String zhType = "unknown";
        String usrID = 
        	((EditText)findViewById(R.id.userID)).getText().toString();
        String phone = 
        	((EditText)findViewById(R.id.phoneNumber)).getText().toString();
        switch(v.getId()) {
        	        		
            case R.id.playButton:
            	
            	
            	if(radioG.getCheckedRadioButtonId() == -1 
            			|| phone.equals("") || usrID.equals(""))
            	{
            	  Toast.makeText(getBaseContext(),
            			  "Error: Registration Info Missing," +
            				" Please check all fields before continuing",
            					Toast.LENGTH_LONG).show();
            	}
            	else{
	            	if(radioG.getCheckedRadioButtonId() == 
	            		findViewById(R.id.zRadioB).getId()){
	            			zhType = GameEngine.ZOMBIE;
	            		
	            	}
	            	else if(radioG.getCheckedRadioButtonId() == 
	            		findViewById(R.id.hRadioB).getId()){
	            			zhType = GameEngine.HUMAN;
	            		
	            	}            	
	            	
	            	GameEngine.getInstance().myPlayer.id = usrID;
	            	GameEngine.getInstance().myPlayer.zhType = zhType;
	            	GameEngine.getInstance().myPlayer.phoneNumber = phone;
	                i = new Intent(this, ZomDroid.class);
	                           	
	                startActivity(i);
            	}
                break;
                
            //refresh the list of games in the spinner    
            case R.id.refreshButton:
                String rawGames = (new HttpLocUtil()).getGames();
                StringTokenizer tkn = new StringTokenizer(rawGames.trim(), ",");
                ArrayList<String> games = new ArrayList();
                
                while(tkn.hasMoreElements()){
              	  games.add(tkn.nextToken());
                }
            	adapter.notifyDataSetChanged();
            	break;
         
        }
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
}
