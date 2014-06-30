package com.vas2nets.fuse.sip.chat;

import java.util.HashMap;

import com.vas2nets.fuse.db.DBHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ChatContentProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.vas2nets.fuse.chat";
	/** A uri to do operations on cust_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/chats" );
    /** Constants to identify the requested operation */
    private static final int CHAT_MESSAGE = 1;
    private static final int CHAT_MESSAGE_ID = 2;
    private static final UriMatcher uriMatcher ;
    
    private static HashMap<String, String> 	chatProjectionMap;
    private DBHelper dbHelper;
    
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "chats", CHAT_MESSAGE);
        uriMatcher.addURI(PROVIDER_NAME, "chats" + "/#", CHAT_MESSAGE_ID);
        
        chatProjectionMap = new HashMap<String, String>();
        chatProjectionMap.put(DBHelper.CHAT_KEY_ID, DBHelper.CHAT_KEY_ID);
        chatProjectionMap.put(DBHelper.CHAT_KEY_CID, DBHelper.CHAT_KEY_CID);
        chatProjectionMap.put(DBHelper.CHAT_KEY_MESSAGEID, DBHelper.CHAT_KEY_MESSAGEID);
        chatProjectionMap.put(DBHelper.CHAT_KEY_CHATKEY, DBHelper.CHAT_KEY_CHATKEY);
        chatProjectionMap.put(DBHelper.CHAT_KEY_AUTHKEY, DBHelper.CHAT_KEY_AUTHKEY);
        chatProjectionMap.put(DBHelper.CHAT_KEY_CREATEDAT, DBHelper.CHAT_KEY_CREATEDAT);
        chatProjectionMap.put(DBHelper.CHAT_KEY_SENDER, DBHelper.CHAT_KEY_SENDER);
        chatProjectionMap.put(DBHelper.CHAT_KEY_RECEIVER, DBHelper.CHAT_KEY_RECEIVER);
        chatProjectionMap.put(DBHelper.CHAT_KEY_CONTENTTYPE, DBHelper.CHAT_KEY_CONTENTTYPE);
        chatProjectionMap.put(DBHelper.CHAT_KEY_MSGCONTENT, DBHelper.CHAT_KEY_MSGCONTENT);
        chatProjectionMap.put(DBHelper.CHAT_KEY_RESOURCENAME, DBHelper.CHAT_KEY_RESOURCENAME);
        chatProjectionMap.put(DBHelper.CHAT_KEY_MSGSTATUS, DBHelper.CHAT_KEY_MSGSTATUS);
        chatProjectionMap.put(DBHelper.CHAT_KEY_PICTUREBLOB, DBHelper.CHAT_KEY_PICTUREBLOB);
        
    }
	
   

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(arg0)) {
		case CHAT_MESSAGE:
			break;
		case CHAT_MESSAGE_ID:
            arg1 = arg1 + "_id = " + arg0.getLastPathSegment();
            break;
		default:
			throw new IllegalArgumentException("Unknown URI " + arg0);
	}
	return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		 switch (uriMatcher.match(arg0)) {
		 case CHAT_MESSAGE:
		 	break;
		 default:
			 throw new IllegalArgumentException("Unknown URI " + arg0);	
		 }
		return null;	
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (uriMatcher.match(uri) != CHAT_MESSAGE) {
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
				
				ContentValues values;
		        if (initialValues != null) {
		            values = new ContentValues(initialValues);
		        } else {
		            values = new ContentValues();
		        }
		        
		        SQLiteDatabase db = dbHelper.getWritableDatabase();
		        long rowId = db.insert(DBHelper.CHAT_TABLE_NAME, null, values);
		        if (rowId > 0) {
		        	 Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
		        	 getContext().getContentResolver().notifyChange(noteUri, null);
		             return noteUri;
		        }
				
		        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DBHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DBHelper.CHAT_TABLE_NAME);
        qb.setProjectionMap(chatProjectionMap);
		
		switch (uriMatcher.match(uri)) {    
			case CHAT_MESSAGE:
				break;
			case CHAT_MESSAGE_ID:
				selection = selection + "_id = " + uri.getLastPathSegment();
                break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
 
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs){
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	     int count;
	     switch (uriMatcher.match(uri)) {    
			case CHAT_MESSAGE:
				count = db.update(DBHelper.CHAT_TABLE_NAME, values, selection, selectionArgs);
              break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
	     getContext().getContentResolver().notifyChange(uri, null);
	     return count;
	}

}
