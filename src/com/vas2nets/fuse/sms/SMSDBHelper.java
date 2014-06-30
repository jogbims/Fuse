package com.vas2nets.fuse.sms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SMSDBHelper extends SQLiteOpenHelper {
	
	public static String DATABASE_NAME	= "sms.dba";
	
	public static String SMS_TABLE_NAME = "smstable";
	public static String SMS_KEY_ID = 	"_id";
	public static String SMS_KEY_MESSAGE = "message";
	public static String SMS_KEY_RECEIVER = "receiver";
	public static String SMS_KEY_TIME = "time";
	public static String SMS_KEY_STATUS = "status";
	

	public SMSDBHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_SMS = "CREATE TABLE " + SMS_TABLE_NAME + " ("+SMS_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+SMS_KEY_MESSAGE+" TEXT, "+SMS_KEY_TIME+" TEXT, "+SMS_KEY_STATUS+" TEXT, "+SMS_KEY_RECEIVER+" TEXT)";
		db.execSQL(CREATE_TABLE_SMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+SMS_TABLE_NAME);
	}

}
