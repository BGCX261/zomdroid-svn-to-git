package edu.smu.zomdroid.database;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.database.Cursor;
import edu.smu.zomdroid.Player;
/**
* @author Hieu Tran
*
* This class is responsible for inserting and access record of zomdroid database
*
*/
public class ZomdroidState {

    private EventsData events;

    public void addPlayer(ArrayList<Player> players, Activity ctx) {
        try {
            events = new EventsData(ctx);
            events.deleteRecords();
            Iterator<Player> iterator = players.iterator();
            Player player;
            while(iterator.hasNext()) {
                player = (Player)iterator.next();
                events.addRecord(player.id, player.zhType, player.gpx
                        .toString(), player.gpy.toString(), player.score,
                        player.isActive);
            }
        }
        finally {
            events.close();
        }
    }

    public Cursor GetPlayers(Activity ctx) {
        Cursor cursor = null;
        events = new EventsData(ctx);
        try {
            cursor = events.getRecords(ctx);
        }
        finally {
            events.close();
        }
        return cursor;
    }

}
