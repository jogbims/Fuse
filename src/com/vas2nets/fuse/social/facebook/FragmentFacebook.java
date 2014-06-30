package com.vas2nets.fuse.social.facebook;


import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentFacebook extends Fragment {
	SocialAuthAdapter adapter;
    ListView list;
	
	List<Feed> feedList;
	// Instance of Facebook Class
    
	
	public FragmentFacebook() {
		// Required empty public constructor
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fragment_facebook, container, false);
		list = (ListView) view.findViewById(R.id.facebookfeed);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.authorize(getActivity(), Provider.FACEBOOK);
		
		return view;
	}
	
	private final class ResponseListener implements DialogListener {

		@Override
		public void onBack() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onComplete(Bundle arg0) {
			// TODO Auto-generated method stub
			adapter.getFeedsAsync(new FeedDataListener()); 
		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private final class FeedDataListener implements SocialAuthListener<List<Feed>> {

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onExecute(String arg0, List<Feed> arg1) {
			// TODO Auto-generated method stub
			 feedList = arg1;
			 //new DownloadFacebookTask().execute();
			 FacebookAdapter mAdapter = new FacebookAdapter(getActivity(), R.layout.facebookcustomlist,feedList);
			 list.setAdapter(mAdapter);
		}
		
	}
	
	private class FacebookAdapter extends ArrayAdapter<Feed>{
		
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<Feed> nt;

		public FacebookAdapter(Context context,
				int textViewResourceId, List<Feed> objects) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
			nt = objects;
			ctx = context;
			mInflater = LayoutInflater.from(ctx);
		}
		
		public int getCount() {
			return nt.size();
		}
		
		public Feed getItem(int index) {
			return nt.get(index);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater =  getActivity().getLayoutInflater(); 
			View row = inflater.inflate(R.layout.facebookcustomlist, parent,false);
			
			TextView feeds = (TextView)row.findViewById(R.id.faebooktextView);
			TextView feedsTime = (TextView)row.findViewById(R.id.facebooktimetextView);
			
			String message = nt.get(position).getMessage();
			String time = String.valueOf(nt.get(position).getCreatedAt().getHours()) + ":" + String.valueOf(nt.get(position).getCreatedAt().getMinutes());
			
			Log.d("Custom-UI", "From = " + nt.get(position).getFrom());
			Log.d("Custom-UI", "Message = " + nt.get(position).getMessage());
			Log.d("Custom-UI", "Screen Name = " + nt.get(position).getScreenName());
			Log.d("Custom-UI", "Feed Id = " + nt.get(position).getId());
			Log.d("Custom-UI", "Created At = " + nt.get(position).getCreatedAt());
			
			feeds.setText(message);
			feedsTime.setText(time);
			
			
			return row;
		}
		
	}
	/*
	private class DownloadFacebookTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try{
				
			}catch(Exception e){
				
			}
			
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				
			}catch(Exception e){
				
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			try{
				
			}catch(Exception e){
				FacebookAdapter mAdapter = new FacebookAdapter(getActivity(), android.R.layout.simple_list_item_1, R.id.faebooktextView, feedList);
				list.setAdapter(mAdapter);
			}
		}
	}
	*/
	
}
