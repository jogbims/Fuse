package com.vas2nets.fuse.sip.core;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;

public class SipIncomingCallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	    SipAudioCall incomingCall = null;
	    
	    try{
	    	SipAudioCall.Listener listener = new SipAudioCall.Listener() {
	    		@Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {
                        call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
	    	};
	    	
	    	 CallActivity wtActivity = (CallActivity) context;

	            incomingCall = wtActivity.mSipManager.takeAudioCall(intent, listener);
	            incomingCall.answerCall(30);
	            incomingCall.startAudio();
	            incomingCall.setSpeakerMode(true);
	            if(incomingCall.isMuted()) {
	                incomingCall.toggleMute();
	            }

	            wtActivity.call = incomingCall;

	            intent.setClass(context, ReceiverCallActivity.class);
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(intent);

	        
	    	
	    } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }
	    
	    
	    
	    
	    
	}

}
