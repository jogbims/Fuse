package com.vas2nets.fuse.sms;

import java.util.HashMap;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SMSContentProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.vas2nets.fuse.sms";
	/** A uri to do operations on cust_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/smss" );
    /** Constants to identify the requested operation */
    private static final int SMS_MESSAGE = 1;
    private static final int SMS_MESSAGE_ID = 2;
    private static final UriMatcher uriMatcher ;
    
    private static HashMap<String, String> 	smsProjectionMap;
    private SMSDBHelper dbHelper;
    
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "smss", SMS_MESSAGE);
        uriMatcher.addURI(PROVIDER_NAME, "smss" + "/#", SMS_MESSAGE_ID);
        
        smsProjectionMap = new HashMap<String, String>();
        smsProjectionMap.put(SMSDBHelper.SMS_KEY_ID, SMSDBHelper.SMS_KEY_ID);
        smsProjectionMap.put(SMSDBHelper.SMS_KEY_MESSAGE, SMSDBHelper.SMS_KEY_MESSAGE);
        smsProjectionMap.put(SMSDBHelper.SMS_KEY_STATUS, SMSDBHelper.SMS_KEY_STATUS);
        smsProjectionMap.put(SMSDBHelper.SMS_KEY_TIME, SMSDBHelper.SMS_KEY_TIME);
        smsProjectionMap.put(SMSDBHelper.SMS_KEY_RECEIVER, SMSDBHelper.SMS_KEY_RECEIVER);
        
    }

    @Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(arg0)) {
		case SMS_MESSAGE:
			break;
		case SMS_MESSAGE_ID:
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
		 case SMS_MESSAGE:
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
				if (uriMatcher.match(uri) != SMS_MESSAGE) {
					throw new IllegalArgumentException("Unknown URI " + uri);
				}
				
				ContentValues values;
		        if (initialValues != null) {
		            values = new ContentValues(initialValues);
		        } else {
		            values = new ContentValues();
		        }
		        
		        SQLiteDatabase db = dbHelper.getWritableDatabase();
		        long rowId = db.insert(SMSDBHelper.SMS_TABLE_NAME, null, values);
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
		dbHelper = new SMSDBHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SMSDBHelper.SMS_TABLE_NAME);
        qb.setProjectionMap(smsProjectionMap);
		
		switch (uriMatcher.match(uri)) {    
			case SMS_MESSAGE:
				break;
			case SMS_MESSAGE_ID:
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
			case SMS_MESSAGE:
				count = db.update(SMSDBHelper.SMS_TABLE_NAME, values, selection, selectionArgs);
              break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
	     getContext().getContentResolver().notifyChange(uri, null);
	     return count;
	}


}
