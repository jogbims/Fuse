package com.vas2nets.fuse.contact;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {
	
	public static String DATABASE_NAME	= "contactfuse.dba";
	
	public static String CONTACTS_TABLE_NAME = "contacttable";
	public static String CONTACTS_ID = "_id";
	public static String CONTACTS_PHONE_NUMBER = "phonenumber";
	
	private static SQLiteDatabase mDb;

	public ContactDBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
		mDb = getWritableDatabase();
		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_PHONE_CONTACTS = "CREATE TABLE " + CONTACTS_TABLE_NAME + " ("+CONTACTS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CONTACTS_PHONE_NUMBER+" TEXT)";
		
        
        arg0.execSQL(CREATE_TABLE_PHONE_CONTACTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
		
	    onCreate(arg0);
	}
	
	

}
