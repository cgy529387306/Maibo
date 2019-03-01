package com.mb.android.maiboapp.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.CommonEntity;
import com.mb.android.maiboapp.utils.ProgressLoadingHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.JsonHelper;
import com.tandy.android.fw2.utils.ToastHelper;

public class FeedbackActivity extends BaseSwipeBackActivity {
	private EditText mInputView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("意见反馈");
		setRightButton("发送", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String content = mInputView.getText().toString().trim();
				if (Helper.isEmpty(content)) {
					ToastHelper.showToast("请输入反馈内容");
				}else {
					 ProgressLoadingHelper.showLoadingDialog(FeedbackActivity.this);
					 HashMap<String, String> requestMap = new HashMap<String, String>();
				     requestMap.put("content", content);
				     get(ProjectConstants.Url.FEEDBACK, requestMap);
				}
			}
		});
		setContentView(R.layout.activity_feedback);
		initUI();
	}
	
	private void initUI() {
		mInputView = findView(R.id.add_text_input);
    }

	@Override
    public boolean onResponseSuccess(int gact, String response,
                                     Object... extras) {
        if (super.onResponseSuccess(gact, response, extras)) {
            return true;
        }
        ProgressLoadingHelper.dismissLoadingDialog();
        CommonEntity entity = JsonHelper.fromJson(response, CommonEntity.class);
		if (Helper.isNotEmpty(entity)) {
			if ("0".equals(entity.getResuleCode())) {
				finish();
			}
			ToastHelper.showToast(entity.getResultMessage());
			return true;
		}
        return true;
    }

    @Override
    public boolean onResponseError(int gact, String response,
                                   VolleyError error, Object... extras) {
    	ProgressLoadingHelper.dismissLoadingDialog();
    	ToastHelper.showToast("发送失败");
    	return true;
    }
    
}
