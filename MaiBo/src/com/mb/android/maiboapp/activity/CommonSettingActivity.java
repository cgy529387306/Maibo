package com.mb.android.maiboapp.activity;

import android.os.Bundle;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.widget.SlipButton;
import com.mb.android.widget.SlipButton.OnChangedListener;
import com.tandy.android.fw2.utils.PreferencesHelper;
/**
 * 通用设置
 * @author cgy
 */
public class CommonSettingActivity extends BaseSwipeBackActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("通用设置");
		setContentView(R.layout.activity_common_setting);
		initViews();
	}

	private void initViews() {
		boolean isOpen = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_OPEN_SOUND,true);
		SlipButton slipButton = findView(R.id.slipBtn);
		slipButton.setState(isOpen);
		slipButton.SetOnChangedListener(new OnChangedListener() {
			@Override
			public void OnChanged(boolean CheckState) {
				PreferencesHelper.getInstance().putBoolean(ProjectConstants.Preferences.KEY_IS_OPEN_SOUND, CheckState);
			}
		});
	}
}
