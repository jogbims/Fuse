package com.vas2nets.fuse.contact;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.api.ISipService;
import com.csipsimple.api.SipProfile;
import com.csipsimple.api.SipUri;
import com.csipsimple.service.SipService;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.signup.RegisterPhoneNumberActivity.SmsAction;
import com.vas2nets.fuse.sip.chat.SipChatActivity;
import com.vas2nets.fuse.sms.SMSActivity;
import com.vas2nets.fuse.R;

public class ContactDetailsActivity extends Activity {
	
	List<String> allFuseContacts = new ArrayList<String>();
	private static final String GET_PHONENUMBERS = "http://83.138.190.170/fusescript/getPhoneNumber.php";
	private static final String USER_MODEL = "http://83.138.190.170/fuse/user.php";
	
	
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private JSONArray output;
	private JSONArray allNumbers;
	
	
	private JSONObject sipjson;
	private JSONArray sipoutput;
	private String sipId;
	private String sipStatus;
	
	private ProgressDialog pDialog;
	
	List<String> fuseContacts;
	JSONArray fprofile;
	
	String name;
	String phoneNumber;
	String photo;
	String fuseInstalled;
	
	String[] projection = new String[] {
			ContactDBHelper.CONTACTS_ID,
			ContactDBHelper.CONTACTS_PHONE_NUMBER
    };
	
	private long existingProfileId = SipProfile.INVALID_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		name = i.getStringExtra("ContactName");
		phoneNumber = i.getStringExtra("ContactNumber");
		photo = i.getStringExtra("ContactPhoto");
		fuseInstalled = i.getStringExtra("Installed");
		
		this.getActionBar().setTitle(name);
		
		TextView tv_name = (TextView)findViewById(R.id.d_contactname);
		tv_name.setText(name);
		
		TextView tv_phonenumber = (TextView)findViewById(R.id.d_contactnumber);
		tv_phonenumber.setText(phoneNumber);
		
		ImageView iv = (ImageView)findViewById(R.id.contactImageView);
		Uri uri = null;
		
		if (photo == null){
			 iv.setImageResource(R.drawable.noface);
		}else{
			uri = Uri.parse(photo);
			iv.setImageURI(uri);
		}
		
		fuseContacts = new ArrayList<String>();
    	//new GetFusePhoneNumber().execute();
		
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
		getFuseContacts();
		
		bindService(new Intent(this, SipService.class), connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void startChat(View v){
		
		if (fuseInstalled.equals("yes")){
			/*
			Intent i = new Intent(this, ChatActivity.class);
			i.putExtra("ChatName", name);
			i.putExtra("contactPhoneNumber", phoneNumber);
		    startActivity(i);*/
		    
		    Intent i = new Intent(this, SipChatActivity.class);
			i.putExtra("ChatName", name);
			i.putExtra("contactPhoneNumber", phoneNumber);
		    startActivity(i);
		}else{
			Intent i = new Intent(this, NotMemberActivity.class);
			i.putExtra("ChatName", name);
			i.putExtra("contactPhoneNumber", phoneNumber);
		    startActivity(i);
		}
		
	}
	
	public void startFreeCall(View v){
		
		if(isOnline()){
			
			if (fuseInstalled.equals("yes")){
				new GetSipID().execute();
			}else{
				Intent i = new Intent(this, NotMemberActivity.class);
				i.putExtra("ChatName", name);
				i.putExtra("contactPhoneNumber", phoneNumber);
			    startActivity(i);
			}
			
			
			
		}else{
			Toast.makeText(ContactDetailsActivity.this, "No Internet Connectivity", Toast.LENGTH_LONG).show();	
			
		}
		
		/*
		String sipAddress = "sip:psalmsin@sip.linphone.org";
		
		Intent it = new Intent(Intent.ACTION_CALL);
		it.setData(SipUri.forgeSipUri("csip", sipAddress));
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
		startActivity(it);
		*/
		//openContactListDialog();
		
		
		
	}
	
	public void sendSMS(View v){
		Intent i = new Intent(this, SMSActivity.class);
		i.putExtra("Name", name);
		i.putExtra("PhoneNumber", phoneNumber);
		startActivity(i);
	}
	
	// Get all fuse phone numbers
			class GetFusePhoneNumber extends AsyncTask<String, String, String> {

				@Override
				protected String doInBackground(String... arg0) {
					// TODO Auto-generated method stub
					try{
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("number", phoneNumber));
						json = jParser.makeHttpRequest(GET_PHONENUMBERS, "POST", params);
						
					}catch(Exception e){
						
					}
					return null;
				}
				
				@Override
				protected void onPreExecute() {
					
				}
				
				protected void onPostExecute(String file_url) {
					try{
						fprofile = json.getJSONArray("fprofile");
						
						for(int i = 0; i < fprofile.length(); i++){
							JSONObject c = fprofile.getJSONObject(i);
							String fusePhoneNumber = c.getString("phone");
							fuseContacts.add(fusePhoneNumber);
						}
						
					}catch(Exception e){
						
					}
				}
				
			}
			
			class GetSipID extends AsyncTask<String, String, String> {
				
				
				@Override
				protected void onPreExecute() {
					pDialog = new ProgressDialog(ContactDetailsActivity.this);
					pDialog.setMessage("Initializing Call...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}

				@Override
				protected String doInBackground(String... arg0) {
					// TODO Auto-generated method stub
					try{
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("action", "getsip"));
						params.add(new BasicNameValuePair("phone", phoneNumber));
						sipjson = jParser.makeHttpRequest(USER_MODEL, "POST", params);
					}catch(Exception e){
						
					}
					return null;
				}
				
				protected void onPostExecute(String file_url) {
					try{
						sipoutput = sipjson.getJSONArray("Response");
						pDialog.dismiss();
						for(int i = 0; i < sipoutput.length(); i++){
							JSONObject c = sipoutput.getJSONObject(i);
							sipStatus = c.getString("Status");
							sipId = c.getString("sipId");					
						}
						
						
						if (sipStatus.equals("OK")){
							
							 String sipAddress = sipId + "@197.253.10.26";
							 
							 
							
							 // Get current account if any
				            Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI, new String[] {
				                    SipProfile.FIELD_ID,
				                    SipProfile.FIELD_ACC_ID,
				                    SipProfile.FIELD_REG_URI
				            }, null, null, SipProfile.FIELD_PRIORITY+ " ASC");
				            if(c != null) {
				                try {
				                    if(c.moveToFirst()) {
				                        SipProfile foundProfile = new SipProfile(c);
				                        existingProfileId = foundProfile.id;
				                        //t1.setText(foundProfile.getSipUserName() + "@" + foundProfile.getSipDomain());
				                    }
				                }catch(Exception e) {
				                    Log.e("ME", "Some problem occured while accessing cursor", e);
				                }finally {
				                    c.close();
				                }
				                
				            }
				            
				            try{
				            	if (service != null){
				            		String toNumber = "sip:" + sipAddress;
				            		service.makeCall(toNumber, (int) existingProfileId);
				            		
				            	}else{
				            		Toast.makeText(getApplicationContext(),"No Call Service",Toast.LENGTH_SHORT).show();
				            	}
				            }catch(Exception e){
				            	
				            }
							 
							 
							 
							 /*
							 //String sipAddress = "29" + "@197.253.10.26";
	        	           		
	        	           	 Intent it = new Intent(Intent.ACTION_CALL);
	        	           	 it.setData(SipUri.forgeSipUri("csip", sipAddress));
	        	           	 it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	           	 it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
	        	           	 startActivity(it);
							*/
						}else{
							
							
							
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

			
			public void getFuseContacts(){
				ContentResolver cr = getContentResolver();
				 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, selection, null, null);
				 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, DBHelper.CHAT_KEY_RECEIVER + "=?", new String[] {contactPhoneNumber}, null);
				 Cursor cursor = cr.query(FuseContactContentProvider.CONTENT_URI, projection, null, null, null);
				 if (cursor.getCount() > 0){
					 while (cursor.moveToNext()){
						 	fuseContacts.add(cursor.getString(cursor.getColumnIndex(ContactDBHelper.CONTACTS_PHONE_NUMBER))); // "Title" is the field name(column) of the Table                 					        
					 }
				 }
				 
				 
				 

			}
			
			// Service connection
		    private ISipService service;
		    private ServiceConnection connection = new ServiceConnection() {
		        @Override
		        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		            service = ISipService.Stub.asInterface(arg1);
		        }

		        @Override
		        public void onServiceDisconnected(ComponentName arg0) {
		            service = null;
		        }
		    };
		    
			
			private boolean isNetworkAvailable() {
			    ConnectivityManager connectivityManager 
			          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
			}
			
			public void openContactListDialog(){
				final Dialog dialog = new Dialog(ContactDetailsActivity.this);
				dialog.setContentView(R.layout.call_list);
				dialog.setTitle("Call List");
				
				final ListView mList = (ListView)dialog.findViewById(R.id.calllistlistView);
				List<String> al = new ArrayList<String>();
		        al.add("Samson");
		        al.add("Gbolaga");
		        al.add("Emmanuel");
		        al.add("Michael");
		        al.add("Jonathan");
		        al.add("Olubunmi");
		        
		        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
		                 this, 
		                 android.R.layout.simple_list_item_1,
		                 al);

		         mList.setAdapter(arrayAdapter); 
		         
		         mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		        	  @Override
		        	  public void onItemClick(AdapterView<?> parent, View view,
		        	                 int position, long id) {

		        	       // ListView Clicked item index
		        	               int itemPosition     = position;
		        	               
		        	               
		        	              

		        	               // ListView Clicked item value
		        	               String  itemValue    = (String) mList.getItemAtPosition(position);
		        	               if (itemValue == "Samson"){
				        	            //String sipAddress = "sip:psalmsin@sip.linphone.org";
		        	            	   String sipAddress = "sip:30000@197.253.10.26";
				        	           		Intent it = new Intent(Intent.ACTION_CALL);
				        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
				        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
				        	           		startActivity(it);
		        	               }else if(itemValue == "Gbolaga"){
				        	            	 String sipAddress = "sip:gbolaga@sip.linphone.org";
				        	            	   //String sipAddress = "sip:20000@197.253.10.26";
				        	           		Intent it = new Intent(Intent.ACTION_CALL);
				        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
				        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
				        	           		startActivity(it);
		        	               }else if(itemValue == "Emmanuel"){
				        	            	   String sipAddress = "sip:emmanuelo@sip.linphone.org";
				        	           		
				        	           		Intent it = new Intent(Intent.ACTION_CALL);
				        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
				        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
				        	           		startActivity(it);
		        	               }else if(itemValue == "Michael"){
				        	            	   String sipAddress = "sip:michealvas2nets@sip.linphone.org";
				        	           		
				        	           		Intent it = new Intent(Intent.ACTION_CALL);
				        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
				        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
				        	           		startActivity(it);
		        	               }else if(itemValue == "Jonathan"){
			        	            	   String sipAddress = "sip:jonathan@sip.linphone.org";
				        	           		
				        	           		Intent it = new Intent(Intent.ACTION_CALL);
				        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
				        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
				        	           		startActivity(it);
		        	               }else if(itemValue == "Olubunmi"){
		        	            	   String sipAddress = "sip:olubunmi@sip.linphone.org";
			        	           		
			        	           		Intent it = new Intent(Intent.ACTION_CALL);
			        	           		it.setData(SipUri.forgeSipUri("csip", sipAddress));
			        	           		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        	           		it.putExtra(SipProfile.FIELD_ACC_ID, SipProfile.FIELD_ACC_ID);
			        	           		startActivity(it);
	        	               }
		        	               
		        	               
		        	               
		        	                // Show Alert 
		        	                //Toast.makeText(getApplicationContext(),"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();
		        	    }
		        	        });
		        
		         dialog.show();
			}

}
