package com.vas2nets.fuse.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class NotMemberActivity extends Activity {
	private String name;
	private String phoneNumber;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_member);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		name = i.getStringExtra("ChatName");
		phoneNumber = i.getStringExtra("contactPhoneNumber");
		
		tv = (TextView) findViewById(R.id.phoneNumbertextView);
		tv.setText(phoneNumber);
		
		this.getActionBar().setTitle(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.not_member, menu);
		return true;
	}
	
	public void sendInvite(View v){
		
	}

}
