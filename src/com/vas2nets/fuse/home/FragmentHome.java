package com.vas2nets.fuse.home;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.db.ActiveSocialDB;
import com.vas2nets.fuse.sip.chat.ChatMessage;
import com.vas2nets.fuse.social.core.AddSocialNetworksActivity;
import com.vas2nets.fuse.social.instagram.FragmentInstagramHome.DownloadImageTask;
import com.vas2nets.fuse.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentHome extends Fragment 
{
	private ActiveSocialDB db;
	List<String> allProviders;
	
	SocialAuthAdapter adapter;
	ListView list;
	ProgressDialog pd;

	
	
	public FragmentHome() {
		// Required empty public constructor
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
		
		list = (ListView) view.findViewById(R.id.homefeedlistView);
		pd = new ProgressDialog(getActivity());
		
		
		db = new ActiveSocialDB(getActivity());
		allProviders = new ArrayList<String>();
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.authorize(getActivity(), Provider.FACEBOOK);
		
		/*
		allProviders = db.getAllProviders();
		
		for (String provider : allProviders){
			
			if (provider.equals("facebook")){
				
				adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
				adapter.authorize(getActivity(), Provider.FACEBOOK);
				
			}else if(provider.equals("twitter")){
				
				adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
				adapter.authorize(getActivity(), Provider.TWITTER);
				
			}else if(provider.equals("linkedin")){
				
				adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
				adapter.authorize(getActivity(), Provider.LINKEDIN);
				
			}else if(provider.equals("instagram")){
				
				adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);
				adapter.authorize(getActivity(), Provider.INSTAGRAM);
				
			}else if(provider.equals("google")){
				
				adapter.addProvider(Provider.GOOGLE, R.drawable.google);
				adapter.authorize(getActivity(), Provider.GOOGLE);
				
			}else if(provider.equals("googleplus")){
				
				adapter.addProvider(Provider.GOOGLEPLUS, R.drawable.googleplus);
				adapter.authorize(getActivity(), Provider.GOOGLEPLUS);
				
			}
			
		}
		*/
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
//			pd.setMessage("Loading Feeds...");
//			pd.setIndeterminate(false);
//			pd.setCancelable(true);
//			pd.show();
			adapter.getFeedsAsync(new FeedDataListener()); 
			//pd.dismiss();
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
			 //feedList = arg1;
			 //new DownloadFacebookTask().execute();
			 HomeAdapter mAdapter = new HomeAdapter(getActivity(), R.layout.homecustomlist,arg1);
			 list.setAdapter(mAdapter);
		}
		
	}
	
	
	
	


	
	private class HomeAdapter extends ArrayAdapter<Feed>{
		
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<Feed> nt;
		
		private Bitmap bitmap;
		
		RelativeLayout rFacebook, rInstagram, rLinkedIn, rTwitter;

		public HomeAdapter(Context context,
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
			View row = inflater.inflate(R.layout.homecustomlist, parent,false);
			
			//facebook component
			TextView ffeeds = (TextView)row.findViewById(R.id.homefacebookmessagetextView);
			TextView ffeedsTime = (TextView)row.findViewById(R.id.homefacebooktimetextView);
			//TextView fprovidertxt = (TextView)row.findViewById(R.id.homefacebookprovidertextView);
			
			//instagram component
			ImageView ifeeds = (ImageView)row.findViewById(R.id.homeinstagramimageView);
			TextView ifeedsTime = (TextView)row.findViewById(R.id.homeinstagramtimetextView);
			TextView iprovidertxt = (TextView)row.findViewById(R.id.homeinstagramprovidertextView);
			
			rFacebook = (RelativeLayout) row.findViewById(R.id.facebookform);
			rInstagram = (RelativeLayout) row.findViewById(R.id.instagramform);
			
			String message = nt.get(position).getMessage();
			String time = String.valueOf(nt.get(position).getCreatedAt().getHours()) + ":" + String.valueOf(nt.get(position).getCreatedAt().getMinutes());
			String providerName = adapter.getCurrentProvider().getAccessGrant().getProviderId();
			
			rFacebook.setVisibility(View.VISIBLE);
			rInstagram.setVisibility(View.GONE);
			
			ChatMessage chat = new ChatMessage(true, "");
			ffeeds.setText(message);
			ffeeds.setBackgroundResource(chat.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
			ffeeds.setGravity(chat.left ? Gravity.LEFT : Gravity.RIGHT);
			ffeedsTime.setText(time);
			//fprovidertxt.setText(providerName);
			
			/*
			if (providerName.equals("instagram")){
				rInstagram.setVisibility(View.VISIBLE);
				rFacebook.setVisibility(View.GONE);
				
				new DownloadImageTask(ifeeds).execute(message);
				
				ifeedsTime.setText(time);
				iprovidertxt.setText(providerName);
				
			}else if(providerName.equals("facebook")){
				
				rFacebook.setVisibility(View.VISIBLE);
				rInstagram.setVisibility(View.GONE);
				
				ffeeds.setText(message);
				ffeedsTime.setText(time);
				fprovidertxt.setText(providerName);
				
				
			}else if(providerName.equals("twitter")){
				
			}else if(providerName.equals("linkedin")){
				
			}
			*/
			Log.d("Custom-UI", "From = " + nt.get(position).getFrom());
			Log.d("Custom-UI", "Message = " + nt.get(position).getMessage());
			Log.d("Custom-UI", "Screen Name = " + nt.get(position).getScreenName());
			Log.d("Custom-UI", "Feed Id = " + nt.get(position).getId());
			Log.d("Custom-UI", "Created At = " + nt.get(position).getCreatedAt());
			
			//ffeeds.setText(message);
			//ffeedsTime.setText(time);
			//fprovidertxt.setText(providerName);
			
			
			return row;
		}
		
	}
	
	public void addSocials(View v){	
			 Intent i = new Intent(getActivity(), AddSocialNetworksActivity.class);
			 startActivity(i);
	}
	
	
	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        pd.show();
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	          InputStream in = new java.net.URL(urldisplay).openStream();
	          mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    @Override 
	    protected void onPostExecute(Bitmap result) {
	        super.onPostExecute(result);
	        pd.dismiss();
	        bmImage.setImageBitmap(result);
	    }
	  }
	
	
	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    //Log.i(TAG, "Service destroying");
	     
	    
	  }
    


	
}
