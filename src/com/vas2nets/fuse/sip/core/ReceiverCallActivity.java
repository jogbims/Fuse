package com.vas2nets.fuse.sip.core;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class ReceiverCallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receiver_call);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receiver_call, menu);
		return true;
	}

}
