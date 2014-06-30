package com.vas2nets.fuse.social.core;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;

public class CustomSocialAdapter extends BaseAdapter {
	
	private final LayoutInflater mInflater;
	private final Context ctx;
	private Bitmap mIcon;
	
	SocialAuthAdapter adapter;
	private final Provider[] providers = new Provider[] { Provider.FACEBOOK, Provider.TWITTER, Provider.LINKEDIN,Provider.INSTAGRAM,
			Provider.YAHOO,Provider.FOURSQUARE,
			Provider.GOOGLEPLUS};
	private final int[] images = new int[] { R.drawable.facebook, R.drawable.twitter, R.drawable.linkedin, R.drawable.instagram,
			 R.drawable.yahoo, R.drawable.foursquare,
			R.drawable.googleplus};

	
	public CustomSocialAdapter(Context context, SocialAuthAdapter mAdapter) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		ctx = context;
		mInflater = LayoutInflater.from(ctx);
		adapter = mAdapter;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return providers.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need to reinflate it. We only inflate a new View when the convertView
		// supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.providers_list, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.providerText);
			holder.icon = (ImageView) convertView.findViewById(R.id.provider);
			holder.signText = (TextView) convertView.findViewById(R.id.signstatus);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		mIcon = BitmapFactory.decodeResource(ctx.getResources(), images[position]);

		// Bind the data efficiently with the holder.

		String textCase = providers[position].toString();
		textCase = String.valueOf(textCase.charAt(0)).toUpperCase() + textCase.substring(1, textCase.length());

		holder.text.setText(textCase);
		holder.icon.setImageBitmap(mIcon);

		holder.text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AddSocialNetworksActivity.pos = position;
				
				

				if (providers[position].equals(Provider.GOOGLE))
					adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
				else if (providers[position].equals(Provider.FOURSQUARE))
					adapter.addCallBack(Provider.FOURSQUARE,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.SALESFORCE))
					adapter.addCallBack(Provider.SALESFORCE,
							"https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.YAMMER))
					adapter.addCallBack(Provider.YAMMER,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.FLICKR))
					adapter.addCallBack(Provider.FLICKR,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.INSTAGRAM))
					adapter.addCallBack(Provider.INSTAGRAM,
							"http://opensource.brickred.com/socialauthdemo/socialAuthSuccessAction.do");

				// This method will enable the selected provider
				adapter.authorize(ctx, providers[position]);

			}
		});

		holder.signText.setText("Sign In");
		holder.signText.setTag(1);

		holder.signText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String text = (String) ((TextView) v).getText();

				if (text.equalsIgnoreCase("sign in")) {
					AddSocialNetworksActivity.pos = position;

					// Use addCallback method if using
					// your own keys for FOURSQUARE , GOOGLE , SALESFORCE ,
					// YAMMER

					if (providers[position].equals(Provider.GOOGLE))
						adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
					else if (providers[position].equals(Provider.FOURSQUARE))
						adapter.addCallBack(Provider.FOURSQUARE,
								"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
					else if (providers[position].equals(Provider.SALESFORCE))
						adapter.addCallBack(Provider.SALESFORCE,
								"https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do");
					else if (providers[position].equals(Provider.YAMMER))
						adapter.addCallBack(Provider.YAMMER,
								"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

					// This method will enable the selected provider
					adapter.authorize(ctx, providers[position]);
				} else if (text.equalsIgnoreCase("sign out")) {
					// Sign Out
					boolean status = adapter.signOut(ctx, providers[position].toString());
					Log.d("status", String.valueOf(status));
					if (status) {
						((TextView) v).setText("Sign In");
					}
				}
			}
		});

		return convertView;
	}
	
	class ViewHolder {
		TextView text;
		ImageView icon;
		TextView signText;
	}

}
