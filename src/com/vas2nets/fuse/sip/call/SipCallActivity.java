package com.vas2nets.fuse.sip.call;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.sip.core.SipIntentService;
import com.vas2nets.fuse.sip.core.SipIntentService.MyBinder;

public class SipCallActivity extends Activity {
	
	private SipIntentService ss;
	private TextView tv;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sip_call);
		tv = (TextView) findViewById(R.id.output);
		
		
		
		ss = new SipIntentService();
		
		 bindService(new Intent(SipCallActivity.this, SipIntentService.class), conn, Context.BIND_AUTO_CREATE);
		
		  try {
			ss.manager.setRegistrationListener(ss.me.getUriString(), new SipRegistrationListener() {

					@Override
					public void onRegistering(String arg0) {
						// TODO Auto-generated method stub
						//updateStatus("Registering with SIP Server...");
						tv.setText("Registering with SIP Server...");
					}

					@Override
					public void onRegistrationDone(String arg0, long arg1) {
						// TODO Auto-generated method stub
						//updateStatus("Ready");
						tv.setText("Ready");
					}

					@Override
					public void onRegistrationFailed(String arg0, int arg1,
							String arg2) {
						// TODO Auto-generated method stub
						//updateStatus("Registration failed.  Please check settings.");
						tv.setText("Registration failed.  Please check settings.");
					}
			  	
			  });
			  
			 
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sip_call, menu);
		return true;
	}
	
	private ServiceConnection conn = new ServiceConnection(){

		@SuppressLint("ShowToast")
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			ss = ((SipIntentService.MyBinder) service).getService();
			Toast.makeText(SipCallActivity.this, "Connectedxx", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			ss = null;
		}
		
	};

}
