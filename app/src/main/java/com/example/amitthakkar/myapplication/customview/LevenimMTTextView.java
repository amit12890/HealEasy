package com.example.amitthakkar.myapplication.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.amitthakkar.myapplication.R;

public class LevenimMTTextView extends TextView {

	public LevenimMTTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public LevenimMTTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public LevenimMTTextView(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LevenimMTTextView);
			 String fontName = a.getString(R.styleable.LevenimMTTextView_fontName);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}