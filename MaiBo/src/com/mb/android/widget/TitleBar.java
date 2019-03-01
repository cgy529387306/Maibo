package com.mb.android.widget;



import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.R;

/**
 * 公有TitleBar实现 Created by cgy on 2015/8/22.
 */
public class TitleBar extends RelativeLayout implements View.OnClickListener{

	private RelativeLayout rel_actionbar;
	private LinearLayout lin_actionbar_back;
	private TextView txv_actionbar_title;
	private Activity mActivity;
	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.common_actionbar_back, this, true);
	}

	/**
	 * 主初始化界面
	 */
	public void initTitleBar(Activity activity,String title) {
		this.mActivity = activity;
		txv_actionbar_title.setText(title);
	}
	
	/**
	 * 主初始化界面
	 */
	public void initTitleBar(Activity activity,int resid) {
		this.mActivity = activity;
		txv_actionbar_title.setText(resid);
	}

	
	/**
	 * 设置背景
	 * 
	 * @param resId
	 */
	public void setBackground(int resid) {
		rel_actionbar.setBackgroundResource(resid);
	}
	
	/**
	 * 隐藏放回按钮
	 * @param resId
	 */
	public void hideBackImage() {
		lin_actionbar_back.setVisibility(View.GONE);
	}

	
	/**
	 * 设置标题颜色
	 * 
	 * @param resId
	 */
	public void setTitleTextColor(int resid) {
		txv_actionbar_title.setTextColor(resid);
	}
	

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		rel_actionbar = (RelativeLayout) findViewById(R.id.rel_actionbar);
		lin_actionbar_back = (LinearLayout) findViewById(R.id.lin_actionbar_back);
		txv_actionbar_title = (TextView) findViewById(R.id.txv_actionbar_title);
		lin_actionbar_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_actionbar_back:
			mActivity.onBackPressed();
			break;
		}
	}

}
