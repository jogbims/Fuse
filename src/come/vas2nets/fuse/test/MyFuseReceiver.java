package come.vas2nets.fuse.test;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyFuseReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Intent myIntent = new Intent(arg0, MyFuseService.class);
	    arg0.startService(myIntent);
	}

}
