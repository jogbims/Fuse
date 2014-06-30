package com.vas2nets.fuse.social.linkedin;

import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.content.Context;
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
public class FragmentLinkedIn extends Fragment {
	
	SocialAuthAdapter adapter;
    ListView list;
	
	List<Feed> feedList;

	public FragmentLinkedIn() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.fragment_fragment_linked_in, container, false);
		list = (ListView) view.findViewById(R.id.linkedinfeed);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.authorize(getActivity(), Provider.LINKEDIN);
		
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
			 LinkedInAdapter mAdapter = new LinkedInAdapter(getActivity(), R.layout.linkedincustomlist,feedList);
			 list.setAdapter(mAdapter);
		}
		
	}
	
	private class LinkedInAdapter extends ArrayAdapter<Feed>{
		
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<Feed> nt;

		public LinkedInAdapter(Context context,
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
			View row = inflater.inflate(R.layout.linkedincustomlist, parent,false);
			
			TextView feeds = (TextView)row.findViewById(R.id.linkedintextView);
			TextView feedsTime = (TextView)row.findViewById(R.id.linkedintimetextView);
			
			String message = nt.get(position).getMessage();
			String time = String.valueOf(nt.get(position).getCreatedAt().getHours()) + ":" + String.valueOf(nt.get(position).getCreatedAt().getMinutes());
			/*
			Log.d("Custom-UI", "From = " + nt.get(position).getFrom());
			Log.d("Custom-UI", "Message = " + nt.get(position).getMessage());
			Log.d("Custom-UI", "Screen Name = " + nt.get(position).getScreenName());
			Log.d("Custom-UI", "Feed Id = " + nt.get(position).getId());
			Log.d("Custom-UI", "Created At = " + nt.get(position).getCreatedAt());
			*/
			feeds.setText(message);
			feedsTime.setText(time);
			
			
			return row;
		}
		
	}

}
