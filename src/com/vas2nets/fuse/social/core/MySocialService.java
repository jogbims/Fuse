package com.vas2nets.fuse.social.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.db.SocialDBHelper;

public class MySocialService extends Service {
	
	private Timer timer = new Timer();
	final Handler handler = new Handler();
	private static final long UPDATE_INTERNAL = 50000;
	private final IBinder mBinder = new MyBinder();
	
	private SocialDBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues content;
	
	SocialAuthAdapter s_adapter;
	List<Feed> feedList;
	
	String[] projection = new String[] {
			SocialDBHelper.SOCIAL_KEY_ID,
            SocialDBHelper.SOCIAL_KEY_MESSAGE,
            SocialDBHelper.SOCIAL_KEY_DATE,
            SocialDBHelper.SOCIAL_KEY_PROVIDER,
            SocialDBHelper.SOCIAL_KEY_READ
    };
	
	public class MyBinder extends Binder{
		public MySocialService getService(){
			return MySocialService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public void onCreate(){
		super.onCreate();
		
		mHelper = new SocialDBHelper(getApplicationContext());
		socialAuthInitializer();
		/*
		TimerTask doAsynchronousTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
    		
    	};
    	timer.schedule(doAsynchronousTask, 0, UPDATE_INTERNAL);
    	*/
	}
	
	 public void socialAuthInitializer(){
	    	s_adapter = new SocialAuthAdapter(new ResponseListener());
			s_adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);
			s_adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
			s_adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
			//s_adapter.authorize(getApplicationContext(), Provider.INSTAGRAM);
			
			feedList = new ArrayList<Feed>();
			content = new ContentValues();
	    }
	 
	    private final class ResponseListener implements DialogListener {

			@Override
			public void onBack() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete(Bundle arg0) {
				// TODO Auto-generated method stub
				TimerTask doAsynchronousTask = new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								s_adapter.getFeedsAsync(new FeedDataListener()); 
							}
							
						});
						
					}
					
				};
				timer.schedule(doAsynchronousTask, 0, UPDATE_INTERNAL);
				
			}

			@Override
			public void onError(SocialAuthError arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}
	    
	    
	    private final class FeedDataListener implements SocialAuthListener<List<Feed>> {

			@Override
			public void onError(SocialAuthError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onExecute(String arg0, List<Feed> arg1) {
				// TODO Auto-generated method stub
				 feedList = arg1;
				 String providerName;
				 String from, message, date, read;
				 
				 
				 for (Feed f : arg1){
					 providerName = s_adapter.getCurrentProvider().getAccessGrant().getProviderId();
					 from = f.getFrom();
					 message = f.getMessage();
					 date = f.getCreatedAt().toString();
					 read = "no";
					 
					 //content.put(SocialDBHelper.SOCIAL_KEY_FROM, from);
					 content.put(SocialDBHelper.SOCIAL_KEY_MESSAGE, message);
					 content.put(SocialDBHelper.SOCIAL_KEY_DATE, date);
					 content.put(SocialDBHelper.SOCIAL_KEY_PROVIDER, providerName);
					 content.put(SocialDBHelper.SOCIAL_KEY_READ, read);
					 System.out.println("samson : "+ message);
					 getContentResolver().insert(SocialContentProvider.CONTENT_URI, content);
				 }
				
					//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
				dataBase.close();
				// new InsertIntoDB().execute();
				
			}
			
		}
	    
	    
	    class InsertIntoDB extends AsyncTask<String, String, String> {

			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
				
				return null;
			}

	    }
	    
	    
	
	@Override
	  public void onDestroy() {
	    super.onDestroy();
	    //Log.i(TAG, "Service destroying");
	     
	    timer.cancel();
	    timer = null;
	  }
	
}
