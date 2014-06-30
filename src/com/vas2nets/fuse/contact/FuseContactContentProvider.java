package com.vas2nets.fuse.contact;

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

public class FuseContactContentProvider extends ContentProvider {
	
	public static final String PROVIDER_NAME = "com.vas2nets.fuse.fusecontact";
	/** A uri to do operations on user_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/fusecontacts" );
    /** Constants to identify the requested operation */
    private static final int FUSECONTACT_ADD = 1;
    private static final int FUSECONTACT_ADD_ID = 2;
    private static final UriMatcher uriMatcher ;
    
    private static HashMap<String, String> 	fusecontactProjectionMap;
    
    private ContactDBHelper dbHelper;
    
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "fusecontacts", FUSECONTACT_ADD);
        uriMatcher.addURI(PROVIDER_NAME, "fusecontacts" + "/#", FUSECONTACT_ADD_ID);
        
        fusecontactProjectionMap = new HashMap<String, String>();
        fusecontactProjectionMap.put(ContactDBHelper.CONTACTS_ID, ContactDBHelper.CONTACTS_ID);
        fusecontactProjectionMap.put(ContactDBHelper.CONTACTS_PHONE_NUMBER, ContactDBHelper.CONTACTS_PHONE_NUMBER);
    }

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(arg0)) {
			case FUSECONTACT_ADD:
				break;
			case FUSECONTACT_ADD_ID:
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
		 case FUSECONTACT_ADD:
		 	break;
		 default:
			 throw new IllegalArgumentException("Unknown URI " + arg0);	
		 }
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues initialValues) {
		// TODO Auto-generated method stub
		if (uriMatcher.match(arg0) != FUSECONTACT_ADD) {
			throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		
		ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(ContactDBHelper.CONTACTS_TABLE_NAME, null, values);
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
		dbHelper = new ContactDBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ContactDBHelper.CONTACTS_TABLE_NAME);
        qb.setProjectionMap(fusecontactProjectionMap);
		
		switch (uriMatcher.match(arg0)) {    
			case FUSECONTACT_ADD:
				break;
			case FUSECONTACT_ADD_ID:
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
			case FUSECONTACT_ADD:
				count = db.update(ContactDBHelper.CONTACTS_TABLE_NAME, arg1, arg2, arg3);
             break;
			default:
				throw new IllegalArgumentException("Unknown URI " + arg0);
		}
		
	     getContext().getContentResolver().notifyChange(arg0, null);
	     return count;
	}

}
