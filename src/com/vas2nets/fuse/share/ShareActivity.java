package com.vas2nets.fuse.share;

import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;

public class ShareActivity extends Activity {
	
	EditText ptxt;
	ImageView pImage, shareAllImageView;
	ListView lv;
	List<String> networks;
	
	SocialAuthAdapter adapter;
	List<String> selectedProvider;
	StringBuffer responseText = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Share");
		
		ptxt = (EditText)findViewById(R.id.personeditText1);
		pImage = (ImageView)findViewById(R.id.personimageView1);
		shareAllImageView = (ImageView) findViewById(R.id.shareallimageView);
		lv = (ListView) findViewById(R.id.allnetworkslistView);
		networks = new ArrayList<String>();
		selectedProvider = new ArrayList<String>();
		
		
		networks.add("Facebook");
		networks.add("Twitter");
		networks.add("LinkedIn");
		
		MyAdapter mAdapter = new MyAdapter(this, R.layout.share_comment_list, R.id.snetworktextView, networks);
		lv.setAdapter(mAdapter);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		for (String p : selectedProvider){
			if (p == "Facebook"){
				adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
				adapter.authorize(ShareActivity.this, Provider.FACEBOOK);
			}else if(p == "Twitter"){
				adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
				adapter.authorize(ShareActivity.this, Provider.TWITTER);
			}else if(p == "LinkedIn"){
				adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
				adapter.authorize(ShareActivity.this, Provider.LINKEDIN);
			}else if(p == "Instagram"){
				//adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);
				//adapter.authorize(ShareActivity.this, Provider.INSTAGRAM);
			}
			
			responseText.append("The following contacts were selected...\n");
			responseText.append("\n" + p);
		}
		
		shareAllImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
				
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share, menu);
		return true;
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
			/*
			shareAllImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
					
				}
				
			});*/
		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}

		
		
	}
	
	
	
	// To get status of message after authentication
			private final class MessageListener implements SocialAuthListener<Integer> {

			@Override
			public void onExecute(String provider, Integer t) {
				// TODO Auto-generated method stub
				Integer status = t;
				if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
					Toast.makeText(ShareActivity.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(ShareActivity.this, "Message not posted on" + provider, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onError(SocialAuthError e) {
				// TODO Auto-generated method stub
				
			}
			}
	
	
	
	private class MyAdapter extends ArrayAdapter<String>{
		
		private final LayoutInflater mInflater;
		private final Context ctx;
		List<String> nt;

		public MyAdapter(Context context, int resource, int textViewResourceId,
				List<String> objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			nt = objects;
			ctx = context;
			mInflater = LayoutInflater.from(ctx);
		}
		
		public int getCount() {
			return nt.size();
		}
		
		public String getItem(int index) {
			return nt.get(index);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater =  getLayoutInflater(); 
			View row = inflater.inflate(R.layout.share_comment_list, parent,false);
			
			TextView sname = (TextView) row.findViewById(R.id.snametextView);
			CheckBox check = (CheckBox) row.findViewById(R.id.scheckBox);
			
			String provider = nt.get(position);
			check.setTag(provider);
			check.setOnCheckedChangeListener(null);
			check.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						String myProvider = (String) arg0.getTag();
						if (arg1){
							selectedProvider.add(myProvider);
							//groupList.add(num);
						}else{
							//groupList.remove(num);
							selectedProvider.remove(myProvider);
						}
					}
			    	 
			     });
			
			
			sname.setText(nt.get(position));
			return row;
		}
		
	}

}
