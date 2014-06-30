package com.vas2nets.fuse.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class SelectContactsActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{
	
	private ContactsSelectCursorAdapter cAdapter;
	ListView mContactsList;
	
	String[] projection = new String[] {
			Phone._ID,
			Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.PHOTO_URI
    };
	
	String selection = Phone.NUMBER + "!='null'";
	String sortOrder = Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	
	private List<String> groupList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contacts);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("Group Chat");
		
		mContactsList = (ListView) findViewById(R.id.selectcontact_listview);
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection, null, sortOrder);
		
		cAdapter = new ContactsSelectCursorAdapter(this,cursor);
		mContactsList.setAdapter(cAdapter);
		 cAdapter.notifyDataSetChanged();
		 mContactsList.setTextFilterEnabled(true);
		 
		 getSupportLoaderManager().initLoader(0, null, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_contacts, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_done:
	           item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	           StringBuffer responseText = new StringBuffer();
	           responseText.append("The following contacts were selected...\n");
	           for (String num : groupList){
	        	   responseText.append("\n" + num);
	           }
	           Toast.makeText(getApplicationContext(),
	        		      responseText, Toast.LENGTH_LONG).show();
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = Phone.CONTENT_URI;
		// return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
		return new CursorLoader(this, uri, projection, selection, null, sortOrder);
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
	
	private class ContactsSelectCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		

		public ContactsSelectCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView nametxt = (TextView) arg0.findViewById(R.id.contactnametextView);
			ImageView contactPix = (ImageView) arg0.findViewById(R.id.contactimageView);
			CheckBox check = (CheckBox) arg0.findViewById(R.id.selectcheckBox);
			
			String contactName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
			String photo = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI));
			String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			
			nametxt.setText(contactName);
		     
		     if(photo == null){
		    	 contactPix.setImageResource(R.drawable.noface);
		     }else{
		    	// Bitmap bm = BitmapFactory.decodeFile(photo);
		    	 contactPix.setImageURI(Uri.parse(photo));
		     }
		     
		     check.setTag(number);
		     check.setOnCheckedChangeListener(null);
		     check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					String num = (String) arg0.getTag();
					if (arg1){
						
						groupList.add(num);
					}else{
						groupList.remove(num);
					}
				}
		    	 
		     });
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.select_custom_contact, container, false);
			return view;
		}

	}

}
