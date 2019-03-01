package com.mb.android.maiboapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserLoginActivity;
import com.mb.android.maiboapp.activity.UserRegisterActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.utils.NavigationHelper;

public class UserBlockToLoginFragment extends BaseNetFragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frg_user_to_login, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI(view);
	}

	private void initUI(View view) {
		LinearLayout lin_fans = (LinearLayout) view.findViewById(R.id.lin_fans);
		Button btn_user_register = (Button) view.findViewById(R.id.btn_user_register);
		Button btn_user_login = (Button) view.findViewById(R.id.btn_user_login);
		
		lin_fans.setOnClickListener(mClickListener);
		btn_user_register.setOnClickListener(mClickListener);
		btn_user_login.setOnClickListener(mClickListener);
	}
	
	/**
	  * 点击事件
	  */
	 private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lin_fans:
				break;
				
			case R.id.btn_user_register:
				NavigationHelper.startActivity(getActivity(), UserRegisterActivity.class, null, false);
				break;

			case R.id.btn_user_login:
				Bundle bundle = new Bundle();
            	bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_CLOSE, false);
				NavigationHelper.startActivity(getActivity(), UserLoginActivity.class, bundle, false);
				break;
				
			default:
				break;
			}
		}
	};
}
