package com.mb.android.maiboapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mb.android.maiboapp.R;


public class MenuBottomActivity extends Activity implements OnClickListener {

	public static final int CAMERA = 0xf4;
	public static final int ALUBM = 0xf5;

	private TextView one, two, three;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_bottom);
		one = (TextView) findViewById(R.id.menu_one);
		two = (TextView) findViewById(R.id.menu_two);
		three = (TextView) findViewById(R.id.menu_three);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_one:
			setResult(CAMERA);
			break;
		case R.id.menu_two:
			setResult(ALUBM);
			break;
		case R.id.menu_three:
			setResult(RESULT_CANCELED);
			break;
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}
}
