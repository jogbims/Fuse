package com.vas2nets.fuse.profile;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.contact.ContactDBHelper;
import com.vas2nets.fuse.contact.FuseContactContentProvider;
import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.image.PhotoUtility;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.social.core.AddSocialNetworksActivity;
import com.vas2nets.fuse.user.UserContentProvider;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class UpdateProfileActivity extends Activity {
	
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	private static final String USER_MODEL = "http://83.138.190.170/fuse/user.php";
	private String firstName;
	private String lastName;
	private String email;
	private String photo;
	private String phoneNumber;
	private String authKey;
	private String deviceId;
	private String sipId;
	
	private TextView ftv;
	private TextView ltv;
	private TextView etv;
	private ImageView profilepix;
	
	
	protected static final int CAMERA_REQUEST = 100;
	protected static final int GALLERY_PICTURE = 101;
	private Intent pictureActionIntent = null;
	private Bitmap bitmap;
	byte [] outputPhoto;
	
	private String id;
	private String myContact = null;
	private List<String> allContacts = new ArrayList<String>();
	
	private JSONObject json;
	private JSONArray output;
	private String status;
	
	private ContactDBHelper dbHelper;
	private ContentValues values;
	
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Fuse";
    /**
     * This is the project number gotten
     * from the Google Cloud API Console"
     */
    String SENDER_ID = "1073942753897";
    GoogleCloudMessaging gcm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		
		ftv = (TextView) findViewById(R.id.firstnameeditText);
		ltv = (TextView) findViewById(R.id.lastnameeditText);
		etv = (TextView) findViewById(R.id.emaileditText);
		profilepix = (ImageView) findViewById(R.id.myprofileimageView);
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	authKey = pref.getString("authkey", null);
    	
    	SharedPreferences pref1 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	phoneNumber = pref1.getString("FusePhoneNumber", null);
    	
    	SharedPreferences pref3 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	sipId = pref3.getString("FuseSipID", null);
		
    	
    	if (checkPlayServices()) {
    		gcm = GoogleCloudMessaging.getInstance(this);
    		deviceId = getRegistrationId(getApplicationContext());
    		
    		if (deviceId.isEmpty()) {
    			registerInBackground();
    		}
    		
    		 SharedPreferences pref2 = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
			 Editor editor = pref2.edit();
			 editor.putString("deviceid", deviceId);
			 editor.commit(); // commit changes
    	}
    	
    	dbHelper = new ContactDBHelper(getApplicationContext());
    	getPhoneNumbers(); //get all contacts phone numbers
		
	}
	
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerInBackground() {
		 new AsyncTask() {

			@Override
			protected Object doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				try {
					
					 if (gcm == null) {
		                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		              }
					 	
					  deviceId = gcm.register(SENDER_ID);
					  
					  SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
					  Editor editor = pref.edit();
					  editor.putString("deviceid", deviceId);
					  editor.commit(); // commit changes
					 
				}catch(Exception e){
					
				}
				return null;
			}
			 
		 }.execute(null, null, null);
	}
	
	
	
	public void pickPicture(View v){
		startDialog();
	}
	
	
	public void createUser(View v){
		firstName = ftv.getText().toString();
		lastName = ltv.getText().toString();
		email = etv.getText().toString();
		//store bitmap in sqlite
		
		
		
		
		Bitmap photobitmap = ((BitmapDrawable)profilepix.getDrawable()).getBitmap();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
	    
	    values = new ContentValues();
		values.put(DBHelper.USER_KEY_FIRSTNAME, firstName);
		values.put(DBHelper.USER_KEY_LASTNAME, lastName);
		values.put(DBHelper.USER_KEY_EMAIL, email);
		values.put(DBHelper.USER_KEY_PHONENUMBER, phoneNumber);
		values.put(DBHelper.USER_KEY_AUTHKEY, authKey);
		values.put(DBHelper.USER_KEY_SIPID, sipId);
		values.put(DBHelper.USER_KEY_DEVICEID, deviceId);
		values.put(DBHelper.USER_KEY_PHOTO, PhotoUtility.getBytes(photobitmap));
		getContentResolver().insert(UserContentProvider.CONTENT_URI, values);
	    
		
		photobitmap.compress(Bitmap.CompressFormat.PNG, 90, bao);
		outputPhoto = bao.toByteArray();
	    photo =Base64.encodeToString(outputPhoto, Base64.DEFAULT);
	    new UpdateMyProfile().execute();
	    
	    
	}
	
	/**
	 *  Starts Dialog box for picture chooser
	 */
	public void startDialog(){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Pictures Option");
	    myAlertDialog.setMessage("How do you want to set your profile picture?");
	    myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                    pictureActionIntent = new Intent(
	                            Intent.ACTION_GET_CONTENT, null);
	                    pictureActionIntent.setType("image/*");
	                    pictureActionIntent.putExtra("return-data", true);
	                    startActivityForResult(pictureActionIntent,
	                            GALLERY_PICTURE);
	                }
	    });
	    
	    
	    myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int arg1) {
	                    pictureActionIntent = new Intent(
	                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	                    startActivityForResult(pictureActionIntent,
	                            CAMERA_REQUEST);

	                }
	     });
	    
	    myAlertDialog.show();
	    
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK && null != data) {
			BitmapDrawable bmpDrawable = null;
            // try to retrieve the image using the data from the intent
            Cursor cursor = getContentResolver().query(data.getData(),null, null, null, null);
            if (cursor != null) {
            	cursor.moveToFirst();

                int idx = cursor.getColumnIndex(ImageColumns.DATA);
                String fileSrc = cursor.getString(idx);
                bitmap = BitmapFactory.decodeFile(fileSrc); 
                bitmap = Bitmap.createScaledBitmap(bitmap,100, 100, false);
                profilepix.setImageBitmap(bitmap);
            }else {

                bmpDrawable = new BitmapDrawable(getResources(), data.getData().getPath());
                profilepix.setImageDrawable(bmpDrawable);
            }
		}
		
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
			bitmap = (Bitmap) data.getExtras().get("data");

            bitmap = Bitmap.createScaledBitmap(bitmap, 100,100, false);
            // update the image view with the bitmap
            profilepix.setImageBitmap(bitmap);
		}
		
		if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled",
                    Toast.LENGTH_SHORT).show();
        }
	}
	
	/**
	 * 
	 * @author SAMSON
	 *
	 */
	 class UpdateMyProfile extends AsyncTask<String, String, String> {
		 
		 @Override
			protected void onPreExecute() {
				super.onPreExecute();
				try{
					pDialog = new ProgressDialog(UpdateProfileActivity.this);
					pDialog.setMessage("Updating Profile...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}catch(Exception e){
					
				}
				
		 }

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "update"));
				params.add(new BasicNameValuePair("authkey", authKey));
				params.add(new BasicNameValuePair("fname", firstName));
				params.add(new BasicNameValuePair("lname", lastName));
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("photo", photo));
				params.add(new BasicNameValuePair("deviceid", deviceId));
				JSONArray allNumbers = new JSONArray();
				for(String n : allContacts){
					allNumbers.put(n);
				}
				params.add(new BasicNameValuePair("allphones", allNumbers.toString()));
				
				json = jParser.makeHttpRequest(USER_MODEL, "POST", params);
			}catch(Exception e){
				
			}
			
			
			return null;
		}
		
		
		protected void onPostExecute(String file_url) {
			try{
				output = json.getJSONArray("Response");
				pDialog.dismiss();
				String allPhones = null;
				for(int i = 0; i < output.length(); i++){
					JSONObject c = output.getJSONObject(i);
					status = c.getString("Status");		
					allPhones = c.getString("AllPhones");
				}
				
				storeNumbersInSqlite(allPhones); // stores all contacts phone numbers in sqlite for persistence
				
				if (status.equals("OK")){
					Intent i = new Intent(UpdateProfileActivity.this, AddSocialNetworksActivity.class);
					finish();
					startActivity(i);
					
					SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
					  Editor editor = pref.edit();
					  editor.putString("Loggedin", "yes");
					  editor.commit(); // commit changes
					
				}else{
					Toast.makeText(UpdateProfileActivity.this, "Could not Register....try again!!!", Toast.LENGTH_LONG).show();	
				}
				
				
			}catch(Exception e){
				
			}
		}
		 
	 }
	 
	 public void storeNumbersInSqlite(String fuseNumbers){
	 
		 ContentValues v = new ContentValues();
		 String REGEX = "\\s*(\\s|=>|,)\\s*";
		 Pattern p = Pattern.compile(REGEX);
		 String[] items = p.split(fuseNumbers);
		 for(String s : items) {
			 v.put(ContactDBHelper.CONTACTS_PHONE_NUMBER, s);
		 }
		 getContentResolver().insert(FuseContactContentProvider.CONTENT_URI, v);
	 }
	 
	 /**
	  * Gets the current registration ID for application on GCM service.
	  * <p>
	  * If result is empty, the app needs to register.
	  *
	  * @return registration ID, or empty string if there is no existing
	  *         registration ID.
	  */
	 @SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
	     final SharedPreferences prefs = getGCMPreferences(context);
	     String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	     if (registrationId.isEmpty()) {
	         Log.i("Device ID", "Registration not found.");
	         return "";
	     }
	     return registrationId;
	 }
	
	 /**
	  * @return Application's {@code SharedPreferences}.
	  */
	 private SharedPreferences getGCMPreferences(Context context) {
	     // This sample app persists the registration ID in shared preferences
	     return getSharedPreferences(UpdateProfileActivity.class.getSimpleName(),
	             Context.MODE_PRIVATE);
	 }
	
	
	
	/**
	 *  Gets all phone numbers
	 * @return list of phone numbers
	 */
	public List<String> getPhoneNumbers(){
		ContentResolver cr = getContentResolver();
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder);
		if (cur.getCount() > 0){
			while (cur.moveToNext()){
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
					while (pCur.moveToNext()){
						myContact = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						//System.out.println("phone" + phoneNumber);
						if (myContact.startsWith("+")){
							myContact = myContact.substring(1);
				            System.out.println(myContact);
				        }else if(myContact.startsWith("0")){
				        	myContact = myContact.substring(1);
				        	myContact = "234" + myContact;
				            System.out.println(myContact);
				        }else{
				            System.out.println(myContact);
				        }
						
					}
					pCur.close();
				}
				
				
				allContacts.add(myContact);
			}
		}
		return allContacts;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_profile, menu);
		return true;
	}

}
