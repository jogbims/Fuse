package com.vas2nets.fuse.chat.smiley;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import com.vas2nets.fuse.R;
import com.vas2nets.fuse.R.drawable;

public class SmileyClass {
	
	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();
	
	public final int[] images = new int[] { R.drawable.emo_im_happy,
            R.drawable.emo_im_sad, R.drawable.emo_im_winking,
            R.drawable.emo_im_tongue_sticking_out, R.drawable.emo_im_surprised,
             R.drawable.emo_im_yelling,
            R.drawable.emo_im_cool, 
             R.drawable.emo_im_embarrassed };

	static {
	    addPattern(emoticons, ":-)", R.drawable.emo_im_happy);
	    addPattern(emoticons, ":-(", R.drawable.emo_im_sad);
	    addPattern(emoticons, ";-)", R.drawable.emo_im_winking);
	    addPattern(emoticons, ":-P", R.drawable.tongue);
	    addPattern(emoticons, "=-O", R.drawable.emo_im_surprised);
	    addPattern(emoticons, ":-*", R.drawable.emo_im_yelling);
	    // ...as many pattern you want. But make sure you have images in drawable directory
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}

}
