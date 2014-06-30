package com.vas2nets.fuse;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.signup.RegisterPhoneNumberActivity;
import com.vas2nets.fuse.util.Constants;

public class SplashActivity extends Activity {
	
	//private static int SPLASH_TIME_OUT = 3000;
	private String phoneNumber;
	private String redirect;
	private String loggedIn;
	
	 public String username = "psalmsin";
	 public String domain = "sip.linphone.org";
	 public String password = "password";
	 
	 public SipManager mSipManager = null;
	 public SipProfile mSipProfile = null;
	 public static final String TAG = "SIPInfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// initializeManager();
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
		    	redirect = pref.getString("GotPin", null);
		    	
		    	SharedPreferences pref1 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
		    	loggedIn = pref1.getString("Loggedin", null);
		    	
		    	/* are we logged in or not?
		    	 * 
		    	 */
		    	if (loggedIn == null){
		    		Intent intent = new Intent(SplashActivity.this, RegisterPhoneNumberActivity.class);
		    		startActivity(intent);
		    		//finish();
		    	}else{
		    		Intent im = new Intent(SplashActivity.this, MainActivity.class);
		    		startActivity(im);
		    		//finish();
		    	}
		    	
		    	// close this activity
		        finish();
			}
			
		 }, Constants.SPLASH_TIME_OUT);
	}
	
	 @Override
	    public void onStart() {
	        super.onStart();
	        // When we get back from the preference setting Activity, assume
	        // settings have changed, and re-login with new auth info.
	       // initializeManager();
	    }
	 
	 public void initializeManager(){
		 if(mSipManager == null) {
			    mSipManager = SipManager.newInstance(this);
			}
			
			if (mSipProfile != null) {
	            closeLocalProfile();
	        }

			
			try{
				SipProfile.Builder builder = new SipProfile.Builder(username, domain);
				builder.setPassword(password);
				mSipProfile = builder.build();
				
				
				Intent intent = new Intent();
				intent.setAction("android.SipDemo.INCOMING_CALL");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
				mSipManager.open(mSipProfile, pendingIntent, null);
				
				mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {

					public void onRegistering(String localProfileUri) {
						Log.i(TAG, "Registering with SIP Server...");
					}

					public void onRegistrationDone(String localProfileUri, long expiryTime) {
						Log.i(TAG, "Ready");
					}
					   
					public void onRegistrationFailed(String localProfileUri, int errorCode,
					    String errorMessage) {
						Log.i(TAG, "Registration failed.  Please check settings.");
					}
				});
			}catch(Exception e){
				
			}
			
	 }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	
	public void closeLocalProfile() {
	    if (mSipManager == null) {
	       return;
	    }
	    try {
	       if (mSipProfile != null) {
	          mSipManager.close(mSipProfile.getUriString());
	       }
	     } catch (Exception ee) {
	       Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
	     }
	}
	
	

}
