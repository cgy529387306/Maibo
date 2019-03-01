package com.mb.android.maiboapp.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mb.android.maiboapp.BaseSwipeBackActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.adapter.NotifyAdapter;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.tandy.android.fw2.utils.PreferencesHelper;
/**
 * 获取新消息
 * @author cgy
 *
 */
public class NotifyActivity extends BaseSwipeBackActivity {
	private ListView lsv_notify;
	private NotifyAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomTitle("获取新消息");
		setContentView(R.layout.activity_notify);
		initUI();
	}
	
	private void initUI() {
		lsv_notify = findView(R.id.lsv_notify);
		List<String> list = new ArrayList<String>();
		list.add("每半分钟");
		list.add("每2分钟");
		list.add("每5分钟");
		mAdapter = new NotifyAdapter(this, list);
		lsv_notify.setAdapter(mAdapter);
		lsv_notify.setOnItemClickListener(mItemClickListener);
    }
	
	/**
	 * 列表点击事件监听者
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int pos = position - lsv_notify.getHeaderViewsCount();
			PreferencesHelper.getInstance().putInt(ProjectConstants.Preferences.KEY_NOTIFY_SETTING, pos);
			mAdapter.notifyDataSetChanged();
		}
	};


}
