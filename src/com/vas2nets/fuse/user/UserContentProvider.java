package com.vas2nets.fuse.user;

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

public class UserContentProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.vas2nets.fuse.user";
	/** A uri to do operations on user_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/users" );
    /** Constants to identify the requested operation */
    private static final int USER_ADD = 1;
    private static final int USER_ADD_ID = 2;
    private static final UriMatcher uriMatcher ;
    
    private static HashMap<String, String> 	userProjectionMap;
    
    private DBHelper dbHelper;
    
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "users", USER_ADD);
        uriMatcher.addURI(PROVIDER_NAME, "users" + "/#", USER_ADD_ID);
        
        userProjectionMap = new HashMap<String, String>();
        userProjectionMap.put(DBHelper.USER_KEY_ID, DBHelper.USER_KEY_ID);
        userProjectionMap.put(DBHelper.USER_KEY_FIRSTNAME, DBHelper.USER_KEY_FIRSTNAME);
        userProjectionMap.put(DBHelper.USER_KEY_LASTNAME, DBHelper.USER_KEY_LASTNAME);
        userProjectionMap.put(DBHelper.USER_KEY_EMAIL, DBHelper.USER_KEY_EMAIL);
        userProjectionMap.put(DBHelper.USER_KEY_PHONENUMBER, DBHelper.USER_KEY_PHONENUMBER);
        userProjectionMap.put(DBHelper.USER_KEY_PHOTO, DBHelper.USER_KEY_PHOTO);
        userProjectionMap.put(DBHelper.USER_KEY_SIPID, DBHelper.USER_KEY_SIPID);
        userProjectionMap.put(DBHelper.USER_KEY_AUTHKEY, DBHelper.USER_KEY_AUTHKEY);
        userProjectionMap.put(DBHelper.USER_KEY_DEVICEID, DBHelper.USER_KEY_DEVICEID);
    }
    
    

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(arg0)) {
			case USER_ADD:
				break;
			case USER_ADD_ID:
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
		 case USER_ADD:
		 	break;
		 default:
			 throw new IllegalArgumentException("Unknown URI " + arg0);	
		 }
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (uriMatcher.match(arg0) != USER_ADD) {
			throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		
		ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DBHelper.USER_TABLE_NAME, null, values);
        if (rowId > 0) {
        	 Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        	 getContext().getContentResolver().notifyChange(noteUri, null);
             return noteUri;
        }
		
        throw new SQLException("Failed to insert row into " + arg0);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DBHelper.USER_TABLE_NAME);
        qb.setProjectionMap(userProjectionMap);
		
		switch (uriMatcher.match(arg0)) {    
			case USER_ADD:
				break;
			case USER_ADD_ID:
				arg2 = arg2 + "_id = " + arg0.getLastPathSegment();
                break;
			default:
				throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, arg1, arg2, arg3, null, null, arg4);
 
        c.setNotificationUri(getContext().getContentResolver(), arg0);
        return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
	     int count;
	     switch (uriMatcher.match(arg0)) {    
			case USER_ADD:
				count = db.update(DBHelper.USER_TABLE_NAME, arg1, arg2, arg3);
              break;
			default:
				throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		
	     getContext().getContentResolver().notifyChange(arg0, null);
	     return count;
	}

}
