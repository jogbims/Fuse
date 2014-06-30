package com.vas2nets.fuse.social.core;


import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.MainActivity;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.db.ActiveSocialDB;

public class AddSocialNetworksActivity extends Activity {
	
	private ListView listview;
	// SocialAuth Components
		private static SocialAuthAdapter adapter;
		// Variables
		boolean status;
		String providerName;
		public static int pos;
		
		ActiveSocialDB db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_social_networks);
		
		listview = (ListView) findViewById(R.id.socialnetworkslistView);
		
		// Adapter initialization
		adapter = new SocialAuthAdapter(new ResponseListener());
		listview.setAdapter(new CustomSocialAdapter(this, adapter));
		
		db = new ActiveSocialDB(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_social_networks, menu);
		return true;
	}
	
	// To receive the response after authentication
		private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			View v = listview.getChildAt(pos - listview.getFirstVisiblePosition());
			TextView pText = (TextView) v.findViewById(R.id.signstatus);
			pText.setText("Sign Out");
			
			// Get the provider
			providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Toast.makeText(AddSocialNetworksActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();
			//add provider to sqlite
			db.addProvider(providerName);
		}

		@Override
		public void onError(SocialAuthError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBack() {
			// TODO Auto-generated method stub
			
		}
			
		}
		
		public void start(View v){
			Intent i = new Intent(AddSocialNetworksActivity.this, MainActivity.class);
			 finish();
			 startActivity(i);
		}
		
		

}
