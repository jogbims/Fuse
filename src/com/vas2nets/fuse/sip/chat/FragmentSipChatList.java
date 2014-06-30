package com.vas2nets.fuse.sip.chat;

import com.csipsimple.api.SipMessage;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;

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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */

public class FragmentSipChatList extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ChatListCursorAdapter cAdapter;
	private ListView lv;
	private TextView emptyTxt;
	String selection;
	
	String[] projection = new String[] {
			SipMessage.FIELD_ID,
			SipMessage.FIELD_BODY,
			SipMessage.FIELD_CONTACT,
			SipMessage.FIELD_DATE,
			SipMessage.FIELD_FROM,
			SipMessage.FIELD_FROM_FULL,
			SipMessage.FIELD_MIME_TYPE,
			SipMessage.FIELD_READ,
			SipMessage.FIELD_STATUS,
			SipMessage.FIELD_TO,
			SipMessage.FIELD_TYPE
			
	};
	
	String from;
	String to;

	public FragmentSipChatList() {
		// Required empty public constructor
	}
	
	@SuppressLint("CutPasteId")
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		lv = (ListView ) getActivity().findViewById(R.id.mychatlist_listview);
		emptyTxt = (TextView) getActivity().findViewById(R.id.emptyListElem);
		
		ContentResolver cr = getActivity().getContentResolver();	
		Cursor cursor = cr.query(SipMessage.THREAD_URI, null, null, null, null);
		
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
		
		getLoaderManager().initLoader(0, null, this);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				String name = null;
				String phoneNumber = null;
				
				
				
				Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
				from = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM));
				to = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_TO));
				
				
				
				phoneNumber = getPhoneNumberFromSipID(to);
				
				boolean numberExist = contactExists(getActivity(), phoneNumber);
				
				if (numberExist){
					// define the columns I want the query to return
					String[] projection = new String[] {
					        ContactsContract.PhoneLookup.DISPLAY_NAME,
					        ContactsContract.PhoneLookup._ID,
					        ContactsContract.PhoneLookup.PHOTO_URI};
					// encode the phone number and build the filter URI
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
					// query time
					Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
					String contactId = null;
					
					
					if (cur.moveToFirst()) {
						// Get values from contacts database:
					    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
					    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));  
					}
				}else{
					
					String newNumber = remove234(phoneNumber);
					boolean newNumberExist = contactExists(getActivity(), newNumber);
					if (newNumberExist){
						// define the columns I want the query to return
						String[] projection = new String[] {
						        ContactsContract.PhoneLookup.DISPLAY_NAME,
						        ContactsContract.PhoneLookup._ID,
						        ContactsContract.PhoneLookup.PHOTO_URI};
						// encode the phone number and build the filter URI
						Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
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
				//Toast.makeText(getActivity(), name + " " + phoneNumber,Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(getActivity(), SipChatActivity.class);
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
		
		
		return inflater.inflate(R.layout.fragment_fragment_sip_chat_list,
				container, false);
	}
	
	private class ChatListCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;
		
		private TextView chatName;
		private TextView chatMessage;
		private TextView chatTime;
		private ImageView chatImage;

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
			
			SipMessage msg = new SipMessage(cursor);
			
			chatName = (TextView) view.findViewById(R.id.chatlistnametextView);
			chatMessage = (TextView) view.findViewById(R.id.chatlistmessagetextView);
			chatTime = (TextView) view.findViewById(R.id.chatlisttimetextView);
			chatImage = (ImageView)view.findViewById(R.id.chatlistimageView);
			
			
			long date = cursor.getLong(cursor.getColumnIndex(SipMessage.FIELD_DATE));
			
			String phoneNumber = msg.getDisplayName();
			String name = null;
			
			
			boolean numberExist = contactExists(getActivity(), phoneNumber);
			
			if (numberExist){
				// define the columns I want the query to return
				String[] projection = new String[] {
				        ContactsContract.PhoneLookup.DISPLAY_NAME,
				        ContactsContract.PhoneLookup._ID,
				        ContactsContract.PhoneLookup.PHOTO_URI};
				// encode the phone number and build the filter URI
				Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
				// query time
				Cursor cur = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
				String contactId = null;
				
				
				if (cur.moveToFirst()) {
					// Get values from contacts database:
				    contactId = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup._ID));
				    name =      cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));  
				}
			}else{
				
				String newNumber = remove234(phoneNumber);
				boolean newNumberExist = contactExists(getActivity(), newNumber);
				if (newNumberExist){
					// define the columns I want the query to return
					String[] projection = new String[] {
					        ContactsContract.PhoneLookup.DISPLAY_NAME,
					        ContactsContract.PhoneLookup._ID,
					        ContactsContract.PhoneLookup.PHOTO_URI};
					// encode the phone number and build the filter URI
					Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
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
			
			
			chatMessage.setText(msg.getBodyContent());
			//chatName.setText(msg.getDisplayName());
			chatName.setText(name);
		    int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
		    chatTime.setText(DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags));
		   
			
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.mychatlist, arg2, false);
			return view;
		}
		
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
	
	public String getPhoneNumberFromSipID(String sipId){
		
		sipId = sipId.substring(sipId.indexOf(":") + 1);
		sipId = sipId.substring(0, sipId.indexOf("@"));
		return sipId;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(getActivity(), SipMessage.THREAD_URI, null, null, null, null);
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

}
