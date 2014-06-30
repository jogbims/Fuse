package com.vas2nets.fuse.sip.core;


import come.vas2nets.fuse.test.MyFuseService.MyBinder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class SipIntentService extends Service {
	
	public String sipAddress = null;

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public SipIncomingCallReceiver callReceiver;

    private static final int CALL_ADDRESS = 1;
    private static final int SET_AUTH_INFO = 2;
    private static final int UPDATE_SETTINGS_DIALOG = 3;
    private static final int HANG_UP = 4;
    
    private String sipId;
    private String domain = "197.253.10.27";
    private String password = "fuse";
    
    private final IBinder mBinder = new MyBinder();
    
    public class MyBinder extends Binder{
		public SipIntentService getService(){
			return SipIntentService.this;
		}
	}



	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public void onCreate(){
		super.onCreate();
		
		SharedPreferences pref3 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	sipId = pref3.getString("FuseSipID", null);
        /*
    	try{
    		 Intent intent = new Intent();
    	     intent.setAction("android.SipDemo.INCOMING_CALL");
    	     PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, Intent.FILL_IN_DATA);
    	     manager.open(me, pendingIntent, null);
    			
    	}catch(Exception e){
    		
    	}
       */
        
        initializeManager();
		
	}
	
	
	
	public void initializeManager() {
        if(manager == null) {
          manager = SipManager.newInstance(this);
        }

        initializeLocalProfile();
    }
	
	
	public void initializeLocalProfile() {
		 if (manager == null) {
	            return;
	        }

	        if (me != null) {
	            closeLocalProfile();
	        }
	        
	        if (sipId.length() == 0 || domain.length() == 0 || password.length() == 0) {
	            //showDialog(UPDATE_SETTINGS_DIALOG);
	            return;
	        }
	        
	        try{
	        	SipProfile.Builder builder = new SipProfile.Builder(sipId, domain);
	        	builder.setPassword(password);
	            me = builder.build();
	            
	            Intent i = new Intent();
	            i.setAction("android.SipDemo.INCOMING_CALL");
	            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
	            manager.open(me, pi, null);
	            
	            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {

					@Override
					public void onRegistering(String arg0) {
						// TODO Auto-generated method stub
						//updateStatus("Registering with SIP Server...");
					}

					@Override
					public void onRegistrationDone(String arg0, long arg1) {
						// TODO Auto-generated method stub
						//updateStatus("Ready");
					}

					@Override
					public void onRegistrationFailed(String arg0, int arg1,
							String arg2) {
						// TODO Auto-generated method stub
						//updateStatus("Registration failed.  Please check settings.");
					}
	            	
	            });

	            
	        }catch(Exception e){
	        	
	        }


	}
	
	
	public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
        }
    }
	
	public  void initiateCall() {
		
		// get receiver sip address
		try{
			 SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				 @TargetApi(Build.VERSION_CODES.GINGERBREAD)
					@SuppressLint("NewApi")
					// happen via listeners.  Even making an outgoing call, don't
	                // forget to set up a listener to set things up once the call is established.
	                @Override
	                public void onCallEstablished(SipAudioCall call) {
	                    call.startAudio();
	                    call.setSpeakerMode(true);
	                    call.toggleMute();
	                    //updateStatus(call);
	                }

	                @TargetApi(Build.VERSION_CODES.GINGERBREAD)
					@Override
	                public void onCallEnded(SipAudioCall call) {
	                    //updateStatus("Ready.");
	                }
			 };
			 
			 call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);
			 
		}catch(Exception e){
			 Log.i("WalkieTalkieActivity/InitiateCall", "Error when trying to close manager.", e);
	            if (me != null) {
	                try {
	                    manager.close(me.getUriString());
	                } catch (Exception ee) {
	                    Log.i("WalkieTalkieActivity/InitiateCall",
	                            "Error when trying to close manager.", ee);
	                    ee.printStackTrace();
	                }
	            }
	            if (call != null) {
	                call.close();
	            }
	        }
		}
	
	
	@Override
	  public void onDestroy() {
	    super.onDestroy();
	    
	    
	}

}
