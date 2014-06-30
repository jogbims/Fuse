package com.vas2nets.fuse.social.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MySocialReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Intent myIntent = new Intent(arg0, MySocialService.class);
	    arg0.startService(myIntent);
		
	}

}
