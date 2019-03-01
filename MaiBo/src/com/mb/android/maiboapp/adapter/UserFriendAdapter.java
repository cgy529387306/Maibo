package com.mb.android.maiboapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.entity.UserEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class UserFriendAdapter extends BaseAdapter{
	private Activity activity;
	private ItemHandler itemHandler;
	private List<UserEntity> dataList = new ArrayList<UserEntity>();
	public UserFriendAdapter(Activity act,List<UserEntity> list) {
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_user_follow, parent,false);
			holder.imv_follow = (ImageView) convertView.findViewById(R.id.imv_follow);
			holder.imv_user_avatar = (ImageView) convertView.findViewById(R.id.imv_user_avatar);
			holder.txv_user_name = (TextView) convertView.findViewById(R.id.txv_user_name);
			holder.txv_user_weibo = (TextView) convertView.findViewById(R.id.txv_user_weibo);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserEntity entity = (UserEntity) getItem(position);
		ImageLoader.getInstance().displayImage(entity.getAvatar_large(), holder.imv_user_avatar);
		holder.txv_user_name.setText(entity.getUser_name());
		holder.txv_user_weibo.setText(entity.getAiring());
	    if (entity.getFollow_state() == 0 || entity.getFollow_state() == 2) {
			holder.imv_follow.setImageResource(R.drawable.ic_follow_add);
		}else{
			holder.imv_follow.setImageResource(R.drawable.ic_follow_yes);
		}
		final int pos = position;
		holder.imv_follow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				itemHandler.onFollow(pos);
			}
		});
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imv_user_avatar;
		ImageView imv_follow;
		TextView txv_user_name;
		TextView txv_user_weibo;
	}
	
	public void setItemHandler(ItemHandler itemHandler) {
		this.itemHandler = itemHandler;
	}
	
	public interface ItemHandler {
		//关注
		void onFollow(int pos);
	}

}
