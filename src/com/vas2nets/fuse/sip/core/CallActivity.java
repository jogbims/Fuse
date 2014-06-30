package com.vas2nets.fuse.sip.core;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class CallActivity extends Activity {
	
    private TextView tv;
    private TextView tv_name;
    private ImageView im;
    
    public SipManager mSipManager;
	public SipProfile mSipProfile;
	public static final String TAG = "SIPCall";
	public SipAudioCall call = null;
	public String callTo = "gbolaga@sip.linphone.org";
	public SipIncomingCallReceiver callReceiver;
    
    public String username = "psalmsin";
    public String domain = "sip.linphone.org";
    public String password = "password";
    
    //public String username = "1524";
    //public String domain = "197.253.10.27";
    //public String password = "password";
    
    public String name;
    public String number;
    public String photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		
		tv = (TextView) findViewById(R.id.output1);
		tv_name = (TextView) findViewById(R.id.textView4);
		im = (ImageView) findViewById(R.id.imageView5);
		
		    
		    Intent i = getIntent();
			name = i.getStringExtra("ContactName");
			number = i.getStringExtra("ContactPhoneNumber");
			photo = i.getStringExtra("photo");
			
			Uri uri  = null;
			if (photo == null){
				im.setImageResource(R.drawable.noface);
			}else{
				uri = Uri.parse(photo);
				im.setImageURI(uri);
			}

			
			tv_name.setText(name);
			
			IntentFilter filter = new IntentFilter();
	        filter.addAction("android.SipDemo.INCOMING_CALL");
	        callReceiver = new SipIncomingCallReceiver();
	        this.registerReceiver(callReceiver, filter);
		
			initializeManager();
			
		
		
		 
	}
	
	@Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call, menu);
		return true;
	}
	
	 public void initializeManager(){
		 if(mSipManager == null) {
			 new Thread(new Runnable() {
			   
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 mSipManager = SipManager.newInstance(getApplicationContext());

				}
			 });
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
						tv.setText("call...");
					}
					   
					public void onRegistrationFailed(String localProfileUri, int errorCode,
					    String errorMessage) {
						Log.i(TAG, "Registration failed.  Please check settings.");
					}
				});
			}catch(Exception e){
				
			}
			
	 }
	 
	 public void initiateCall() {
		 try{
			 SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				 @Override
	             public void onCallEstablished(SipAudioCall call) {
					 call.startAudio();
	                 call.setSpeakerMode(true);
	                 call.toggleMute();
	                 Log.i(TAG, "Calling...");
	                 tv.setText("calling...");
				 }
				 
				 @Override
	             public void onCallEnded(SipAudioCall call) {
					 tv.setText("call end");
				 }
			 };
			 call = mSipManager.makeAudioCall(mSipProfile.getUriString(),callTo , listener, 30);
		 }catch(Exception e){
			 if (call != null) {
	                call.close();
	         }
		 }
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
