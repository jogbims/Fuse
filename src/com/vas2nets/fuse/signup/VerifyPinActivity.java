package com.vas2nets.fuse.signup;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.profile.UpdateProfileActivity;
import com.vas2nets.fuse.util.Constants;

public class VerifyPinActivity extends Activity {
	
	private String pin;
	private String phoneNumber;
	private String authKey;
	private String status;
	private TextView tv;
	private ProgressDialog pDialog;
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private JSONArray output;
	//private static final String USER_MODEL = "http://83.138.190.170/fuse/user.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_pin);
		
		tv = (TextView) findViewById(R.id.fusepin);
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	phoneNumber = pref.getString("FusePhoneNumber", null);
    	
    	
    	SharedPreferences pref2 = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
		Editor editor1 = pref2.edit();
		editor1.putString("GotPin", "yes");
		editor1.commit(); // commit changes
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.verify_pin, menu);
		return true;
	}
	
	
	public void verifyPin(View v){
		
		if(isOnline()){
			
			pin = tv.getText().toString();
			new SendPinForVerification().execute();
			
		}else{
			// avoid hardoding string , user Constants class in package com.vas2nets.fuse.util
			Toast.makeText(VerifyPinActivity.this, Constants.NO_INTERNET_ACTIVITY, Toast.LENGTH_LONG).show();	
		
		}
		
		
	}
	
	class SendPinForVerification extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "verify"));
				params.add(new BasicNameValuePair("phone", phoneNumber));
				params.add(new BasicNameValuePair("pin", pin));
				json = jParser.makeHttpRequest(Constants.USER_MODEL, "POST", params);
				
			}catch(Exception e){
				
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			try{
				pDialog = new ProgressDialog(VerifyPinActivity.this);
				pDialog.setMessage("Verifying pin...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}catch(Exception e){
				
			}
		}
		
		protected void onPostExecute(String file_url) {
			try{
				output = json.getJSONArray("Response");
				pDialog.dismiss();
				
				for(int i = 0; i < output.length(); i++){
					JSONObject c = output.getJSONObject(i);
					authKey = c.getString("authkey");
					status = c.getString("Status");					
				}
				
				if (status.equals("OK")){
					SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
					Editor editor = pref.edit();
					editor.putString("authkey", authKey);
					editor.commit(); // commit changes
					
					 Intent i = new Intent(VerifyPinActivity.this, UpdateProfileActivity.class);
					 finish();
					 startActivity(i);
					
				}else{
					Toast.makeText(VerifyPinActivity.this, Constants.PIN_VERIFICATION_FAILED, Toast.LENGTH_LONG).show();	
				}
				
				
				
			}catch(Exception e){
				
			}
		}
		
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}


}
