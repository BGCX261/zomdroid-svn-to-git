package edu.smu.zomdroid;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.location.Location;
import android.util.Log;
import edu.smu.zomdroid.activity.ZomDroid;

public class GameEngine {

    private static GameEngine singleton = null;
    public ArrayList<Player> players = null;

    public static final String MY_PLAYER = "myPlayer";
    public Player myPlayer;
    public String lastLocTimeStamp;
    public static final int radius = 10;

    private int INFECTED_SURVIVAL_POINT_VALUE = HUMAN_SURVIVAL_POINT_VALUE;
    private static final int ZOMBIE_CONVERTION_POINT_VALUE = 50;
    private static final int HUMAN_SURVIVAL_POINT_VALUE = 5;
    private static final int HUMAN_SCORE_UPDATE_RATE = 30000;
    private static final int TIME_TILL_ZOMBIE = 300000;

    public static final String ZOMBIE = "zombie";
    public static final String HUMAN = "human";
    public static final String INFECTED = "infected";
    public static final String MEDIC = "medic";
    public static final String HOSPITAL = "hospital";

    private Timer infectedCountdown;
    private Timer scoreTimer;
    private ScoreTask scoreTask;
    private InfectedTask infectedTask;

    // TODO: These two variables will need to be saved in state
    // and reread onResume()
    public static final String ELAPSED_TIME = "elapsedTime";
    public static final int ELAPSED_TIME_DEF = 0;
    public static final String INFECTED_BY = "infectedBy";
    public static final String INFECTED_BY_DEF = "";
    public int elapsedTime;
    public String infectedBy;

    public static boolean stateChanged = false;
    public static String notifications = "";

    public boolean playersLoaded = false;

    private GameEngine() {
        players = new ArrayList<Player>();
        myPlayer = new Player();

        infectedCountdown = new Timer();
        infectedTask = new InfectedTask();
        infectedCountdown.schedule(infectedTask, 0, 1000);

        scoreTimer = new Timer();
        scoreTask = new ScoreTask();
        scoreTimer.schedule(scoreTask, 0, HUMAN_SCORE_UPDATE_RATE);
    }

    /**
     * Implementation for singleton pattern
     * 
     * @return GameEngine singleton
     */
    public static synchronized GameEngine getInstance() {
        if(singleton == null) {
            singleton = new GameEngine();
        }
        return singleton;
    }

    /**
     * Parses doc for user information and updates internal user array. Calls
     * VicinityMap.updateUsers().
     * 
     * @param doc
     *            Valid XML document from server
     */
    public void update(Document doc) {
        // Parse the doc and update players
        // For each player, find out if this changes
        NodeList nodeList = doc.getElementsByTagName("user");
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE) {

                if(players.size() == i)
                    players.add(new Player());
                NamedNodeMap atts = node.getAttributes();

                if(atts.getNamedItem("id").getNodeValue() == myPlayer.id)
                    continue;
                Log.d(ZomDroid.TAG, "usrID="
                        + atts.getNamedItem("id").getNodeValue() + ", gpx ="
                        + atts.getNamedItem("gpx").getNodeValue() + ", gpy = "
                        + atts.getNamedItem("gpy").getNodeValue()
                        + ", score = "
                        + atts.getNamedItem("score").getNodeValue()
                        + ", zhType = "
                        + atts.getNamedItem("zhtype").getNodeValue());
                try {
                    players.get(i).id = atts.getNamedItem("id").getNodeValue();
                    players.get(i).gpx = Double.parseDouble(atts.getNamedItem(
                            "gpx").getNodeValue());
                    players.get(i).gpy = Double.parseDouble(atts.getNamedItem(
                            "gpy").getNodeValue());
                    players.get(i).zhType = atts.getNamedItem("zhtype")
                            .getNodeValue();
                    players.get(i).score = Integer.parseInt(atts.getNamedItem(
                            "score").getNodeValue());
                }
                catch(NumberFormatException e) {
                    Log.e(ZomDroid.TAG, e.getMessage());
                }

                if(playersLoaded) {

                    if(!myPlayer.zhType.equals(players.get(i).zhType)) {
                        // two people with opposing classes
                        boolean close = proximityDetection(players.get(i).gpx,
                                players.get(i).gpy);

                        checkResults(players.get(i).zhType, close);
                    }
                }
            }
        }
        playersLoaded = true;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void updatePlayerInfo(Document doc) {

    }

    public boolean proximityDetection(double endLat, double endLong) {

        float result[] = new float[5];

        Location.distanceBetween(myPlayer.gpx, myPlayer.gpy, endLat, endLong,
                result);

        if(result[0] < radius)
            return true;

        return false;
    }

    /**
     * This function examines the user close to us, and makes the appropriate
     * changes.
     * 
     * @author Corey Cothrum
     * @param close
     *            boolean flag stating if another use is in our proximity
     * @param otherType
     *            zhType from other user in our proximity
     */
    private void checkResults(String otherType, boolean close) {

        if(!close) { // no interaction with other users
            if(myPlayer.zhType.equals(HUMAN)) {
                // survived
            }
            else
                if(myPlayer.zhType.equals(INFECTED)) {
                    // still infected
                }
                else
                    if(myPlayer.zhType.equals(MEDIC)) {
                        // didn't heal anything
                    }
                    else
                        if(myPlayer.zhType.equals(ZOMBIE)) {
                            // didn't infect anything
                        }
        }
        else { // interaction with opposing type
            if(myPlayer.zhType.equals(HUMAN)) {
                // / YOU ARE HUMAN ///
                if(otherType.equals(HUMAN)) {
                    // survived
                }
                else
                    if(otherType.equals(ZOMBIE)) {
                        myPlayer.zhType = INFECTED;
                        infectedBy = ZOMBIE;
                        // VicinityMap.showInfectedAlert();
                    }
                    else
                        if(otherType.equals(INFECTED)) {
                            myPlayer.zhType = INFECTED;
                            infectedBy = INFECTED;
                            // VicinityMap.showInfectedAlert();
                        }
                        else
                            if(otherType.equals(MEDIC)) {
                                // survived
                            }
            }
            else
                if(myPlayer.zhType.equals(INFECTED)) {
                    // / YOU ARE INFECTED ///
                    if(otherType.equals(HUMAN)) {
                    }
                    else
                        if(otherType.equals(ZOMBIE)) {
                        }
                        else
                            if(otherType.equals(INFECTED)) {
                            }
                            else
                                if(otherType.equals(MEDIC)) {
                                    myPlayer.zhType = HUMAN;
                                }
                }
                else
                    if(myPlayer.zhType.equals(MEDIC)) {
                        // / YOU ARE A MEDIC ///
                        if(otherType.equals(HUMAN)) {
                            // nothing
                        }
                        else
                            if(otherType.equals(ZOMBIE)) {
                                // nothing
                            }
                            else
                                if(otherType.equals(INFECTED)) {
                                    // healed someone
                                }
                                else
                                    if(otherType.equals(MEDIC)) {
                                        // nothing
                                    }
                    }
                    else
                        if(myPlayer.zhType.equals(ZOMBIE)) {
                            // / YOU ARE A ZOMBIE ///
                            if(otherType.equals(HUMAN)) {
                                myPlayer.score += ZOMBIE_CONVERTION_POINT_VALUE;
                            }
                            else
                                if(otherType.equals(ZOMBIE)) {
                                    // nothing
                                }
                                else
                                    if(otherType.equals(INFECTED)) {
                                        // nothing
                                    }
                                    else
                                        if(otherType.equals(MEDIC)) {
                                            // nothing
                                        }
                        }
        }
    }

    class ScoreTask extends TimerTask {
        @Override
        public void run() {
            if(myPlayer.zhType.equals(HUMAN)) {
                myPlayer.score += HUMAN_SURVIVAL_POINT_VALUE;
            }
            else
                if(myPlayer.zhType.equals(INFECTED)) {
                    myPlayer.score += INFECTED_SURVIVAL_POINT_VALUE;
                }
        }
    }

    class InfectedTask extends TimerTask {
        double ratio;

        public void run() {
            if(!myPlayer.zhType.equals(INFECTED)) {
                elapsedTime = 0;
            }

            ratio = ((double)TIME_TILL_ZOMBIE - (double)elapsedTime)
                    / (double)TIME_TILL_ZOMBIE;
            INFECTED_SURVIVAL_POINT_VALUE = (int)((double)HUMAN_SURVIVAL_POINT_VALUE * ratio);

            if(infectedBy.equals(ZOMBIE))
                elapsedTime += 1000;
            else
                if(infectedBy.equals(INFECTED))
                    elapsedTime += 500;

            if(elapsedTime >= TIME_TILL_ZOMBIE) {
                // times up!
                // VicinityMap.showZombieAlert();
                myPlayer.zhType = ZOMBIE;
                infectedCountdown.cancel();
                scoreTimer.cancel();
            }
        }
    }
}
