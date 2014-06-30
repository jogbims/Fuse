package com.vas2nets.fuse.social.facebook;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;
import com.vas2nets.fuse.R.id;
import com.vas2nets.fuse.R.layout;
import com.vas2nets.fuse.image.ImageLoader;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class FragmentFacebookShare extends Fragment {
	
	
	SocialAuthAdapter adapter;
	TextView commentTxtView;
	TextView wordCountTxt;
	ImageView shareButton;
	ImageView userImage;
	
	
	public FragmentFacebookShare() {
		// Required empty public constructor
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		final View view = inflater.inflate(R.layout.fragment_fragment_facebook_share, container, false);
		
		commentTxtView = (TextView) view.findViewById(R.id.commenteditText);
		wordCountTxt = (TextView) view.findViewById(R.id.wordcounter);
		shareButton = (ImageView) view.findViewById(R.id.shareimageView);
		userImage = (ImageView) view.findViewById(R.id.userimageView);
		
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.authorize(getActivity(), Provider.FACEBOOK);
		
		
		
		
	
		TextWatcher mTextEditorWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           //This sets a textview to the current length
	           wordCountTxt.setText(String.valueOf(s.length()));
	        }

	        public void afterTextChanged(Editable s) {
	        }
		};
		commentTxtView.addTextChangedListener(mTextEditorWatcher);
		
		return view;
	}
	
	
	

	
	
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			
			Profile p = adapter.getUserProfile();
			ImageLoader imageLoader = new ImageLoader(getActivity());
			imageLoader.DisplayImage(p.getProfileImageURL(), userImage);
			
			shareButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					adapter.updateStatus(commentTxtView.getText().toString(), new MessageListener(), false);
					commentTxtView.setText(" ");
				}
				
			});
		}

		@Override
		public void onError(SocialAuthError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onBack() {
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
				Toast.makeText(getActivity(), "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getActivity(), "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {
			// TODO Auto-generated method stub
			
		}
		}
		

}
