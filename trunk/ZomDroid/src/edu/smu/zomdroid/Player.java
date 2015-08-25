package edu.smu.zomdroid;

public class Player implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
     
    public String gameID; // The identification of the game being played
	public Double gpx; // player's latitude
	public Double gpy; // player's longitude
	public String id; 
	public boolean isActive=true; // Is the player still actively playing?
	public String phoneNumber; 
	public int score; 
	public String zhType; // Player's status {human, zombie, infected}
	
	public Player(){
		gameID="";
		gpx=0.0;
		gpy=0.0;
		id="";
		phoneNumber="";
		score=0;
		zhType="unknown";
	}
	
	/**
	 * Returns a string containing all of the player's information.
	 * @return
	 */
	public String getStats()
	{
		return("id= " + id + " zhType= " + zhType +
				" gpx= " + gpx + " gpy= " + gpy + "score= " + score +
				" phone# = " + phoneNumber);
	}
	
	

}
