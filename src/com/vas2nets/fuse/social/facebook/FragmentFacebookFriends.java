package com.vas2nets.fuse.social.facebook;

import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.image.ImageLoader;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentFacebookFriends extends Fragment {
	
	SocialAuthAdapter adapter;
	ListView list;
	List<Contact> contactss;
	
	
	public FragmentFacebookFriends() {
		// Required empty public constructor
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		list = (ListView) getActivity().findViewById(R.id.contactList);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Contact friend = contactss.get(arg2);
				
				
				
				Intent i = new Intent(getActivity(), FacebookFriendDetailActivity.class);
				i.putExtra("FriendId",friend.getId());
				i.putExtra("FriendName", friend.getDisplayName());
				i.putExtra("FriendPhoto", friend.getProfileImageURL());
				startActivity(i);
			}
			
		});
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_fragment_facebook_friends, container, false);
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.authorize(getActivity(), Provider.FACEBOOK);
		/*
		list = (ListView) view.findViewById(R.id.contactList);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Contact friend = (Contact) arg0.getItemAtPosition(arg2);
				
				
				
				Intent i = new Intent(getActivity(), FacebookFriendDetailActivity.class);
				i.putExtra("FriendId",friend.getId());
				i.putExtra("FriendName", friend.getDisplayName());
				i.putExtra("FriendPhoto", friend.getProfileImageURL());
				startActivity(i);
			}
			
		});
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
			adapter.getContactListAsync(new ContactDataListener());
			
		}

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	
	// To receive the contacts response after authentication
		private final class ContactDataListener implements SocialAuthListener<List<Contact>> {

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onExecute(String arg0, List<Contact> arg1) {
			// TODO Auto-generated method stub
			List<Contact> contactsList = arg1;
			list.setAdapter(new ContactAdapter(getActivity(), R.layout.facebook_contact_list, contactsList));
		}
		
		}
		
		// adapter for contact list
		public class ContactAdapter extends ArrayAdapter<Contact> {
			private final LayoutInflater mInflater;
			//List<Contact> contacts;
			ImageLoader imageLoader;

			public ContactAdapter(Context context, int textViewResourceId, List<Contact> contacts) {
				super(context, textViewResourceId);
				mInflater = LayoutInflater.from(context);
				contactss = contacts;
				imageLoader = new ImageLoader(context);
			}

			@Override
			public int getCount() {
				return contactss.size();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				final Contact bean = contactss.get(position);
				View row = mInflater.inflate(R.layout.facebook_contact_list, parent, false);

				TextView label = (TextView) row.findViewById(R.id.cName);
				TextView email = (TextView) row.findViewById(R.id.cEmail);
				ImageView cImage = (ImageView) row.findViewById(R.id.cImage);

				Log.d("Custom-UI", "Display Name = " + bean.getDisplayName());
				Log.d("Custom-UI", "First Name = " + bean.getFirstName());
				Log.d("Custom-UI", "Last Name = " + bean.getLastName());
				Log.d("Custom-UI", "Contact ID = " + bean.getId());
				Log.d("Custom-UI", "Profile URL = " + bean.getProfileUrl());
				Log.d("Custom-UI", "Profile Image URL = " + bean.getProfileImageURL());
				Log.d("Custom-UI", "Email = " + bean.getEmail());

				imageLoader.DisplayImage(bean.getProfileImageURL(), cImage);
				label.setText(bean.getFirstName() + "  " + bean.getLastName());
				if (bean.getEmail() != null){
					email.setVisibility(View.VISIBLE);
					email.setText(bean.getEmail());
				}
			
				return row;
			}
		}

}
