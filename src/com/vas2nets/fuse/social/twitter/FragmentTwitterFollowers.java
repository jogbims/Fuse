package com.vas2nets.fuse.social.twitter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.layout;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentTwitterFollowers extends Fragment {

	public FragmentTwitterFollowers() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_fragment_twitter_followers,
				container, false);
	}

}
