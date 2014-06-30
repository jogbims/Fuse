package com.vas2nets.fuse.profile;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.db.DBHelper;
import com.vas2nets.fuse.image.PhotoUtility;
import com.vas2nets.fuse.user.UserContentProvider;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentProfile extends Fragment implements LoaderCallbacks<Cursor>{
	
	private UserCursorAdapter cAdapter;
	
	String[] projection = new String[] {
			DBHelper.USER_KEY_ID,
			DBHelper.USER_KEY_FIRSTNAME,
			DBHelper.USER_KEY_LASTNAME,
			DBHelper.USER_KEY_PHONENUMBER,
			DBHelper.USER_KEY_PHOTO
    };
	

	public FragmentProfile() {
		// Required empty public constructor
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		ContentResolver cr = getActivity().getContentResolver();
		 Cursor cursor = cr.query(UserContentProvider.CONTENT_URI, projection, null, null, null);
		 
		 cAdapter = new UserCursorAdapter(getActivity(),cursor);
		 
		 cAdapter.notifyDataSetChanged();
		 getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_fragment_profile, container,
				false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Uri uri = UserContentProvider.CONTENT_URI;
		// return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
		return new CursorLoader(getActivity(), uri, projection, null, null, null);
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
	
	
	private class UserCursorAdapter extends CursorAdapter {
		
		private Context mContext;
		private Cursor cr;
		private LayoutInflater inflater;

		public UserCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
			mContext = context;
			cr = c;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			 ImageView imageView = (ImageView) view.findViewById(R.id.profilepiximageView);
			 byte[] img = cursor.getBlob(cursor.getColumnIndex(DBHelper.USER_KEY_PHOTO));
			 imageView.setImageBitmap(PhotoUtility.getPhoto(img));
			 imageView.setBackgroundResource(R.drawable.imageborder);
			 
			 
			 String firstName = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_FIRSTNAME));
			 String lastName = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_LASTNAME));
			 String phoneN = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_PHONENUMBER));
			 
			 TextView fullName = (TextView)view.findViewById(R.id.nametextView);
			 //fullName.setText(firstName + " " + lastName);
			 fullName.setText(phoneN);
			 //byte[] img=null;
			
			 /*if (img == null){
				 imageView.setImageResource(R.drawable.noface);
			 }else{
				 
				 imageView.setImageBitmap(PhotoUtility.getPhoto(img));
			 }*/
			 
			 /*
			 String photo = cursor.getString(cursor.getColumnIndex(DBHelper.USER_KEY_PHOTO));
			 if(photo == null){
		    	 imageView.setImageResource(R.drawable.noface);
		     }else{
			     imageView.setImageURI(Uri.parse(photo));
		     }*/
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup container) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.fragment_fragment_profile, container, false);
			return view;
		}
		
	}

}
