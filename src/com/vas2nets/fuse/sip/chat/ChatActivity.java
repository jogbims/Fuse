package com.vas2nets.fuse.sip.chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.color;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.chat.smiley.SmileyClass;
import com.vas2nets.fuse.contact.SelectContactsActivity;
import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.location.LocationAnotherActivity;
import come.vas2nets.fuse.test.MyFuseService;
import come.vas2nets.fuse.test.MyFuseService.MyBinder;

public class ChatActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	private String phoneNumber;
	private String contactPhoneNumber;
	private String name;
	private String chatKey;
	private String message = null;
	private String authKey;
	
	private EditText editText1;
	private ImageView attachImage;
	private ListView lv;
	private ChatListCursorAdapter cAdapter;
	
	private RelativeLayout r1, r2, r3, r4, r5;
	
	
	private DBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues content;
	
	String[] projection = new String[] {
			DBHelper.CHAT_KEY_ID,
			DBHelper.CHAT_KEY_MSGCONTENT,
            DBHelper.CHAT_KEY_CREATEDAT,
            DBHelper.CHAT_KEY_MSGSTATUS,
            DBHelper.CHAT_KEY_SENDER,
            DBHelper.CHAT_KEY_RECEIVER,
            DBHelper.CHAT_KEY_MESSAGEID,
            DBHelper.CHAT_KEY_CONTENTTYPE,
            DBHelper.CHAT_KEY_PICTUREBLOB
    };
	
	//String selection = Phone.NUMBER + "!='null'";
	//String selection = DBHelper.CHAT_KEY_RECEIVER + "==" + contactPhoneNumber;
	//String sortOrder = Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	
	
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private JSONArray output;
	private static final String CHAT_MODEL = "http://83.138.190.170/fuse/chat.php";
	
	JSONParser jParser1 = new JSONParser();
	private static final String SEND_NOTIFICATION = "http://83.138.190.170/push.php";
	JSONObject json1;
	
	
	private MyFuseService fs;
	String test = "verify";
	String nonSpacePhoneNumber;
	
	// Voice Recording
	private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String VIDEO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	private static final String AUDIO_RECORDER_FOLDER = "FuseMedia";
	private static final String VIDEO_RECORDER_FOLDER = "FuseMedia";
	
	
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.AMR_NB, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP3, AUDIO_RECORDER_FILE_EXT_3GP, VIDEO_RECORDER_FILE_EXT_MP4 };
	
	Button record_dialogButton;
	TextView start_dialogText;
	ProgressBar bar;
	
	Button useVoiceNoteBtn;
	Button playpausenote;
	Button cancelnote;
	String voiceNotePath;
	TextView startTimeNotetxt;
	TextView endTimeNotetxt;
	ProgressBar playBar;
	private MediaPlayer  mPlayer = null;
	String timeLength;
	int fileTimeLength = 0;
	
	String selectedMessage;
	
	
	// share contacts
	private static final int CONTACT_PICKER_RESULT = 1001;
	private String selectedContactName;
	private String selectedContactPhoneNumber;
	private String selectedContactPhoto;
	private String selectedContactEmail;
	
	
	//share location
	
	
	//start camera
	protected static final int CAMERA_REQUEST = 100;
	protected static final int GALLERY_PICTURE = 101;
	
	protected static final int VIDEO_REQUEST = 200;
	protected static final int GALLERY_VIDEO = 201;
	
	private Intent pictureActionIntent = null;
	private Intent videoActionIntent = null;
	
	SmileyClass smiley;
	
	byte[] img=null;

	TextView chatStatus;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		r1 = (RelativeLayout) findViewById(R.id.listform);
		r2 = (RelativeLayout) findViewById(R.id.footerform);
		r3 = (RelativeLayout) findViewById(R.id.form2);
		r4 = (RelativeLayout) findViewById(R.id.form3);
		
		r3.setVisibility(View.GONE);
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		
		smiley = new SmileyClass();
		
		attachImage = (ImageView) findViewById(R.id.additemimageView);
		
		Intent i = getIntent();
		name = i.getStringExtra("ChatName");
		contactPhoneNumber = i.getStringExtra("contactPhoneNumber");
		contactPhoneNumber = contactPhoneNumber.startsWith("+") ? contactPhoneNumber.substring(1) : contactPhoneNumber;
		this.getActionBar().setTitle(name);
		
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
		
		editText1 = (EditText) findViewById(R.id.addchateditText);
		lv = (ListView ) findViewById(R.id.chatlistView);
		lv.setDivider(null);
		
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	phoneNumber = pref.getString("FusePhoneNumber", null);
    	
    	SharedPreferences pref1 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	authKey = pref1.getString("authkey", null);
    	
    	chatKey = phoneNumber + "-" + contactPhoneNumber;
		
		Toast.makeText(getApplicationContext(), phoneNumber + " " + contactPhoneNumber,
                Toast.LENGTH_SHORT).show();
		
		mHelper = new DBHelper(this);
		dataBase = mHelper.getWritableDatabase();
		
		content = new ContentValues();
		
		//remove whitespaces
		nonSpacePhoneNumber = contactPhoneNumber.replaceAll("\\p{Z}","");
		
		String selection = DBHelper.CHAT_KEY_RECEIVER + "=" + nonSpacePhoneNumber;
		 ContentResolver cr = getContentResolver();
		 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, selection, null, null);
		 //Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, DBHelper.CHAT_KEY_RECEIVER + "=?", new String[] {contactPhoneNumber}, null);
		Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, null, null, null);
		//Cursor cursor = mHelper.getChatMessage(phoneNumber, nonSpacePhoneNumber);
		cAdapter = new ChatListCursorAdapter(this,cursor);
		
		lv.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
		
		//lv.setSelection(lv.getAdapter().getCount()-1);
		  //Ensures a loader is initialized and active.
		  //getLoaderManager().initLoader(0, null, this);
		getSupportLoaderManager().initLoader(0, null, ChatActivity.this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		  
		  editText1.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					messageMethod();
					return true;
				}
				return false;
			}
			  
		  });
		  
		  lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
				selectedMessage = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_MSGCONTENT));
				
				if (selectedMessage.length() > 3){
					String extension = selectedMessage.substring(selectedMessage.length() - 3);
					
					if (extension.equals("mp3")){
						final Dialog selectedNotedialog = new Dialog(ChatActivity.this);
						selectedNotedialog.setContentView(R.layout.play_stored_voicenote);
						selectedNotedialog.setTitle("Voice Note");
						Button bPlay = (Button)selectedNotedialog.findViewById(R.id.voicenoteplaybutton);
						Button bCancel = (Button) selectedNotedialog.findViewById(R.id.voicenotecancelbutton);
						
						bPlay.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								try{
										MediaPlayer mPlayer = new MediaPlayer();
								       
							       
							            mPlayer.setDataSource(selectedMessage);
							            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
							            mPlayer.prepare();
							            mPlayer.start();
							       
								}catch(Exception e){
									
								}
								
							}
							
						});
						
						bCancel.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								selectedNotedialog.dismiss();
							}
							
						});
						
						
						
						selectedNotedialog.show();
					}else if(extension.equals("jpg")){
						
					}
					
					
				}else{
					
				}
				
				
			}
			  
		  });
		  
		  
		  
		  
		 // bindService(new Intent(ChatActivity.this, ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
		  
		  bindService(new Intent(ChatActivity.this, MyFuseService.class), conn, Context.BIND_AUTO_CREATE);
    	
	}
	
	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_add_person:
	            Intent i = new Intent(ChatActivity.this, SelectContactsActivity.class);
	            startActivity(i);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public void sendMessage(View v){
		messageMethod();
	}
	
	public void showShare(View v){
		if (r3.isShown()){
			
			r3.setVisibility(View.GONE);
			r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
			r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
			attachImage.setImageResource(R.drawable.ic_action_new);
			editText1.clearFocus();
			editText1.setFocusableInTouchMode(true);
			editText1.setClickable(true);
			
		}else{
			
			r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 5.8f));
			r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 0.9f));
			r3.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 3.3f));
			r3.setVisibility(View.VISIBLE);
			r4.setVisibility(View.GONE);
			attachImage.setImageResource(R.drawable.ic_action_expand);
			editText1.clearFocus();
			editText1.setFocusable(false);
			editText1.setClickable(false);
			
		}
		
		
	}
	
	public void messageMethod(){
		message = editText1.getText().toString();
		if (message != null){
			insertToSQLite();
			new InsertChat().execute();
			new NotifyMe().execute();
			editText1.setText("");
	    	lv.setSelection(lv.getAdapter().getCount()-1);
		}
		
	}
	
	
	@SuppressLint("SimpleDateFormat")
	public void insertToSQLite(){
		if (message != null){
			
			Calendar cal = Calendar.getInstance();
		    System.out.println(cal.getTime());
		       
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    //System.out.println(sdf.format(cal.getTime()));
			
			content.put(DBHelper.CHAT_KEY_CID, "sender");
			content.put(DBHelper.CHAT_KEY_MESSAGEID, test);
			content.put(DBHelper.CHAT_KEY_CHATKEY, test);
			content.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
			content.put(DBHelper.CHAT_KEY_CREATEDAT, sdf.format(cal.getTime()));
			content.put(DBHelper.CHAT_KEY_SENDER, phoneNumber);
			content.put(DBHelper.CHAT_KEY_RECEIVER, nonSpacePhoneNumber);
			content.put(DBHelper.CHAT_KEY_CONTENTTYPE, "text");
			content.put(DBHelper.CHAT_KEY_MSGCONTENT, message);
			content.put(DBHelper.CHAT_KEY_RESOURCENAME, "");
			content.put(DBHelper.CHAT_KEY_MSGSTATUS, "w");
			
			getContentResolver().insert(ChatContentProvider.CONTENT_URI, content);
			//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
			dataBase.close();
			ChatMessage m = new ChatMessage(true, "");
		}
	}
	
	
	class NotifyMe extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("recipientnumber", nonSpacePhoneNumber));
				params.add(new BasicNameValuePair("message", message));
				params.add(new BasicNameValuePair("name", name));
				json1 = jParser1.makeHttpRequest(SEND_NOTIFICATION, "POST", params);
			}catch(Exception e){
				
			}
			return null;
		}
		
		protected void onPreExecute() {
			
		}
		
	}
	
	
	class InsertChat extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "sendmessage"));
				
				params.add(new BasicNameValuePair("chatkey", chatKey));
				params.add(new BasicNameValuePair("authkey", authKey));
				params.add(new BasicNameValuePair("sender", phoneNumber));
				params.add(new BasicNameValuePair("receiver", nonSpacePhoneNumber));
				params.add(new BasicNameValuePair("contenttype", "text"));
				params.add(new BasicNameValuePair("msgcontent", message));
				params.add(new BasicNameValuePair("msgstatus", "w"));
				params.add(new BasicNameValuePair("pictureblob", "0"));
				
				json = jParser.makeHttpRequest(CHAT_MODEL, "POST", params);
				
			}catch(Exception e){
				
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			try{
				//chatStatus.setText("...");
			}catch(Exception e){
				
			}
		}
		
		
		protected void onPostExecute(String file_url) {
			try{
				//chatStatus.setText("D");
			}catch(Exception e){
				
			}
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = ChatContentProvider.CONTENT_URI;
		//return new CursorLoader(this, uri, projection, DBHelper.CHAT_KEY_RECEIVER + "=?", new String[] {contactPhoneNumber}, null);
		String my_selection = DBHelper.CHAT_KEY_RECEIVER + "=?" + " OR "  + DBHelper.CHAT_KEY_SENDER + "=?";
		String [] my_selectionArgs = {contactPhoneNumber};
		String selection = DBHelper.CHAT_KEY_RECEIVER + "=" + nonSpacePhoneNumber;
		//return new CursorLoader(this, uri, projection, my_selection, my_selectionArgs, null);
		//return new CursorLoader(this, uri, projection, DBHelper.CHAT_KEY_RECEIVER + " =? " , new String[] { String.valueOf(nonSpacePhoneNumber) }, null);
		return new CursorLoader(this, uri, projection, my_selection, new String[] { String.valueOf(nonSpacePhoneNumber), String.valueOf(nonSpacePhoneNumber)}, null);
		
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
	
	
	private ServiceConnection conn = new ServiceConnection(){

		@SuppressLint("ShowToast")
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			fs = ((MyFuseService.MyBinder) service).getService();
			Toast.makeText(ChatActivity.this, "Connectedxx", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			fs = null;
		}
		
	};
	
	private class ChatListCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		
		private TextView chatText;
		private TextView chatTime;
		private ImageView chatImage;
	    private LinearLayout wrapper;
	    private LinearLayout wrapper1;
	    private LinearLayout wrapper2;
	    private LinearLayout wrapper3;
	    private Bitmap bitmap;
	    

		@SuppressWarnings("deprecation")
		public ChatListCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
			
		}
		
		

		@Override
		public void bindView(View view, Context arg1, Cursor cursor) {
			// TODO Auto-generated method stub
			
			
			chatText = (TextView) view.findViewById(R.id.comment);
			//chatTime = (TextView) view.findViewById(R.id.chattime);
			//chatStatus = (TextView)view.findViewById(R.id.chatstatus);
			
			wrapper = (LinearLayout) view.findViewById(R.id.wrapper);
			wrapper1 = (LinearLayout)view.findViewById(R.id.picturewrapper);
			wrapper2 = (LinearLayout)view.findViewById(R.id.voicenotewrapper);
			wrapper3 = (LinearLayout)view.findViewById(R.id.videowrapper);
			chatImage = (ImageView)view.findViewById(R.id.chatImage);
			//VideoView mVideoView = (VideoView)findViewById(R.id.videoView1);
			TextView t1 = (TextView)view.findViewById(R.id.wordcounttextView);
			
			String message = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_MSGCONTENT));
			String messageId = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_MESSAGEID));
			String time = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_CREATEDAT));
			String receiver = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_RECEIVER));
			String sender = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_SENDER));
			//String chatKey = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_CHATKEY));
			
			//String contentType = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_CONTENTTYPE));
			
			/*
			if (contentType.equals("text")){
				
			}else if(contentType.equals("image")){
				wrapper.setVisibility(View.GONE);
				wrapper2.setVisibility(View.GONE);
				wrapper1.setVisibility(View.VISIBLE);
				/*
				bitmap = BitmapFactory.decodeFile(message); 
                bitmap = Bitmap.createScaledBitmap(bitmap,100, 100, false);
                chatImage.setImageBitmap(bitmap);
              
				byte [] img;
				if(cursor!=null){
                    cursor.moveToFirst();
                    do{
                        img=cursor.getBlob(cursor.getColumnIndex(DBHelper.CHAT_KEY_PICTUREBLOB));
                       }while(cursor.moveToNext());
                }  
				img = cursor.getBlob(cursor.getColumnIndex(DBHelper.CHAT_KEY_PICTUREBLOB));
				Bitmap b1=BitmapFactory.decodeByteArray(img, 0, img.length);
				chatImage.setImageBitmap(b1);
                wrapper1.setGravity(Gravity.CENTER);
			}*/
			
			
			if (message.length() > 3){
				
				
						String extension = message.substring(message.length() - 3);
						
						if (extension.equals("jpg") || extension.equals("png")){
							//if (wrapper.isShown()){
								wrapper.setVisibility(View.GONE);
								wrapper2.setVisibility(View.GONE);
								wrapper1.setVisibility(View.VISIBLE);
								bitmap = BitmapFactory.decodeFile(message); 
				                bitmap = Bitmap.createScaledBitmap(bitmap,100, 100, false);
				                chatImage.setImageBitmap(bitmap);
				                wrapper1.setGravity(Gravity.CENTER);
							//}
							
						}else if(extension.equals("mp3")){
							wrapper.setVisibility(View.GONE);
							wrapper1.setVisibility(View.GONE);
							wrapper2.setVisibility(View.VISIBLE);
						}else if(extension.equals("mp4")){
							wrapper.setVisibility(View.GONE);
							wrapper1.setVisibility(View.GONE);
							wrapper2.setVisibility(View.GONE);
							wrapper3.setVisibility(View.VISIBLE);
							t1.setText(message);
							//Uri uri = Uri.parse(message);
		                    //mVideoView.setVideoURI(uri);
		                    //mVideoView.setMediaController(new MediaController(ChatActivity.this));
		                    //mVideoView.requestFocus();
		                    
							
						}else{
						
							//if (wrapper1.isShown()){
								wrapper1.setVisibility(View.GONE);
								wrapper.setVisibility(View.VISIBLE);
							//}
							String output = "verify";
							
							//String output = messageId.replaceAll("\\s+","");
							//boolean output = messageId.equals(tet);
							if (messageId == test){
							
								ChatMessage chat = new ChatMessage(false, "");
								chatText.setText(message);
								// convert long time to hour:minute
								Date d = null;  
						        String format = "yyyy-MM-dd HH:mm:ss";
						        try{
						             d = new SimpleDateFormat(format).parse(time);  
						        }catch(Exception e){
						            
						        }
								chatTime.setText(d.getHours() + ":" +d.getMinutes());
								chatTime.setTextColor(Color.parseColor("#33b5e5"));
								//chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
								wrapper.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
								wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
							}else{
								ChatMessage chat = new ChatMessage(true, "");
								chatText.setText(SmileyClass.getSmiledText(ChatActivity.this, message));
								//chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
								// convert long time to hour:minute
								Date d = null;  
						        String format = "yyyy-MM-dd HH:mm:ss";
						        try{
						             d = new SimpleDateFormat(format).parse(time);  
						        }catch(Exception e){
						            
						        }
								chatTime.setText(d.getHours() + ":" +d.getMinutes());
								chatTime.setTextColor(Color.parseColor("#33b5e5"));
								wrapper.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
								wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);			
							}
						}
				
				
				
						
			}else{
							//if (wrapper1.isShown()){
							wrapper1.setVisibility(View.GONE);
							wrapper2.setVisibility(View.GONE);
							wrapper.setVisibility(View.VISIBLE);
						//}
						String output = "verify";
						
						//String output = messageId.replaceAll("\\s+","");
						//boolean output = messageId.equals(tet);
						if (messageId == test){
							ChatMessage chat = new ChatMessage(false, "");
							chatText.setText(message);
							//chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
							// convert long time to hour:minute
							Date d = null;  
					        String format = "yyyy-MM-dd HH:mm:ss";
					        try{
					             d = new SimpleDateFormat(format).parse(time);  
					        }catch(Exception e){
					            
					        }
							chatTime.setText(d.getHours() + ":" +d.getMinutes());
							wrapper.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
							wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
						}else{
							ChatMessage chat = new ChatMessage(true, "");
							chatText.setText(SmileyClass.getSmiledText(ChatActivity.this, message));
							//chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
							wrapper.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
							wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);			
						}
			}
			
			
		
			
			
			/*
			boolean output1 = messageId.equals(test);
			if (output1){
			
				ChatMessage chat = new ChatMessage(false, "");
				chatText.setText(message);
				chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
				wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
			}else{
				ChatMessage chat = new ChatMessage(true, "");
				chatText.setText(message);
				chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
				wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);			
			}
			*/
			//chatTime.setText(messageId);
			
			//String test = chatText.getText().toString();
			
			
			/*
			if (test == "verify"){
				ChatMessage chat = new ChatMessage(true, "");
				chatText.setText(message);
				chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
				wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
			}else{
				ChatMessage chat = new ChatMessage(false, "");
				chatText.setText(message);
				chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
				wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
			}
			
			
			
			
			/*
			ChatMessage chat = new ChatMessage(false, "");
			chatText.setText(message);
			chatText.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
			wrapper.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
					*/
			
			
			
		}

		@Override
		public View newView(Context arg0, Cursor cursor, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.chatcustomlist, container, false);
			
			
			
			return view;
		}

	}
	
	@SuppressLint("ResourceAsColor") 
	public void startVoiceNote(View v){
		final Dialog dialog = new Dialog(ChatActivity.this);
		dialog.setContentView(R.layout.record_layout);
		dialog.setTitle("Voice Note");
		
		record_dialogButton = (Button) dialog.findViewById(R.id.recordpbutton);
		start_dialogText = (TextView) dialog.findViewById(R.id.startptextView);
		bar = (ProgressBar) dialog.findViewById(R.id.progressBar);
		bar.setMax(30);
		bar.setProgress(0);
		final RecordCounter timer = new RecordCounter(30000,1000);
		record_dialogButton.setOnClickListener(new OnClickListener() {
			@SuppressLint("ResourceAsColor") @Override
			public void onClick(View v) {
				String name = record_dialogButton.getText().toString();
				
				if (name.equals("Record")){
					
					timer.start();
					
					record_dialogButton.setText("Stop");
					record_dialogButton.setBackgroundResource(R.color.red);
					record_dialogButton.setTextColor(R.color.white);
					
					startRecording();
					
					
				}else if(name.equals("Stop")){
					timer.cancel();
					record_dialogButton.setText("Play");
					record_dialogButton.setBackgroundResource(R.color.green);
					record_dialogButton.setTextColor(R.color.black);
					
					stopRecording();
					
					
				}else if(name.equals("Play")){
					timeLength = start_dialogText.getText().toString();
					dialog.dismiss();
					final Dialog playdialog = new Dialog(ChatActivity.this);
					playdialog.setContentView(R.layout.record_play_layout);
					playdialog.setTitle("Play Voice Note");
					
					//  play / pause voice note
					useVoiceNoteBtn = (Button) playdialog.findViewById(R.id.usevoicenotebutton);
					playpausenote =  (Button) playdialog.findViewById(R.id.playnotebutton);
					cancelnote = (Button) playdialog.findViewById(R.id.cancelplaybutton);
					startTimeNotetxt = (TextView) playdialog.findViewById(R.id.playstarttimetextView);
					endTimeNotetxt = (TextView) playdialog.findViewById(R.id.endplaytextView);
					playBar = (ProgressBar) playdialog.findViewById(R.id.playprogressBar);
					endTimeNotetxt.setText(timeLength);
					
					 fileTimeLength = Integer.parseInt(timeLength.substring(timeLength.lastIndexOf(":") + 1).trim());
					 int playcountermax = fileTimeLength * 1000;
					 playBar.setMax(fileTimeLength);
					 playBar.setProgress(0);
					 final PlayCounter timer = new PlayCounter(playcountermax,1000);
					playpausenote.setOnClickListener(new OnClickListener(){
							
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							String name = playpausenote.getText().toString();
							if(name.equals("Play")){
								timer.start();
								startPlaying();
								playpausenote.setText("Pause");
								playpausenote.setBackgroundResource(R.color.green);
							}else if(name.equals("Pause")){
								timer.cancel();
								pausePlayer();
								playpausenote.setText("Play");
								playpausenote.setBackgroundResource(R.color.myblue);
							}
						}
						
					});
					
					useVoiceNoteBtn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (voiceNotePath != null){
								
								Date d = new Date();
								
								content.put(DBHelper.CHAT_KEY_CID, "sender");
								content.put(DBHelper.CHAT_KEY_MESSAGEID, test);
								content.put(DBHelper.CHAT_KEY_CHATKEY, test);
								content.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
								content.put(DBHelper.CHAT_KEY_CREATEDAT, d.toString());
								content.put(DBHelper.CHAT_KEY_SENDER, phoneNumber);
								content.put(DBHelper.CHAT_KEY_RECEIVER, contactPhoneNumber);
								content.put(DBHelper.CHAT_KEY_CONTENTTYPE, "image");
								content.put(DBHelper.CHAT_KEY_MSGCONTENT, voiceNotePath);
								content.put(DBHelper.CHAT_KEY_RESOURCENAME, "");
								content.put(DBHelper.CHAT_KEY_MSGSTATUS, "w");
								
								getContentResolver().insert(ChatContentProvider.CONTENT_URI, content);
								//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
								dataBase.close();
								ChatMessage m = new ChatMessage(true, "");
							}
							playdialog.dismiss();
							r3.setVisibility(View.GONE);
							r4.setVisibility(View.GONE);
							r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
							r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
							attachImage.setImageResource(R.drawable.ic_action_new);
							editText1.clearFocus();
							editText1.setFocusableInTouchMode(true);
							editText1.setClickable(true);
						}
						
					});
					
					
					cancelnote.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							playdialog.dismiss();
						}
						
					});
					
					playdialog.show();
				}
				
			}
		});
		
		Button cancel_dialogButton = (Button) dialog.findViewById(R.id.cancelpbutton);
		cancel_dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
		
		
	}
	
	class RecordCounter extends CountDownTimer{
		 
        public RecordCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
 
        @Override
        public void onFinish() {
            System.out.println("Timer Completed.");
            start_dialogText.setText("00:30");
        }
 
        @Override
        public void onTick(long millisUntilFinished) {
        	bar.setProgress((int) (31- millisUntilFinished/1000));
        	start_dialogText.setText("00:" + (31- millisUntilFinished/1000)+"" );
            System.out.println("Timer  : " + (millisUntilFinished/1000));
        }
    }
	
	class PlayCounter extends CountDownTimer{

		public PlayCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			startTimeNotetxt.setText(timeLength);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			playBar.setProgress((int) ((fileTimeLength + 1) - millisUntilFinished/1000));
			int input = (int) ((fileTimeLength + 1) - millisUntilFinished/1000);
			startTimeNotetxt.setText("00:" + String.valueOf(input));
		}
		
	}
	

	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setMaxDuration(30000);
		recorder.setOnInfoListener(new MediaRecorder.OnInfoListener(){

			@Override
			public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if (arg1 == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
						arg0.stop();
						record_dialogButton.setText("Play");
			      }
				
				
			}
			
		});

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		voiceNotePath = getFilename();
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopRecording() {
		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}
	}
	
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
	}
	
	private String getVideoFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, VIDEO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[2]);
	}
	
	private void startPlaying() {
        mPlayer = new MediaPlayer();
       
        try {
            mPlayer.setDataSource(voiceNotePath);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }
    }
	
	public void pausePlayer(){
		if (mPlayer.isPlaying()){
			mPlayer.pause();
		}
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(ChatActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(ChatActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};

	public void startContactPicker(View v){
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}
	
	public void shareLocation(View v){
		Intent i = new Intent(ChatActivity.this, LocationAnotherActivity.class);
		startActivity(i);
	}
	
	public void startCamera(View v){
		//Intent pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(pictureActionIntent,CAMERA_REQUEST);
		startPictureDialog();
	}
	
	public void startVideoPicker(View v){
		//Intent pictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        //pictureActionIntent.setType("image/*");
        //pictureActionIntent.putExtra("return-data", true);
        //startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
		startVideoDialog();
	}
	
	
	public void addSmiley(View v){
		
		r3.setVisibility(View.GONE);
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 5.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 0.9f));
		r4.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 3.3f));
		r4.setVisibility(View.VISIBLE);
		attachImage.setImageResource(R.drawable.ic_action_new);
		editText1.setFocusable(true);
		editText1.setFocusableInTouchMode(true);
		editText1.setClickable(true);
		
	}
	
	public void addHappy(View v){
		editText1.append(":-)");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	public void addSad(View v){
		editText1.append(":-(");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	public void addWinking(View v){
		editText1.append(";-)");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	public void addTongueStickingOut(View v){
		editText1.append(":-P");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	public void addYelling(View v){
		editText1.append(":-*");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	public void addSurprised(View v){
		editText1.append("=-O");
		r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
		r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
		r3.setVisibility(View.GONE);
		r4.setVisibility(View.GONE);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case CONTACT_PICKER_RESULT:
                // handle contact results
            	Uri contactData = data.getData();
                Cursor c =  managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                	String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    
                    if (hasPhone.equalsIgnoreCase("1")) {
                    	Cursor phones = getContentResolver().query( 
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
                                null, null);
                       phones.moveToFirst();
                       selectedContactPhoneNumber = phones.getString(phones.getColumnIndex("data1"));
                    }
                    
                    selectedContactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    selectedContactPhoto = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));


                }
                
                //Toast.makeText(getApplicationContext(), selectedContactName + " " + selectedContactPhoneNumber,Toast.LENGTH_SHORT).show();
                editText1.setText(selectedContactName + "\t" + selectedContactPhoneNumber);
                r3.setVisibility(View.GONE);
                r4.setVisibility(View.GONE);
    			r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
    			r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
    			attachImage.setImageResource(R.drawable.ic_action_new);
    			editText1.clearFocus();
    			editText1.setFocusableInTouchMode(true);
    			editText1.setClickable(true);
                break;
            }

        } 
		
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && null != data) {
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Uri uri = getImageUri(getApplicationContext(), bitmap);
			String fileSrc = getPhotoPath(uri);
			//String fileSrc = (String) data.getExtras().get("data");
			
			
			if (fileSrc != null){
				
				Date d = new Date();
				
				content.put(DBHelper.CHAT_KEY_CID, "sender");
				content.put(DBHelper.CHAT_KEY_MESSAGEID, test);
				content.put(DBHelper.CHAT_KEY_CHATKEY, test);
				content.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
				content.put(DBHelper.CHAT_KEY_CREATEDAT, d.toString());
				content.put(DBHelper.CHAT_KEY_SENDER, phoneNumber);
				content.put(DBHelper.CHAT_KEY_RECEIVER, contactPhoneNumber);
				content.put(DBHelper.CHAT_KEY_CONTENTTYPE, "image");
				content.put(DBHelper.CHAT_KEY_MSGCONTENT, fileSrc);
				content.put(DBHelper.CHAT_KEY_RESOURCENAME, "");
				content.put(DBHelper.CHAT_KEY_MSGSTATUS, "w");
				
				getContentResolver().insert(ChatContentProvider.CONTENT_URI, content);
				//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
				dataBase.close();
				ChatMessage m = new ChatMessage(true, "");
			}
			
			r3.setVisibility(View.GONE);
			r4.setVisibility(View.GONE);
			r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
			r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
			attachImage.setImageResource(R.drawable.ic_action_new);
			editText1.clearFocus();
			editText1.setFocusableInTouchMode(true);
			editText1.setClickable(true);
			
			/*
			bitmap = (Bitmap) data.getExtras().get("data");

            bitmap = Bitmap.createScaledBitmap(bitmap, 100,100, false);
            // update the image view with the bitmap
            profilepix.setImageBitmap(bitmap);
            */
		}
		
		if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK && null != data) {
			
			Cursor cursor = getContentResolver().query(data.getData(),null, null, null, null);
			if (cursor != null){
				cursor.moveToFirst();
				
				int idx = cursor.getColumnIndex(ImageColumns.DATA);
				String fileSrc = cursor.getString(idx);
				
				if (fileSrc != null){
					/*
					Uri uri = Uri.parse(fileSrc);
					try{
						Bitmap b =BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						b.compress(Bitmap.CompressFormat.PNG, 100, bos);
						
						img = bos.toByteArray();
						
					}catch(Exception e){
						
					}*/
					
					
					Date d = new Date();
					
					content.put(DBHelper.CHAT_KEY_CID, "sender");
					content.put(DBHelper.CHAT_KEY_MESSAGEID, test);
					content.put(DBHelper.CHAT_KEY_CHATKEY, test);
					content.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
					content.put(DBHelper.CHAT_KEY_CREATEDAT, d.toString());
					content.put(DBHelper.CHAT_KEY_SENDER, phoneNumber);
					content.put(DBHelper.CHAT_KEY_RECEIVER, contactPhoneNumber);
					content.put(DBHelper.CHAT_KEY_CONTENTTYPE, "image");
					content.put(DBHelper.CHAT_KEY_MSGCONTENT, fileSrc);
					content.put(DBHelper.CHAT_KEY_RESOURCENAME, "");
					content.put(DBHelper.CHAT_KEY_MSGSTATUS, "w");
					//content.put(DBHelper.CHAT_KEY_PICTUREBLOB, img);
					
					getContentResolver().insert(ChatContentProvider.CONTENT_URI, content);
					//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
					dataBase.close();
					ChatMessage m = new ChatMessage(true, "");
				}
				
				r3.setVisibility(View.GONE);
				r4.setVisibility(View.GONE);
				r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
				r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
				attachImage.setImageResource(R.drawable.ic_action_new);
				editText1.clearFocus();
				editText1.setFocusableInTouchMode(true);
				editText1.setClickable(true);
			}
			
			/*
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
            }*/
		}
		
		if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && null != data) {
			/*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			Uri uri = getImageUri(getApplicationContext(), bitmap);
			String fileSrc = getPhotoPath(uri);*/
			//String fileSrc = (String) data.getExtras().get("data");
			
			Uri uri = data.getData();
			String fileSrc = getPhotoPath(uri);
		
			Toast.makeText(ChatActivity.this,
				    uri.getPath().toString(),
				    Toast.LENGTH_LONG)
				    .show();
			
			if (fileSrc != null){
				
				Date d = new Date();
				
				content.put(DBHelper.CHAT_KEY_CID, "sender");
				content.put(DBHelper.CHAT_KEY_MESSAGEID, test);
				content.put(DBHelper.CHAT_KEY_CHATKEY, test);
				content.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
				content.put(DBHelper.CHAT_KEY_CREATEDAT, d.toString());
				content.put(DBHelper.CHAT_KEY_SENDER, phoneNumber);
				content.put(DBHelper.CHAT_KEY_RECEIVER, contactPhoneNumber);
				content.put(DBHelper.CHAT_KEY_CONTENTTYPE, "video");
				content.put(DBHelper.CHAT_KEY_MSGCONTENT, uri.getPath());
				System.out.println(uri.getPath());
				content.put(DBHelper.CHAT_KEY_RESOURCENAME, "");
				content.put(DBHelper.CHAT_KEY_MSGSTATUS, "w");
				
				getContentResolver().insert(ChatContentProvider.CONTENT_URI, content);
				//dataBase.insert(DBHelper.CHAT_TABLE_NAME, null, content);
				dataBase.close();
				ChatMessage m = new ChatMessage(true, "");
			}
			
			r3.setVisibility(View.GONE);
			r4.setVisibility(View.GONE);
			r1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8.8f));
			r2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 1.2f));
			attachImage.setImageResource(R.drawable.ic_action_new);
			editText1.clearFocus();
			editText1.setFocusableInTouchMode(true);
			editText1.setClickable(true);
			
			/*
			bitmap = (Bitmap) data.getExtras().get("data");

            bitmap = Bitmap.createScaledBitmap(bitmap, 100,100, false);
            // update the image view with the bitmap
            profilepix.setImageBitmap(bitmap);
            */
		}
		
		if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled",
                    Toast.LENGTH_SHORT).show();
        }
		
		
	}
	
	//get photo uri
	private Uri getImageUri(Context incontext, Bitmap inImage){
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(incontext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}
	
	//get photo path from uri
	private String getPhotoPath(Uri uri){
		String [] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public void startPictureDialog(){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Pictures Option");
	    myAlertDialog.setMessage("Take or select Picture");
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
	
	private void startVideoDialog(){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Video Option");
	    myAlertDialog.setMessage("Take or Select a Video");
	    
	    myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                videoActionIntent = new Intent(
                        Intent.ACTION_GET_CONTENT, null);
                videoActionIntent.setType("image/*");
                videoActionIntent.putExtra("return-data", true);
                startActivityForResult(videoActionIntent,
                        GALLERY_PICTURE);
            }
	    });
	    
	    myAlertDialog.setNegativeButton("Video", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	File mediaFile = new File(getVideoFilename());
                videoActionIntent = new Intent(
                        android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                Uri videoUri = Uri.fromFile(mediaFile);
                videoActionIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(videoActionIntent,
                        VIDEO_REQUEST);

            }
	    });

	    myAlertDialog.show();
	    
		
	}

}
