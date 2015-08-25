package edu.smu.zomdroid.database;

import android.provider.BaseColumns;
/**
* @author Hieu Tran
*
* This class is provide the constant variable of database
* such as table name, column names
*
*/
public interface Constants extends BaseColumns {
   public static final String TABLE_NAME = "zomdroid";

   // Columns in the Events database
   public static final String USERID = "USERID";
   public static final String ZHTYPE = "ZHTYPE";
   public static final String GPX = "GPX";
   public static final String GPY = "GPY";
   public static final String SCORE = "SCORE";
   public static final String ACTIVE = "ACTIVE";
   public static String[] FROM = { USERID, ZHTYPE, GPX, GPY, SCORE };
   public static String ORDER_BY = USERID;
   
}