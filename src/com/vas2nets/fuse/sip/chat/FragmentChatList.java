package com.vas2nets.fuse.sip.chat;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.db.DBHelper;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */

public class FragmentChatList extends Fragment implements LoaderCallbacks<Cursor>{
	
	String[] projection = new String[] {
			DBHelper.CHAT_KEY_ID,
			DBHelper.CHAT_KEY_MSGCONTENT,
            DBHelper.CHAT_KEY_CREATEDAT,
            DBHelper.CHAT_KEY_SENDER,
            DBHelper.CHAT_KEY_RECEIVER
    };
	
	private ChatListCursorAdapter cAdapter;
	private ListView lv;
	private TextView emptyTxt;
	String selection;
	
	DBHelper db;
	

	public FragmentChatList() {
		// Required empty public constructor
	}
	
	@SuppressLint("CutPasteId")
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		lv = (ListView ) getActivity().findViewById(R.id.mychatlist_listview);
		emptyTxt = (TextView) getActivity().findViewById(R.id.emptyListElem);
		
		
		
		
		ContentResolver cr = getActivity().getContentResolver();	
		//Cursor cursor = cr.query(ChatContentProvider.CONTENT_URI, projection, null, null, null);
		//Cursor cursor = DBHelper.fetchActiveChat();
		db = new DBHelper(getActivity());
		Cursor cursor = db.getActiveChat();
		
		if (cursor.getCount() > 0){
			
		}else{
			emptyTxt.setVisibility(View.VISIBLE);
			emptyTxt.setText("No Active Chat");
			View empty = getActivity().findViewById(R.id.emptyListElem);  
			empty.setVisibility(View.VISIBLE);
			lv.setEmptyView(empty);
		}
		cAdapter = new ChatListCursorAdapter(getActivity(),cursor);
		lv.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
		
		//lv.setSelection(lv.getAdapter().getCount()-1);
		  //Ensures a loader is initialized and active.
		  getLoaderManager().initLoader(0, null, this);
		// getActivity().getSupportLoaderManager().initLoader(0, null, this);
		  
		  lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String name = null;
				String phoneNumber = null;
				
				Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
				String receiver = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_RECEIVER));
				phoneNumber = receiver;
				
				boolean numberExist = contactExists(getActivity(), receiver);
				if (numberExist){
					// define the columns I want the query to return
					String[] projection = new String[] {
					        ContactsContract.PhoneLookup.DISPLAY_NAME,
					        ContactsContract.PhoneLookup._ID,
					        ContactsContract.PhoneLookup.PHOTO_URI};
					// encode the phone number and build the filter URI
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(receiver));
					// query time
					Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
					String contactId = null;
					
					
					if (cur.moveToFirst()) {
						// Get values from contacts database:
					    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
					    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));  
					}
					
					
				}else{
					
					String newNumber = remove234(receiver);
					boolean newNumberExist = contactExists(getActivity(), newNumber);
					if (newNumberExist){
						// define the columns I want the query to return
						String[] projection = new String[] {
						        ContactsContract.PhoneLookup.DISPLAY_NAME,
						        ContactsContract.PhoneLookup._ID,
						        ContactsContract.PhoneLookup.PHOTO_URI};
						// encode the phone number and build the filter URI
						Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(receiver));
						// query time
						Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
						String contactId = null;
						if (cur.moveToFirst()) {
							// Get values from contacts database:
						    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
						    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
						    
						}
						
					}
					
				}
				
				Toast.makeText(getActivity(),
					    name,
					    Toast.LENGTH_LONG)
					    .show();
				
				Intent i = new Intent(getActivity(), ChatActivity.class);
				i.putExtra("ChatName", name);
				i.putExtra("contactPhoneNumber", phoneNumber);
			    startActivity(i);
				
			}
			  
		  });

		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_fragment_chat_list, container, false);
		
		
		return view;
	}
	
	private class ChatListCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		
		private TextView chatName;
		private TextView chatMessage;
		private TextView chatTime;
		private ImageView chatImage;

		@SuppressWarnings("deprecation")
		public ChatListCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			chatName = (TextView) view.findViewById(R.id.chatlistnametextView);
			chatMessage = (TextView) view.findViewById(R.id.chatlistmessagetextView);
			chatTime = (TextView) view.findViewById(R.id.chatlisttimetextView);
			chatImage = (ImageView)view.findViewById(R.id.chatlistimageView);
			
			String message = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_MSGCONTENT));
			String time = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_CREATEDAT));
			String receiver = cursor.getString(cursor.getColumnIndex(DBHelper.CHAT_KEY_RECEIVER));
			chatMessage.setText(message);
			
			// convert long time to hour:minute
			Date d = null;  
	        String format = "yyyy-MM-dd HH:mm:ss";
	        try{
	             d = new SimpleDateFormat(format).parse(time);  
	        }catch(Exception e){
	            
	        }
			chatTime.setText(d.getHours() + ":" +d.getMinutes());
			
			boolean numberExist = contactExists(getActivity(), receiver);
			if (numberExist){
				// define the columns I want the query to return
				String[] projection = new String[] {
				        ContactsContract.PhoneLookup.DISPLAY_NAME,
				        ContactsContract.PhoneLookup._ID,
				        ContactsContract.PhoneLookup.PHOTO_URI};
				// encode the phone number and build the filter URI
				Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(receiver));
				// query time
				Cursor cur = context.getContentResolver().query(contactUri, projection, null, null, null);
				String contactId = null;
				String name = null;
				String photo = null;
				//String photo = null;
				if (cur.moveToFirst()) {
					// Get values from contacts database:
				    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
				    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				    photo = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));

				}
				
				chatName.setText(name);
				if(photo == null){
			    	 chatImage.setImageResource(R.drawable.noface);
			     }else{
			    	 //Bitmap bm = BitmapFactory.decodeFile(photo);
				     chatImage.setImageURI(Uri.parse(photo));
			     }
			}else{
				
				String newNumber = remove234(receiver);
				boolean newNumberExist = contactExists(getActivity(), newNumber);
				if (newNumberExist){
					// define the columns I want the query to return
					String[] projection = new String[] {
					        ContactsContract.PhoneLookup.DISPLAY_NAME,
					        ContactsContract.PhoneLookup._ID,
					        ContactsContract.PhoneLookup.PHOTO_URI};
					// encode the phone number and build the filter URI
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(receiver));
					// query time
					Cursor cur = context.getContentResolver().query(contactUri, projection, null, null, null);
					String contactId = null;
					String name = null;
					String photo = null;
					//String photo = null;
					if (cur.moveToFirst()) {
						// Get values from contacts database:
					    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
					    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
					    photo = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));

					}
					
					chatName.setText(name);
					if(photo == null){
				    	 chatImage.setImageResource(R.drawable.noface);
				     }else{
				    	 //Bitmap bm = BitmapFactory.decodeFile(photo);
					     chatImage.setImageURI(Uri.parse(photo));
				     }
				}
				
			}
			
		
			
			//chatMessage.setText(message);
			//chatTime.setText(time);
			
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.mychatlist, container, false);
			return view;
		}
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		String sortOrder = DBHelper.CHAT_KEY_ID + " COLLATE LOCALIZED DESC";
		Uri uri = ChatContentProvider.CONTENT_URI;
		return new CursorLoader(getActivity(), uri, projection, null, null, sortOrder);
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
	
	public boolean contactExists(Context context, String number) {
			/// number is the phone number
			Uri lookupUri = Uri.withAppendedPath(
			PhoneLookup.CONTENT_FILTER_URI, 
			Uri.encode(number));
			String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
			Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
			try {
			   if (cur.moveToFirst()) {
			      return true;
			}
			} finally {
			if (cur != null)
			   cur.close();
			}
			return false;
	}
	
	public String remove234(String number){
		String newNumber = null;
		StringBuffer buf = new StringBuffer(number);

        int start = 0;
        int end = 3;
        buf.replace(start, end, "0"); 
        newNumber = buf.toString();
		
		return newNumber;
	}

}
