package com.vas2nets.fuse.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SocialDBHelper extends SQLiteOpenHelper {
	
public static String DATABASE_NAME	= "socialunique";
	
	public static String SOCIAL_TABLE_NAME = "socialtabl";
	public static String SOCIAL_KEY_ID = 	"_id";
	public static String SOCIAL_KEY_FROM = "fromsource";
	public static String SOCIAL_KEY_MESSAGE = "message";
	public static String SOCIAL_KEY_DATE = "date";
	public static String SOCIAL_KEY_PROVIDER = "provider";
	public static String SOCIAL_KEY_READ = "read";
	
	private static SQLiteDatabase mDb;

	public SocialDBHelper(Context context){
		super(context, DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
		mDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_SOCIAL = "CREATE TABLE " + SOCIAL_TABLE_NAME + " ("+SOCIAL_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+SOCIAL_KEY_FROM+" TEXT, "+SOCIAL_KEY_MESSAGE+" TEXT, "+SOCIAL_KEY_DATE+" TEXT, "+SOCIAL_KEY_PROVIDER+" TEXT, "+SOCIAL_KEY_READ+" TEXT)";
		db.execSQL(CREATE_TABLE_SOCIAL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+SOCIAL_TABLE_NAME);
		onCreate(db);
	}
	
	public static Cursor fetchActiveFeeds ()
    {
        //return mDb.rawQuery("select distinct " + CHAT_KEY_RECEIVER + ", " + CHAT_KEY_CREATEDAT + ", " + CHAT_KEY_MSGCONTENT  +   " from " + CHAT_TABLE_NAME, null);
		return mDb.rawQuery("select *  from socialtabl", null);
    }

}
