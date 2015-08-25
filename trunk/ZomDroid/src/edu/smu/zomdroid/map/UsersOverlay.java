package edu.smu.zomdroid.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.OverlayItem;

import edu.smu.zomdroid.*;
import edu.smu.zomdroid.activity.ZomDroid;

/**
 * Overlay for ZomDroid users.
 */
public class UsersOverlay extends ItemizedOverlay<OverlayItem> {
    private MapActivity activity;
    private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

    public UsersOverlay(MapActivity activity, Drawable marker) {
        super(boundCenter(marker));
        this.activity = activity;
    }

    public void addOverlay(OverlayItem overlay) {
        items.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    protected boolean onTap(int i) {
        synchronized(GameEngine.getInstance()) {

        Log.d(ZomDroid.TAG, "Touch on user " + items.get(i).getTitle());
        synchronized(activity) {
            Toast.makeText(activity, items.get(i).getTitle(),
                    Toast.LENGTH_SHORT).show();
        }
        return(true);
        }
    }
}
