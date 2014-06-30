package come.vas2nets.fuse.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.json.JSONParser;
import com.vas2nets.fuse.sip.chat.ChatContentProvider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class MyFuseService extends Service {
	
	private Timer timer = new Timer();
	final Handler handler = new Handler();
	private static final long UPDATE_INTERNAL = 2000;
	private final IBinder mBinder = new MyBinder();
	String output;
	JSONObject json;
	JSONArray sample;
	
	private DBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues values;
	
	JSONParser jParser = new JSONParser();
	//private static final String GET_CHAT = "http://83.138.190.170/fuse/chat.php";
	private static final String GET_CHAT = "http://83.138.190.170/fuse/chat.php";
	
	private static List<String> lastIdHold = new ArrayList<String>();
	private String lastInserted = null;
	
	private String phoneNumber;
	
	
	private String id;
	private String messageid;
	private String chatKey;
	private String authKey;
	private String createdAt;
	private String sender;
	private String receiver;
	private String contenttype;
	private String msgContent;
	private String resourceName;
	private String msgStatus;

	public class MyBinder extends Binder{
		public MyFuseService getService(){
			return MyFuseService.this;
		}
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public void onCreate(){
		super.onCreate();
		
		mHelper = new DBHelper(getApplicationContext());
		SharedPreferences pref1 = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	lastInserted = pref1.getString("kdiyan", null);
    	
    	SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
    	phoneNumber = pref.getString("FusePhoneNumber", null);
    	
    	TimerTask doAsynchronousTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						new GetData().execute();
						
						System.out.println("Do again!!!");
						//System.out.println(id);
						//System.out.println(lastInserted);
						//ContentValues c = new ContentValues();
						//c.put(DBHelper.CHAT_KEY_MSGCONTENT, "dat");
						//getContentResolver().insert(ChatContentProvider.CONTENT_URI, c);
					}
					
				});
			}
    		
    	};
    	timer.schedule(doAsynchronousTask, 0, UPDATE_INTERNAL);
		
	}
	
	class GetData extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				
				if (lastInserted == null){
					lastInserted = "0";
				}
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "receivermsg"));
				params.add(new BasicNameValuePair("receiver", phoneNumber));
				params.add(new BasicNameValuePair("id", lastInserted));
				json = jParser.makeHttpRequest(GET_CHAT, "POST", params);
				
				
			}catch(Exception e){
				System.out.println(e.getMessage().toString());
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			try{
				
				sample = json.getJSONArray("response");
				
				for(int i = 0; i < sample.length(); i++){
					JSONObject c = sample.getJSONObject(i);
					id = c.getString("id");
					//messageid = c.getString("messageid");
					//chatKey = c.getString("chatkey");
					//authKey = c.getString("authkey");
					createdAt = c.getString("createdat");
					sender = c.getString("sender");
					receiver = c.getString("receiver");
					//contenttype = c.getString("contenttype");
					msgContent = c.getString("msgcontent");
					//resourceName = c.getString("resourcename");
					msgStatus = c.getString("msgstatus");
					
					System.out.println(msgContent);
					
					values = new ContentValues();
					values.put(DBHelper.CHAT_KEY_CID, id);
					values.put(DBHelper.CHAT_KEY_CHATKEY, chatKey);
					values.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
					values.put(DBHelper.CHAT_KEY_CREATEDAT, createdAt);
					values.put(DBHelper.CHAT_KEY_SENDER, sender);
					
					values.put(DBHelper.CHAT_KEY_RECEIVER, receiver);
					values.put(DBHelper.CHAT_KEY_CONTENTTYPE, contenttype);
					values.put(DBHelper.CHAT_KEY_MSGCONTENT, msgContent);
					values.put(DBHelper.CHAT_KEY_AUTHKEY, authKey);
					values.put(DBHelper.CHAT_KEY_CREATEDAT, createdAt);
					values.put(DBHelper.CHAT_KEY_SENDER, sender);
					getContentResolver().insert(ChatContentProvider.CONTENT_URI, values);
					System.out.println(id);
					System.out.println(msgContent);
					lastIdHold.add(id); //holds chatid for add chat record
					lastInserted = lastIdHold.get(lastIdHold.size()-1);
					SharedPreferences pref2 = getApplicationContext().getSharedPreferences("FusePreferences", 0); // 0 - for private mode
					Editor editor = pref2.edit();
					editor.putString("kdiyan", lastInserted);
					editor.commit(); // commit changes
					System.out.println(lastInserted);
				}
				//dataBase.close();
			}catch(Exception e){
				
			}
		}

	}
	
	 @Override
	  public void onDestroy() {
	    super.onDestroy();
	    //Log.i(TAG, "Service destroying");
	     
	    timer.cancel();
	    timer = null;
	  }

}
