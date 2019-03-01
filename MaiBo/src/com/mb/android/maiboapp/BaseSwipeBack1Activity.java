package com.mb.android.maiboapp;

import android.os.Bundle;
import android.view.MotionEvent;

import com.mb.android.widget.OnSwipeTouchListener;

/**
 * 带滑动返回和网络请求功能的基类Activity
 * 
 * @author cgy
 * 
 */
public class BaseSwipeBack1Activity extends BaseActivity {
	private OnSwipeTouchListener onSwipeTouchListener;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onSwipeTouchListener = new OnSwipeTouchListener() {
			public void onSwipeRight() {
				finish();
			}

			public void onSwipeLeft() {
			}
		};

		getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(onSwipeTouchListener);

	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

}