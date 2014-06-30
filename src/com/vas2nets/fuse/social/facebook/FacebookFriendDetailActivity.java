package com.vas2nets.fuse.social.facebook;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.R.menu;
import com.vas2nets.fuse.image.ImageLoader;

public class FacebookFriendDetailActivity extends Activity {
	
	String id, name, photo;
	TextView tv;
	ImageView fIV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_friend_detail);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent i = getIntent();
		id = i.getStringExtra("FriendId");
		name = i.getStringExtra("FriendName");
		photo = i.getStringExtra("FriendPhoto");
		
		this.getActionBar().setTitle("Facebook");
		
		tv = (TextView) findViewById(R.id.fNametextView);
		fIV = (ImageView) findViewById(R.id.fcontactImageView);
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.DisplayImage(photo, fIV);
		
		tv.setText(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook_friend_detail, menu);
		return true;
	}

}
