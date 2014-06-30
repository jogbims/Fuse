package com.vas2nets.fuse.db;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ActiveSocialDB extends SQLiteOpenHelper {
	
	public static String DATABASE_NAME	= "selectedproviders.dba";
	
	public static String PROVIDER_TABLE_NAME = "providertable";
	public static String PROVIDER_ID = "_id";
	public static String PROVIDER_NAME = "providername";
	
	

	public ActiveSocialDB(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_PROVIDER = "CREATE TABLE " + PROVIDER_TABLE_NAME + " ("+PROVIDER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+PROVIDER_NAME+" TEXT)";
		
		db.execSQL(CREATE_TABLE_PROVIDER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS "+PROVIDER_TABLE_NAME);
		
	    onCreate(arg0);
	}
	
	public void addProvider(String provider){
		 SQLiteDatabase db = this.getWritableDatabase();
		 
		    ContentValues values = new ContentValues();
		    values.put(PROVIDER_NAME, provider); // Contact Name
		 
		    // Inserting Row
		    db.insert(PROVIDER_TABLE_NAME, null, values);
		    db.close(); // Closing database connection
	}
	
	public List<String> getAllProviders() {
	    List<String> providerList = new ArrayList<String>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + PROVIDER_TABLE_NAME;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do{
	            // Adding contact to list
	            providerList.add(cursor.getString(1));
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return providerList;
	}
	
	public int getProvidersCount() {
        String countQuery = "SELECT  * FROM " + PROVIDER_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

}
