package com.vas2nets.fuse.gcm;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.sip.chat.ChatActivity;

public class GcmFuseIntentService extends IntentService {
	
	private String message;
	private String phoneNumber;
	 public static final int NOTIFICATION_ID = 1;
	    private NotificationManager mNotificationManager;
	    NotificationCompat.Builder builder;
	    private static final String TAG = "PUSH";
	
	public GcmFuseIntentService() {
		super("Fuse GCM");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	phoneNumber = pref.getString("FusePhoneNumber", null);
		
		Bundle extras = intent.getExtras();
		message = extras.getString("message");
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
	       // in your BroadcastReceiver.
	       String messageType = gcm.getMessageType(intent);
	       if (!extras.isEmpty()) {
	    	   /*
	            * Filter messages based on message type. Since it is likely that GCM
	            * will be extended in the future with new message types, just ignore
	            * any message types you're not interested in, or that you don't
	            * recognize.
	            */
	           if (GoogleCloudMessaging.
	                   MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	               sendNotification("Send error: " + extras.toString());
	           } else if (GoogleCloudMessaging.
	                   MESSAGE_TYPE_DELETED.equals(messageType)) {
	               sendNotification("Deleted messages on server: " +
	                       extras.toString());
	           // If it's a regular GCM message, do some work.
	           } else if (GoogleCloudMessaging.
	                   MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	               // This loop represents the service doing some work.
	               for (int i=0; i<5; i++) {
	                   Log.i(TAG, "Working... " + (i+1)
	                           + "/5 @ " + SystemClock.elapsedRealtime());
	                   try {
	                       Thread.sleep(5000);
	                   } catch (InterruptedException e) {
	                   }
	               }
	               Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
	               // Post notification of received message.
	               //sendNotification("Received: " + extras.toString());
	               //sendNotification(extras.toString());
	               sendNotification(message);
	               Log.i(TAG, "Received: " + extras.toString());
	           }
	       }
	    // Release the wake lock provided by the WakefulBroadcastReceiver.
	       GcmFuseBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	 private void sendNotification(String msg) {
		   	
		   	Intent i = new Intent(this, ChatActivity.class);
		   	i.putExtra("message", msg);
		   	//i.putExtra("ChatName", name);
			i.putExtra("contactPhoneNumber", phoneNumber);
		   	
		       mNotificationManager = (NotificationManager)
		               this.getSystemService(Context.NOTIFICATION_SERVICE);

		       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		               i, 0);
		       
		       

		       NotificationCompat.Builder mBuilder =
		               new NotificationCompat.Builder(this)
		       .setSmallIcon(R.drawable.ic_launcher)
		       .setContentTitle(phoneNumber)
		       .setStyle(new NotificationCompat.BigTextStyle()
		       .bigText(msg))
		       .setTicker(msg)
		       .setAutoCancel(true)
		       .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
		       .setContentText(msg);
		       
		       mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		       //mBuilder.setLights(Color.RED, 3000, 3000); 
		       //mBuilder.setSound(Uri.parse(""));
		      
				// Vibrate if vibrate is enabled
		       //mBuilder.defaults |= Notification.DEFAULT_VIBRATE;

		       mBuilder.setContentIntent(contentIntent);
		       mNotificationManager.notify((int)Calendar.getInstance().getTimeInMillis(), mBuilder.build());
		       {
		       	// Wake Android Device when notification received
		           PowerManager pm = (PowerManager) this
		                   .getSystemService(Context.POWER_SERVICE);
		           final PowerManager.WakeLock mWakelock = pm.newWakeLock(
		                   PowerManager.FULL_WAKE_LOCK
		                           | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
		           mWakelock.acquire();

		           // Timer before putting Android Device to sleep mode.
		           Timer timer = new Timer();
		           TimerTask task = new TimerTask() {
		               public void run() {
		                   mWakelock.release();
		               }
		           };
		           timer.schedule(task, 5000);
		       }
	 }
}
