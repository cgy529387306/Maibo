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
import com.mb.android.maiboapp.db.UserHistoryEntity;
import com.mb.android.maiboapp.entity.UserEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class AccountAdapter extends BaseAdapter{
	private Activity activity;
	private List<UserHistoryEntity> dataList = new ArrayList<UserHistoryEntity>();
	public AccountAdapter(Activity act, List<UserHistoryEntity> list) {
		this.activity = act;
		this.dataList = list;
	}
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_account, null);
			holder.imv_user_avatar = (ImageView) convertView.findViewById(R.id.imv_user_avatar);
			holder.is_check = (ImageView) convertView.findViewById(R.id.is_check);
			holder.txv_user_name = (TextView) convertView.findViewById(R.id.txv_user_name);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserHistoryEntity entity = (UserHistoryEntity) getItem(position);
		holder.txv_user_name.setText(entity.getUser_name());
		ImageLoader.getInstance().displayImage(entity.getAvatar_large(), holder.imv_user_avatar);
		if (UserEntity.getInstance().getMember_id().equals(entity.getMember_id())) {
			holder.is_check.setVisibility(View.VISIBLE);
		}else {
			holder.is_check.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imv_user_avatar;
		ImageView is_check;
		TextView txv_user_name;
	}
}
