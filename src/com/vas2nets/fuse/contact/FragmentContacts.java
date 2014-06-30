package com.vas2nets.fuse.contact;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.json.JSONParser;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentContacts extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ContactsCursorAdapter cAdapter;
	Context thiscontext;
	ListView mContactsList;
	EditText inputSearch;
	private ArrayList<String> array_sort= new ArrayList<String>();
	int textlength=0;
	
	String[] projection = new String[] {
			Phone._ID,
			Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.PHOTO_URI
    };
	
	String selection = Phone.NUMBER + "!='null'";
	String sortOrder = Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	
	List<String> allFuseContacts = new ArrayList<String>();
	private static final String GET_PHONENUMBERS = "http://83.138.190.170/fusescript/getPhoneNumber.php";
	private JSONParser jParser = new JSONParser();
	private JSONObject json;
	private JSONArray output;
	private JSONArray allNumbers;
	
	List<String> fuseContacts;
	JSONArray fprofile;
	String phoneNumber;
	String fuseText = "Fuse installed";

	public FragmentContacts() {
		// Required empty public constructor
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		 
		 mContactsList = (ListView) getActivity().findViewById(R.id.contact_listview);
		 
		 ContentResolver cr = getActivity().getContentResolver();
		 Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection, null, sortOrder);
		 
	
		 cAdapter = new ContactsCursorAdapter(getActivity(),cursor);
		
		 mContactsList.setAdapter(cAdapter);
		 cAdapter.notifyDataSetChanged();
		 mContactsList.setTextFilterEnabled(true);
		 
		 mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView txt = (TextView) arg1.findViewById(R.id.customcontacttextView);
				String p = txt.getText().toString();
				
				 Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
				 String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			     String dName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
			     String photo = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI));
				
				
				
				//Toast.makeText(getActivity(), p, Toast.LENGTH_LONG).show();
				char last = p.charAt(p.length() - 1);
				if (last == 'd'){
					Intent i = new Intent(getActivity(), ContactDetailsActivity.class);
					i.putExtra("ContactName",dName);
					i.putExtra("ContactPhoto", photo);
					i.putExtra("ContactNumber", number);
					i.putExtra("Installed", "yes");
					startActivity(i);
				}else{
					Intent i = new Intent(getActivity(), ContactDetailsActivity.class);
					i.putExtra("ContactName",dName);
					i.putExtra("ContactPhoto", photo);
					i.putExtra("ContactNumber", number);
					i.putExtra("Installed", "no");
					startActivity(i);
				}
			     
			        
			    
			}
			 
		 });
		 
		/*
		 cAdapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence arg0) {
				// TODO Auto-generated method stub
				 ContentResolver cr = getActivity().getContentResolver();
				 String selection1 = Phone.DISPLAY_NAME;
				Cursor cursor = cr.query(Phone.CONTENT_URI, projection, selection1, null, sortOrder);
				return cursor;
			}
			 
		 });*/
		 
	     getActivity().getSupportLoaderManager().initLoader(0, null, this);
	     
	     inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);
			inputSearch.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					// TODO Auto-generated method stub
					
					cAdapter.getFilter().filter(s.toString());
					//cAdapter.notifyDataSetChanged();
				}
				
			});
			
			fuseContacts = new ArrayList<String>();
			SharedPreferences pref1 = getActivity().getSharedPreferences("FusePreferences", 0);
	    	phoneNumber = pref1.getString("FusePhoneNumber", null);
	    	new GetFusePhoneNumber().execute();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		thiscontext = container.getContext();
		
		return inflater.inflate(R.layout.fragment_fragment_contacts, container,
				false);
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = Phone.CONTENT_URI;
		// return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
		return new CursorLoader(getActivity(), uri, projection, selection, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(arg1);
		StringBuffer responseText = new StringBuffer();
		allNumbers = new JSONArray();
		for(String n : allFuseContacts){
			allNumbers.put(n);
			
			//responseText.append(n + " ");
		}
		
		//Toast.makeText(getActivity().getApplicationContext(), allNumbers.toString(),
               // Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		cAdapter.swapCursor(null);
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
	
	private class ContactsCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public ContactsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			 ImageView imageView = (ImageView) view.findViewById(R.id.customimageView);
			 TextView txtName = (TextView) view.findViewById(R.id.customtextView);
			 TextView txtPhone = (TextView) view.findViewById(R.id.customcontacttextView);
			 ImageView fuseImage = (ImageView) view.findViewById(R.id.fuseimageView);
		
		     String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
		     String dName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
		     String photo = cursor.getString(cursor.getColumnIndex(Phone.PHOTO_URI));
		     
		     txtName.setText(dName);
		    // txtPhone.setText(number);
		     //imageView.setImageURI(Uri.parse(photo));
		     if(photo == null){
		    	 imageView.setImageResource(R.drawable.noface);
		     }else{
		    	 Bitmap bm = BitmapFactory.decodeFile(photo);
			     imageView.setImageURI(Uri.parse(photo));
		     }
		     
		     if (number.startsWith("+")){
		            number = number.substring(1);
		            System.out.println(number);
		        }else if(number.startsWith("0")){
		            number = number.substring(1);
		            number = "234" + number;
		            System.out.println(number);
		        }else{
		            System.out.println(number);
		        }
		     
		     if (fuseContacts.contains(number)){
					txtPhone.setText(number + "\n" + fuseText);
					txtPhone.setTextColor(Color.parseColor("#33b5e5"));
					fuseImage.setVisibility(view.VISIBLE);
				}else{
					txtPhone.setText(number);
					txtPhone.setTextColor(Color.BLACK);
					fuseImage.setVisibility(view.GONE);
				}
		     //get all fuse contacts
		     allFuseContacts.add(number);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.custom_contact_layout, container, false);
			return view;
		}
		


	}


}
