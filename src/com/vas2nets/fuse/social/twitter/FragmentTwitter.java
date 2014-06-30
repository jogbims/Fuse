package com.vas2nets.fuse.social.twitter;

import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

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
public class FragmentTwitter extends Fragment {
	
	SocialAuthAdapter adapter;
	ListView list;
	
	List<Feed> feedList;

	public FragmentTwitter() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_fragment_twitter, container, false);
		
		list = (ListView) getActivity().findViewById(R.id.twitterfeeds);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addCallBack(Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
		adapter.authorize(getActivity(), Provider.TWITTER);
		
		
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
			 new DownloadTwitterTask().execute();
		}
		
	}
	
	private class MyAdapter extends ArrayAdapter<Feed>{
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<Feed> nt;

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<Feed> objects) {
			super(context, resource, textViewResourceId, objects);
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
			View row = inflater.inflate(R.layout.twittercustomlist, parent,false);
			
			TextView tweets = (TextView)row.findViewById(R.id.tweettextView);
			TextView tweetTime = (TextView)row.findViewById(R.id.tweettimetextView);
			
			String message = nt.get(position).getMessage();
			String time = String.valueOf(nt.get(position).getCreatedAt().getHours()) + ":" + String.valueOf(nt.get(position).getCreatedAt().getMinutes());
			
			Log.d("Custom-UI", "From = " + nt.get(position).getFrom());
			Log.d("Custom-UI", "Message = " + nt.get(position).getMessage());
			Log.d("Custom-UI", "Screen Name = " + nt.get(position).getScreenName());
			Log.d("Custom-UI", "Feed Id = " + nt.get(position).getId());
			Log.d("Custom-UI", "Created At = " + nt.get(position).getCreatedAt());
			
			tweets.setText(message);
			tweetTime.setText(time);
			
			return row;
		}
		
	}
	
	// Uses an AsyncTask to download a Twitter user's timeline
		private class DownloadTwitterTask extends AsyncTask<String, Void, String> {

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
					MyAdapter mAdapter = new MyAdapter(getActivity(), R.layout.social_network_layout, R.id.providerText, feedList);
					list.setAdapter(mAdapter);
				}catch(Exception e){
					
				}
			}
			
		}

}
