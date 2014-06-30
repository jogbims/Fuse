package com.vas2nets.fuse.signup;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.MainActivity;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.array;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.util.Constants;

public class RegisterPhoneNumberActivity extends Activity {
	
	private String phoneNumber;
	private String userCountry;
	private Spinner spCountry;
	private String selectedCountry;
	private TextView tv;
	private ArrayAdapter adapter;
	private ProgressDialog pDialog;
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private JSONArray output;
	//private static final String USER_MODEL = "http://83.138.190.170/fuse/user.php";
	
	private String status;
	private String sipId;
	
	private String redirect;
	private String loggedIn;
	
	private String nonSpacePhoneNumber;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_phone_number);
		
		spCountry = (Spinner) findViewById(R.id.countryspinner);
		tv = (TextView) findViewById(R.id.phonenumbereditText);
		
		//userCountry = getCountry();
		String [] array_spinner = new String [200];
		
		array_spinner = getResources().getStringArray(R.array.countries_array);
		adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCountry.setAdapter(adapter);
		//spCountry.setSelection(adapter.getPosition(userCountry));
		//spCountry.setSelection(adapter.getPosition("Nigeria"));
		
		
		System.out.println("My country: " + userCountry);
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	redirect = pref.getString("GotPin", null);
    	
    	SharedPreferences pref1 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	loggedIn = pref1.getString("Loggedin", null);
    	
    	
    	if (loggedIn == "yes"){
    		Intent intent = new Intent(RegisterPhoneNumberActivity.this, MainActivity.class);
    		startActivity(intent);
    		finish();
    	}else if (redirect == "yes"){
    		Intent im = new Intent(RegisterPhoneNumberActivity.this, MainActivity.class);
    		startActivity(im);
    		finish();
    	}
    	
    	// close this activity
        //finish();
    	/*
    	if (loggedIn.equals("yes")){
    		Intent i = new Intent(RegisterPhoneNumberActivity.this, MainActivity.class);
			finish();
			startActivity(i);
    	}else if (redirect == "yes"){
    		Intent i = new Intent(RegisterPhoneNumberActivity.this, VerifyPinActivity.class);
			finish();
			startActivity(i);
    	}
    	
      
    	/*
    	if (redirect == "yes"){
    		Intent i = new Intent(RegisterPhoneNumberActivity.this, VerifyPinActivity.class);
			finish();
			startActivity(i);
    	}*/
		
	}
	
	public void register(View v){
		
		selectedCountry = spCountry.getSelectedItem().toString();
		
		String code = GetCountryZipCode(selectedCountry);
		
		
		
		//Toast.makeText(RegisterPhoneNumberActivity.this, code, Toast.LENGTH_LONG).show();	
		
		
		phoneNumber = tv.getText().toString();
		
		//remove whitespaces
		nonSpacePhoneNumber = phoneNumber.replaceAll("\\p{Z}","");
		
		if(nonSpacePhoneNumber.length() < 13){
			
				if (nonSpacePhoneNumber.startsWith("+")){
					
					nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
					//nonSpacePhoneNumber = code + nonSpacePhoneNumber;
					//StringUtils.leftPad("foobar", 10, '*');
					nonSpacePhoneNumber = padString(nonSpacePhoneNumber, 12,'0');
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}else if(nonSpacePhoneNumber.startsWith("0")){
					
					nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
					nonSpacePhoneNumber = code + nonSpacePhoneNumber;
					nonSpacePhoneNumber = padString(nonSpacePhoneNumber, 12,'0');
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}else{
					nonSpacePhoneNumber = padString(nonSpacePhoneNumber, 12,'0');
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}
				
		}else{
			
				if (nonSpacePhoneNumber.startsWith("+")){
					
					nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
					//nonSpacePhoneNumber = code + nonSpacePhoneNumber;
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}else if(nonSpacePhoneNumber.startsWith("0")){
					
					nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
					nonSpacePhoneNumber = code + nonSpacePhoneNumber;
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}else{
					
					//Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
					
				}
		}
		/*
		if (nonSpacePhoneNumber.startsWith("+")){
			
			nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
			//nonSpacePhoneNumber = code + nonSpacePhoneNumber;
			Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
			
		}else if(nonSpacePhoneNumber.startsWith("0")){
			
			nonSpacePhoneNumber = nonSpacePhoneNumber.substring(1);
			nonSpacePhoneNumber = code + nonSpacePhoneNumber;
			Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
			
		}else{
			
			Toast.makeText(RegisterPhoneNumberActivity.this, nonSpacePhoneNumber, Toast.LENGTH_LONG).show();
			
		}*/
		
		verifyPhoneNumberDialog(nonSpacePhoneNumber);
		
		/*
		if(isOnline()){
			
			phoneNumber = tv.getText().toString();
			
			if (phoneNumber.startsWith("+")){
				phoneNumber = phoneNumber.substring(1);
	            System.out.println(phoneNumber);
	        }else if(phoneNumber.startsWith("0")){
	        	phoneNumber = phoneNumber.substring(1);
	        	phoneNumber = "234" + phoneNumber;
	            System.out.println(phoneNumber);
	        }else{
	            System.out.println(phoneNumber);
	        }
			
			
			selectedCountry = spCountry.getSelectedItem().toString();
			
			
			SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
			Editor editor = pref.edit();
			editor.putString("FusePhoneNumber", phoneNumber);
			editor.commit(); // commit changes
			new SmsAction().execute();
			
		}else{
			
			Toast.makeText(RegisterPhoneNumberActivity.this, "No Internet Connectivity....try again!!!", Toast.LENGTH_LONG).show();	
			
			
		}*/
		
	}
	
	public void verifyPhoneNumberDialog(String phoneNumber){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("PhoneNumber Verification");
	    myAlertDialog.setMessage(Constants.SMS_WILL_BE_SENT + "'" + phoneNumber + "'");
	    myAlertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                   
	                	if(isOnline()){
	                		
	                		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
	            			Editor editor = pref.edit();
	            			editor.putString("FusePhoneNumber", nonSpacePhoneNumber);
	            			editor.commit(); // commit changes
	                		
	                		new SmsAction().execute();
	                		
	                	}else{
	            			
	            			Toast.makeText(RegisterPhoneNumberActivity.this, Constants.NO_INTERNET_ACTIVITY, Toast.LENGTH_LONG).show();	
	            		
	            		}
	                	
	                }
	    });
	    
	    
	    myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                   arg0.cancel();
	                }
	     });
	    
	    myAlertDialog.show();
	}
	
	
	/**
	 * 
	 * @author SAMSON
	 * --- Background process that makes ajax calls to insert phone number
	 * --- and selected country into database
	 *
	 */
	public class SmsAction extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "signup"));
				params.add(new BasicNameValuePair("phone", nonSpacePhoneNumber));
				params.add(new BasicNameValuePair("country", "nigeria"));
				json = jParser.makeHttpRequest(Constants.USER_MODEL, "POST", params);
				
				
			}catch(Exception e){
				
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(RegisterPhoneNumberActivity.this);
			pDialog.setMessage(Constants.SENDING_SMS);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		protected void onPostExecute(String file_url) {
			try{
				output = json.getJSONArray("Response");
				pDialog.dismiss();
				
				for(int i = 0; i < output.length(); i++){
					JSONObject c = output.getJSONObject(i);
					status = c.getString("Status");
					sipId = c.getString("sipId");					
				}
				
				SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
				Editor editor = pref.edit();
				editor.putString("FuseSipID", sipId);
				editor.commit(); // commit changes
				
				//Toast.makeText(RegisterPhoneNumberActivity.this, status + " " + sipId, Toast.LENGTH_LONG).show();	
				
				if (status.equals("OK")){			
					 Intent i = new Intent(RegisterPhoneNumberActivity.this, VerifyPinActivity.class);
					 finish();
					 startActivity(i);
				}else if(status.equals("Duplicate_Signup")){
					Intent i = new Intent(RegisterPhoneNumberActivity.this, VerifyPinActivity.class);
					finish();
					startActivity(i);
				}
				else{
					Toast.makeText(RegisterPhoneNumberActivity.this, Constants.COULD_NOT_SEND_SMS, Toast.LENGTH_LONG).show();	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_phone_number, menu);
		return true;
	}
	
	public String GetCountryZipCode(String countryName){

        //String CountryID="";
        String CountryZipCode="";

       //TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
              //getNetworkCountryIso
        //CountryID= manager.getSimCountryIso().toUpperCase();
        countryName = countryName.toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
                                    String[] g=rl[i].split(",");
                                    if(g[1].trim().equals(countryName.trim())){
                                                        CountryZipCode=g[0];break;  }
        }
        
        return CountryZipCode;
	}
	
	public String padString(String str, int leng,char chr) {
        for (int i = str.length(); i <= leng; i++)
            //str += chr;
        	str = chr + str;
        return str;
    }
	
	
	/**
	 * 
	 * @return Returns the country code stored in the sim card of user phone
	 */
	public String getCountry(){
		TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimCountryIso();
	}

}
