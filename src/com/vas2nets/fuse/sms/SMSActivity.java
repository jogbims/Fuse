package com.vas2nets.fuse.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.json.JSONParser;

public class SMSActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	private String name;
	private String contactPhoneNumber;
	private String nonSpacePhoneNumber;
	private String myPhoneNumber;
	private String msg;
	private String smsOutput;
	
	private TextView wordCountTxt;
	private EditText smsEditText;
	private ListView lv;
	
	private JSONArray output;
	
	private SMSListCursorAdapter cAdapter;
	
	
	private SMSDBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues content;
	
	String[] projection = new String[] {
			SMSDBHelper.SMS_KEY_ID,
			SMSDBHelper.SMS_KEY_MESSAGE,
            SMSDBHelper.SMS_KEY_RECEIVER,
            SMSDBHelper.SMS_KEY_STATUS,
            SMSDBHelper.SMS_KEY_TIME
    };
	
	private ProgressDialog pDialog;
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private static final String SMS_MODEL = "http://83.138.190.170/fuse/user.php";
	
	String authKey = null;
	String status = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		name = i.getStringExtra("Name");
		contactPhoneNumber = i.getStringExtra("PhoneNumber");
		//contactPhoneNumber = contactPhoneNumber.startsWith("+") ? contactPhoneNumber.substring(1) : contactPhoneNumber;
		
		
		if (contactPhoneNumber.startsWith("+")){
			contactPhoneNumber = contactPhoneNumber.substring(1);
            System.out.println(contactPhoneNumber);
        }else if(contactPhoneNumber.startsWith("0")){
        	contactPhoneNumber = contactPhoneNumber.substring(1);
        	contactPhoneNumber = "234" + contactPhoneNumber;
            System.out.println(contactPhoneNumber);
        }else{
            System.out.println(contactPhoneNumber);
        }
		
		
		//remove whitespaces
		nonSpacePhoneNumber = contactPhoneNumber.replaceAll("\\p{Z}","");
		this.getActionBar().setTitle(name);
		
		mHelper = new SMSDBHelper(this);
		dataBase = mHelper.getWritableDatabase();
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	myPhoneNumber = pref.getString("FusePhoneNumber", null);
		
		content = new ContentValues();
		
		wordCountTxt = (TextView) findViewById(R.id.wordcounttextView);
		smsEditText = (EditText) findViewById(R.id.smseditText);
		lv = (ListView ) findViewById(R.id.smslistView);
		lv.setDivider(null);
		
		 String selection = SMSDBHelper.SMS_KEY_RECEIVER + "=" + nonSpacePhoneNumber;
		 ContentResolver cr = getContentResolver();
		 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, selection, null, null);
		 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, DBHelper.CHAT_KEY_RECEIVER + "=?", new String[] {contactPhoneNumber}, null);
		 Cursor cursor = cr.query(SMSContentProvider.CONTENT_URI, projection, selection, null, null);
			
		cAdapter = new SMSListCursorAdapter(this,cursor);
		
		lv.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
		
		//lv.setSelection(lv.getAdapter().getCount()-1);
		  //Ensures a loader is initialized and active.
		  //getLoaderManager().initLoader(0, null, this);
		getSupportLoaderManager().initLoader(0, null, SMSActivity.this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		// restrict characters to 140
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(140);
		smsEditText.setFilters(FilterArray);
		
		
		// update character count
		TextWatcher mTextEditorWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           //This sets a textview to the current length
	           wordCountTxt.setText("(" + String.valueOf(s.length()) + "/140)");
	        }

	        public void afterTextChanged(Editable s) {
	        }
		};
		smsEditText.addTextChangedListener(mTextEditorWatcher);
		
	}
	
	private class SMSListCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		
		TextView msgTxt, timeTxt, statusTxt;

		@SuppressWarnings("deprecation")
		public SMSListCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context arg1, Cursor cursor) {
			// TODO Auto-generated method stub
			msgTxt = (TextView) view.findViewById(R.id.smsmsgtextView);
			timeTxt = (TextView) view.findViewById(R.id.smsdatetimetextView);
			statusTxt = (TextView) view.findViewById(R.id.smsDeliveredtextView);
			
			String message = cursor.getString(cursor.getColumnIndex(SMSDBHelper.SMS_KEY_MESSAGE));
			String time = cursor.getString(cursor.getColumnIndex(SMSDBHelper.SMS_KEY_TIME));
			String status = cursor.getString(cursor.getColumnIndex(SMSDBHelper.SMS_KEY_STATUS));
			
			msgTxt.setText(message);
			msgTxt.setBackgroundResource(R.drawable.bubble_yellow);
			
			
			
			timeTxt.setText(time);
			statusTxt.setText(status);
			
			
			
		}

		@Override
		public View newView(Context arg0, Cursor cursor, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.smscustomlist, container, false);
			
			return view;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sm, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = SMSContentProvider.CONTENT_URI;
		//return new CursorLoader(this, uri, projection, DBHelper.CHAT_KEY_RECEIVER + "=?", new String[] {contactPhoneNumber}, null);
		String my_selection = SMSDBHelper.SMS_KEY_RECEIVER + "=?";
		String [] my_selectionArgs = {contactPhoneNumber};
		String selection = SMSDBHelper.SMS_KEY_RECEIVER + "=" + nonSpacePhoneNumber;
		//return new CursorLoader(this, uri, projection, my_selection, my_selectionArgs, null);
		return new CursorLoader(this, uri, projection, selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(null);
	}
	
	public void sendMessage(View v){
		msg = smsEditText.getText().toString();
		if (msg != null){
			new SendSMS().execute();
		}else{
			Toast.makeText(SMSActivity.this, "Empty content", Toast.LENGTH_LONG).show();	
			
		}
		smsEditText.setText("");
	}
	
	class SendSMS extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			try{
				pDialog = new ProgressDialog(SMSActivity.this);
				pDialog.setMessage("Sending SMS...");
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
				//params.add(new BasicNameValuePair("mynumber", myPhoneNumber));
				
				if(myPhoneNumber.startsWith("234")){
					myPhoneNumber = myPhoneNumber.substring(3);
					myPhoneNumber = "0" + myPhoneNumber;
				}
				
				params.add(new BasicNameValuePair("action", "sendsms"));
				params.add(new BasicNameValuePair("sender", myPhoneNumber));
				params.add(new BasicNameValuePair("phone", nonSpacePhoneNumber));
				params.add(new BasicNameValuePair("smsmessage", msg));
				
				
				//params.add(new BasicNameValuePair("number", nonSpacePhoneNumber));
				//params.add(new BasicNameValuePair("msg", msg));
				json = jParser.makeHttpRequest(SMS_MODEL, "POST", params);
			}catch(Exception e){
				
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			try{
				output = json.getJSONArray("Response");
				pDialog.dismiss();
				
				
				
				for(int i = 0; i < output.length(); i++){
					JSONObject c = output.getJSONObject(i);
					authKey = c.getString("smsstatus");
					status = c.getString("Status");					
				}
				
				
				if (status.equals("OK")){
					insertToSQLite();
					Toast.makeText(SMSActivity.this, "SMS Sent", Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(SMSActivity.this, "SMS not Sent...TRY AGAIN!!!", Toast.LENGTH_LONG).show();	
				}
				
				
				
			}catch(Exception e){
				
			}
		}

	}
	
	public void insertToSQLite(){
		if (msg != null){
			
			Date d = new Date();
			
			content.put(SMSDBHelper.SMS_KEY_MESSAGE, msg);
			content.put(SMSDBHelper.SMS_KEY_RECEIVER, nonSpacePhoneNumber);
			content.put(SMSDBHelper.SMS_KEY_STATUS, "delivered");
			content.put(SMSDBHelper.SMS_KEY_TIME, d.toString());
			
			getContentResolver().insert(SMSContentProvider.CONTENT_URI, content);
			dataBase.close();
		}
	}
	

}
