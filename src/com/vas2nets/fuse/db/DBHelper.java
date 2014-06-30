package com.vas2nets.fuse.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public static String DATABASE_NAME	= "kdiyan.dba";
	
	public static String CHAT_TABLE_NAME = "chatmessage";
	public static String CHAT_KEY_ID = "_id";
	public static String CHAT_KEY_CID = "cid";
	public static String CHAT_KEY_MESSAGEID = "messageid";
	public static String CHAT_KEY_CHATKEY = "chatkey";
	public static String CHAT_KEY_AUTHKEY = "authkey";
	public static String CHAT_KEY_CREATEDAT = "createdat";
	public static String CHAT_KEY_SENDER = "sender";
	public static String CHAT_KEY_RECEIVER = "receiver";
	public static String CHAT_KEY_CONTENTTYPE = "contenttype";
	public static String CHAT_KEY_MSGCONTENT = "msgcontent";
	public static String CHAT_KEY_RESOURCENAME = "resourcename";
	public static String CHAT_KEY_MSGSTATUS = "msgstatus";
	public static String CHAT_KEY_PICTUREBLOB = "pictureblob";
	
	
	public static String USER_TABLE_NAME = "usertable";
	public static String USER_KEY_ID = 	"_id";
	public static String USER_KEY_FIRSTNAME = "firstname";
	public static String USER_KEY_LASTNAME = "lastname";
	public static String USER_KEY_EMAIL = "email";
	public static String USER_KEY_PHOTO = "photo";
	public static String USER_KEY_PHONENUMBER = "phonenumber";
	public static String USER_KEY_AUTHKEY = "authkey";
	public static String USER_KEY_SIPID = "sipid";
	public static String USER_KEY_DEVICEID = "deviceid";
	
	
	public static String CONTACTS_TABLE_NAME = "contacttable";
	public static String CONTACTS_ID = "_id";
	public static String CONTACTS_PHONE_NUMBER = "phonenumber";
	
	private static SQLiteDatabase mDb;
	
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, 1);
		mDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_CHAT = "CREATE TABLE " + CHAT_TABLE_NAME + " ("+CHAT_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CHAT_KEY_CID+" TEXT,"+CHAT_KEY_MESSAGEID+" TEXT, "+CHAT_KEY_CHATKEY+" TEXT, "+CHAT_KEY_AUTHKEY+" TEXT, "+CHAT_KEY_CREATEDAT+" TEXT, "+CHAT_KEY_SENDER+" TEXT, "+CHAT_KEY_RECEIVER+" TEXT, "+CHAT_KEY_CONTENTTYPE+" TEXT, "+CHAT_KEY_MSGCONTENT+" TEXT, "+CHAT_KEY_RESOURCENAME+" TEXT, "+CHAT_KEY_MSGSTATUS+" TEXT, "+CHAT_KEY_PICTUREBLOB+" TEXT)";
		
		String CREATE_TABLE_USER = "CREATE TABLE " + USER_TABLE_NAME + " ("+USER_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_KEY_FIRSTNAME+" TEXT, "+USER_KEY_LASTNAME+" TEXT, "+USER_KEY_EMAIL+" TEXT, "+USER_KEY_PHOTO+" BLOB, "+USER_KEY_PHONENUMBER+" TEXT, "+USER_KEY_AUTHKEY+" TEXT, "+USER_KEY_SIPID+" TEXT, "+USER_KEY_DEVICEID+" TEXT)";
		
		String CREATE_TABLE_PHONE_CONTACTS = "CREATE TABLE " + CONTACTS_TABLE_NAME + " ("+CONTACTS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CONTACTS_PHONE_NUMBER+" TEXT)";
		
        arg0.execSQL(CREATE_TABLE_CHAT);
        arg0.execSQL(CREATE_TABLE_USER);
        arg0.execSQL(CREATE_TABLE_PHONE_CONTACTS);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL("DROP TABLE IF EXISTS "+CHAT_TABLE_NAME);
		arg0.execSQL("DROP TABLE IF EXISTS "+USER_TABLE_NAME);
		arg0.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
		
	    onCreate(arg0);
	}
	
	public static Cursor fetchActiveChat ()
    {
        //return mDb.rawQuery("select distinct " + CHAT_KEY_RECEIVER + ", " + CHAT_KEY_CREATEDAT + ", " + CHAT_KEY_MSGCONTENT  +   " from " + CHAT_TABLE_NAME, null);
		return mDb.rawQuery("select distinct receiver,_id,cid,messageid,chatkey,authkey,createdat,sender,contenttype,msgcontent,resourcename,msgstatus,pictureblob  from chatmessage", null);
    }
	
	public Cursor getActiveChat(){
		
		 String selectQuery = "SELECT _id, receiver, sender, msgcontent,createdat FROM chatmessage group by receiver order by _id desc";
		 SQLiteDatabase db = this.getWritableDatabase();
		 Cursor cursor = db.rawQuery(selectQuery, null);
		 return cursor;
		 
	}
	
	public Cursor getChatMessage(String sender, String receiver){
		String selectQuery = "SELECT * FROM CHATMESSAGE WHERE " + DBHelper.CHAT_KEY_SENDER + " = " + sender + " AND " + DBHelper.CHAT_KEY_RECEIVER + " = " + receiver;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}


}
