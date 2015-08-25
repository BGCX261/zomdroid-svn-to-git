package edu.smu.zomdroid.database;
import static edu.smu.zomdroid.database.Constants.TABLE_NAME;
import static edu.smu.zomdroid.database.Constants.USERID;
import static edu.smu.zomdroid.database.Constants.ZHTYPE;
import static edu.smu.zomdroid.database.Constants.GPX;
import static edu.smu.zomdroid.database.Constants.GPY;
import static edu.smu.zomdroid.database.Constants.SCORE;
import static edu.smu.zomdroid.database.Constants.ACTIVE;
import static edu.smu.zomdroid.database.Constants.FROM;
import static edu.smu.zomdroid.database.Constants.ORDER_BY;
import static android.provider.BaseColumns._ID;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
* @author Hieu Tran
*
* This class is zomdroid event database class
* which is used to create database, table,
* and provide the database insert and accessing functions.
*
*/
public class EventsData extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "zomdroid.db";
   private static final int DATABASE_VERSION = 3;
   /** Create a helper object for the Events database */
   public EventsData(Context ctx) {	  
      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," 
            + USERID + " TEXT NOT NULL,"
            + ZHTYPE + " TEXT NOT NULL,"
            + GPX + " TEXT,"
            + GPY + " TEXT,"
            + SCORE + " INTEGER,"
            + ACTIVE + " TEXT);");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion,
         int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
   }
   public void deleteRecords()
   {
	   SQLiteDatabase db = getWritableDatabase();
	   db.delete(TABLE_NAME, null, null);
   }
   
   public void addRecord(String userID, String zhType, 
		   String gpx, String gpy, 
		   int score, boolean active) {
	      // Insert a new record into the Events data source.
	      // You would do something similar for delete and update.
	      SQLiteDatabase db = getWritableDatabase();
	      ContentValues values = new ContentValues();
	      values.put(USERID, userID);
	      values.put(ZHTYPE, zhType);	      
	      values.put(GPX, gpx);
	      values.put(GPY, gpy);
	      values.put(SCORE, score);
	      values.put(ACTIVE, (active?"true":"false"));
	      db.delete(TABLE_NAME, null, null);
	      db.insertOrThrow(TABLE_NAME, null, values);
	      
	   }

	public Cursor getRecords(Activity ctx) {
	      // Perform a managed query. The Activity will handle closing
	      // and re-querying the cursor when needed.
	      SQLiteDatabase db = getReadableDatabase();
	      Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
	            null, ORDER_BY);
	      ctx.startManagingCursor(cursor);
	      return cursor;
	   }

}

