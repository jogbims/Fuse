package com.vas2nets.fuse;



import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csipsimple.api.SipConfigManager;
import com.csipsimple.api.SipManager;
import com.csipsimple.api.SipProfile;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.db.ActiveSocialDB;
import com.vas2nets.fuse.db.SocialDBHelper;
import com.vas2nets.fuse.home.FragmentHomeSwipe;
import com.vas2nets.fuse.navigation.NavDrawerItem;
import com.vas2nets.fuse.navigation.NavDrawerListAdapter;
import com.vas2nets.fuse.profile.FragmentProfile;
import com.vas2nets.fuse.social.core.MySocialService;
import com.vas2nets.fuse.social.facebook.FragmentFacebookSwipe;
import com.vas2nets.fuse.social.instagram.FragmentInstagramSwipe;
import com.vas2nets.fuse.social.linkedin.FragmentLinkedInSwipe;
import com.vas2nets.fuse.social.twitter.FragmentTwitterSwipe;

public class MainActivity extends FragmentActivity  {
	
	private MySocialService fs;
	
	private Timer timer = new Timer();
	final Handler handler = new Handler();
	private static final long UPDATE_INTERNAL = 20000;
	
	private SocialDBHelper mHelper;
	private SQLiteDatabase dataBase;
	private ContentValues content;
	
	SocialAuthAdapter s_adapter;
	List<Feed> feedList;
	
	String[] projection = new String[] {
			SocialDBHelper.SOCIAL_KEY_ID,
            SocialDBHelper.SOCIAL_KEY_MESSAGE,
            SocialDBHelper.SOCIAL_KEY_DATE,
            SocialDBHelper.SOCIAL_KEY_PROVIDER,
            SocialDBHelper.SOCIAL_KEY_READ
    };
	
	ActiveSocialDB db;
	private List<String> allProviders;
	
	//sip
	 private static final String THIS_FILE = "SampleCSipSimpleAppActivity";
	 private static final String SAMPLE_ALREADY_SETUP = "sample_already_setup";
	 private long existingProfileId = SipProfile.INVALID_ID;
	
	
	private String[] menus;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    
    TextView commentTxtView;
	ImageView shareButton;
    
	
	 
	 
	// slide menu items
	    private String[] navMenuTitles;
	    private TypedArray navMenuIcons;
	 
	    private ArrayList<NavDrawerItem> navDrawerItems;
	    private NavDrawerListAdapter adapter;
	    
  
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new ActiveSocialDB(this);
		allProviders = new ArrayList<String>();
		allProviders = db.getAllProviders();
		StringBuffer responseText = new StringBuffer();
		responseText.append("Logged in social networks...\n");
		for (String provider : allProviders){
			 responseText.append("\n" + provider);
		}
		//Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
		//social intializer
		//socialAuthInitializer();
		
		//sip initialization

	      // Retrieve private preferences
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      boolean alreadySetup = prefs.getBoolean(SAMPLE_ALREADY_SETUP, false);
      if(!alreadySetup) {
          // Activate debugging .. here can come various other options
          // One can also decide to reuse csipsimple activities to setup config
          SipConfigManager.setPreferenceStringValue(this, SipConfigManager.LOG_LEVEL, "4");
      }
      
      // Bind view buttons
     // ((Button) findViewById(R.id.start_btn)).setOnClickListener(this);
      //((Button) findViewById(R.id.save_acc_btn)).setOnClickListener(this);
      
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
              Log.e(THIS_FILE, "Some problem occured while accessing cursor", e);
          }finally {
              c.close();
          }
          
      }
		
		//end of initialization
      
      
      //setting sip profile
      
      SharedPreferences pref = getApplicationContext().getSharedPreferences("FusePreferences", 0);
  	  String sipID = pref.getString("FuseSipID", null);
     // String sipID = pref.getString("FusePhoneNumber", null);
      
      
      String pwd =  "vas";
      //String pwd = "password";
      //String pwd =  "";
      //String fullUser = "gbolaga@sip.linphone.org";
      String fullUser = sipID + "@197.253.10.26";
      //String fullUser = "29@197.253.10.26";
      
      String[] splitUser = fullUser.split("@");
      
      String error = getValidAccountFieldsError();
      if(TextUtils.isEmpty(error)) {
          
          // We do some VERY basic thing here (minimal), a real app should probably manage input differently
          SipProfile builtProfile = new SipProfile();
          builtProfile.display_name = "Fuse";
          builtProfile.id = existingProfileId;
          builtProfile.acc_id = "<sip:"+fullUser+">";
          builtProfile.reg_uri = "sip:"+splitUser[1];
          builtProfile.realm = "*";
          builtProfile.username = splitUser[0];
          builtProfile.data = pwd;
          builtProfile.proxies = new String[] {"sip:"+splitUser[1]};
          
          ContentValues builtValues = builtProfile.getDbContentValues();
          
          if(existingProfileId != SipProfile.INVALID_ID) {
              getContentResolver().update(ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE, existingProfileId), builtValues, null, null);
          }else {
              Uri savedUri = getContentResolver().insert(SipProfile.ACCOUNT_URI, builtValues);
              if(savedUri != null) {
                  existingProfileId = ContentUris.parseId(savedUri);
              }
          }
      }
      
      
      Intent it = new Intent(SipManager.INTENT_SIP_SERVICE);
      startService(it);
      
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_3G_IN, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_3G_OUT, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_EDGE_IN, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_EDGE_OUT, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_GPRS_IN, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_GPRS_OUT, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_OTHER_IN, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_OTHER_OUT, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_WIFI_IN, true);
      SipConfigManager.setPreferenceBooleanValue(MainActivity.this, SipConfigManager.USE_WIFI_OUT, true);
      
      //end sip profile
		
		
		// initializeManager();
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		mTitle = "Fuse";
		menus = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
        
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Facebook
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true, "17"));
        // Twitter
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true, "22"));
        // LinkedIn
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, 1), true, "4"));
        // instagram
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1), true, "10"));
        //profile
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        
        // Recycle the typed array
        navMenuIcons.recycle();
        
	
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        
        
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menus));
        
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
 
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
               
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle);
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        if (savedInstanceState == null) {
            // on first time display view for first nav item
        	selectItem(0);
        }
        
	    
       // bindService(new Intent(MainActivity.this, MySocialService.class), conn, Context.BIND_AUTO_CREATE);
	}
	
	
	
	/**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	
    	switch (position) {
        case 0:
            ft.replace(R.id.content_frame, new FragmentHomeSwipe());
            break;
        case 1:
            ft.replace(R.id.content_frame, new FragmentFacebookSwipe());
           
            break;
        case 2:
            ft.replace(R.id.content_frame, new FragmentTwitterSwipe());
            break;
        case 3:
            ft.replace(R.id.content_frame, new FragmentLinkedInSwipe());
            break;
        case 4:
        	ft.replace(R.id.content_frame, new FragmentInstagramSwipe());
        	break;
        case 5:
        	ft.replace(R.id.content_frame, new FragmentProfile());
        }
        ft.commit();
 
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(menus[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
    	if (item.getItemId() == R.id.action_edit){
    		/*
    		Intent i = new Intent(this, ShareActivity.class);
    		startActivity(i);
    		*/
    		return true;
    	}
    	
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
 
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @SuppressWarnings("rawtypes")
		@Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    
    
   
    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        //initializeManager();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	//initializeManager();
    }
 


    private String getValidAccountFieldsError() {
        String pwd =  "password";
        //String pwd =  "";
        String fullUser = "psalmsin@sip.linphone.org";
        //String fullUser = "20000@197.253.10.26";
        String[] splitUser = fullUser.split("@");

        if(TextUtils.isEmpty(fullUser)) {
            return "Empty user";
        }
        if(TextUtils.isEmpty(pwd)) {
            return "Empty password";
        }
        if(splitUser.length != 2) {
            return "Invaid user, should be user@domain";
        }
        return "";
	}
    
   
    class InsertIntoDB extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			// new InsertIntoDB().execute();
			return null;
		}

    }
   
}
