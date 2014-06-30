package com.vas2nets.fuse.social.twitter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentTwitterSwipe extends Fragment {
	
	private static final int TWO_FRAGMENTS = 3;
	private ViewPager mViewPager;
	private TabContentFactory mFactory = new TabContentFactory() {

		@Override
		public View createTabContent(String tag) {
			View v = new View(getActivity());
			v.setMinimumHeight(0);
			return v;
		}
	};



	public FragmentTwitterSwipe() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragContent = inflater.inflate(R.layout.fragment_fragment_twitter_swipe,
				container, false);
		
		
		mViewPager = (ViewPager) fragContent.findViewById(R.id.pagertwitter);
		mViewPager.setAdapter(new InnerPagerAdapter(getChildFragmentManager()));
		final TabHost tabHost = (TabHost) fragContent
				.findViewById(android.R.id.tabhost);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("Tab1").setIndicator("Home").setContent(mFactory));
		tabHost.addTab(tabHost.newTabSpec("Tab2").setIndicator("Followers").setContent(mFactory));
		tabHost.addTab(tabHost.newTabSpec("Tab3").setIndicator("Share").setContent(mFactory));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				tabHost.setCurrentTab(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("Tab1")) {
					mViewPager.setCurrentItem(0);
				} else if (tabId.equals("Tab2")) {
					mViewPager.setCurrentItem(1);
				} else if (tabId.equals("Tab3")){
					mViewPager.setCurrentItem(2);
				}
			}
		});
		
		
		return fragContent;
	}
	
	private class InnerPagerAdapter extends FragmentPagerAdapter {

		public InnerPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new FragmentTwitter();
			} else if (position == 1) {
				return new FragmentTwitterFollowers();
			} else if(position == 2){
				return new FragmentTwitterShare();
			} 
			return null;
		}

		@Override
		public int getCount() {
			return TWO_FRAGMENTS;
		}

		
	}

}
