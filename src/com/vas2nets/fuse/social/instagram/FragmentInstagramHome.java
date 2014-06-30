package com.vas2nets.fuse.social.instagram;

import java.io.InputStream;
import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
public class FragmentInstagramHome extends Fragment {
	
	SocialAuthAdapter adapter;
    ListView list;
	
	List<Feed> feedList;
	ProgressDialog pd;

	public FragmentInstagramHome() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_fragment_instagram_home, container, false);
		pd = new ProgressDialog(getActivity());
		list = (ListView) view.findViewById(R.id.instagramfeed);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);
		adapter.authorize(getActivity(), Provider.INSTAGRAM);
		
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
			 InstagramAdapter mAdapter = new InstagramAdapter(getActivity(), R.layout.instagramcustomlist,feedList);
			 list.setAdapter(mAdapter);
		}
		
	}
	
	private class InstagramAdapter extends ArrayAdapter<Feed>{
		
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<Feed> nt;

		public InstagramAdapter(Context context,
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
			View row = inflater.inflate(R.layout.instagramcustomlist, parent,false);
			
			//TextView feeds = (TextView)row.findViewById(R.id.faebooktextView);
			TextView feedsTime = (TextView)row.findViewById(R.id.instagramtimetextView);
			TextView fromTxt = (TextView)row.findViewById(R.id.instagramfromtextView);
			ImageView im = (ImageView)row.findViewById(R.id.instagramimageView);
			
			String message = nt.get(position).getMessage();
			String time = String.valueOf(nt.get(position).getCreatedAt().getHours()) + ":" + String.valueOf(nt.get(position).getCreatedAt().getMinutes());
			String from = nt.get(position).getFrom();
			
			Log.d("Custom-UI", "From = " + nt.get(position).getFrom());
			Log.d("Custom-UI", "Message = " + nt.get(position).getMessage());
			Log.d("Custom-UI", "Screen Name = " + nt.get(position).getScreenName());
			Log.d("Custom-UI", "Feed Id = " + nt.get(position).getId());
			Log.d("Custom-UI", "Created At = " + nt.get(position).getCreatedAt());
			
			
			fromTxt.setText(from);
			//feedsTime.setText(time);
			feedsTime.setText(nt.get(position).getCreatedAt().toString());
			new DownloadImageTask(im).execute(message);
			//im.setImageURI(Uri.parse(message));
			
			
			return row;
		}
		
	}
	
	public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

}
