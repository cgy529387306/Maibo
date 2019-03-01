package com.mb.android.maiboapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.PreferencesHelper;

public class NotifyAdapter extends BaseAdapter{
	private Activity activity;
	private List<String> dataList = new ArrayList<String>();
	private int selectIndex = 0;
	public NotifyAdapter(Activity act, List<String> list) {
		this.activity = act;
		this.dataList = list;
	}
	
	public void updateState(int index){
		selectIndex = index;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public String getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (Helper.isNull(convertView)) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_notify, null);
			holder.imv_is_select = (ImageView) convertView.findViewById(R.id.imv_is_select);
			holder.txv_title = (TextView) convertView.findViewById(R.id.txv_title);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txv_title.setText(getItem(position));
		selectIndex = PreferencesHelper.getInstance().getInt(ProjectConstants.Preferences.KEY_NOTIFY_SETTING, 0);
		if (position == selectIndex) {
			holder.imv_is_select.setVisibility(View.VISIBLE);
		}else {
			holder.imv_is_select.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imv_is_select;
		TextView txv_title;
	}
}
