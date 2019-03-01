package com.mb.android.maiboapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.activity.WeiboDetailActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.MessageCommentResp.MsgCommentEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class MsgCommentAdapter extends BaseAdapter {
	private Activity activity;
	private List<MsgCommentEntity> dataList = new ArrayList<MsgCommentEntity>();

	public MsgCommentAdapter(Activity act, List<MsgCommentEntity> list) {
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_msg_good, null);
			holder.imv_gooder_avater = (ImageView) convertView.findViewById(R.id.imv_gooder_avater);
			holder.txv_gooder_name = (TextView) convertView.findViewById(R.id.txv_gooder_name);
			holder.txv_gooder_time = (TextView) convertView.findViewById(R.id.txv_gooder_time);
			holder.txv_weibo_comment = (TextView) convertView.findViewById(R.id.txv_weibo_comment);
			
			holder.imv_user_avatar = (ImageView) convertView.findViewById(R.id.imv_user_avatar);
			holder.txv_user_name = (TextView) convertView.findViewById(R.id.txv_user_name);
			holder.txv_weibo_content = (TextView) convertView.findViewById(R.id.txv_weibo_content);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MsgCommentEntity entity = (MsgCommentEntity) getItem(position);
		holder.txv_gooder_name.setText(entity.getComment().getMember().getUser_name());
		ImageLoader.getInstance().displayImage(entity.getComment().getMember().getAvatar_large(),holder.imv_gooder_avater);
		holder.txv_gooder_time.setText(entity.getComment().getAdd_time());
		holder.txv_weibo_comment.setText(entity.getComment().getContent());
		holder.txv_user_name.setText("@"+entity.getStatues().getMember().getUser_name());
		if (Helper.isEmpty(entity.getStatues().getPhotos())) {
			holder.imv_user_avatar.setVisibility(View.GONE);
		}else {
			holder.imv_user_avatar.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(entity.getStatues().getPhotos().get(0).getBmiddle(),holder.imv_user_avatar);
		}
		holder.txv_weibo_content.setText(entity.getStatues().getContent());
		
		holder.imv_gooder_avater.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID, entity.getComment().getMember().getMember_id());
				bundle.putString(ProjectConstants.BundleExtra.KEY_USER_NAME, entity.getComment().getMember().getUser_name());
				NavigationHelper.startActivity(activity, UserProfileActivity.class, bundle, false);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle=new Bundle();
				bundle.putString("id", entity.getStatues().getId());
				NavigationHelper.startActivity(activity,
						WeiboDetailActivity.class, bundle, false);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView imv_gooder_avater;
		TextView txv_gooder_name;
		TextView txv_gooder_time;
		TextView txv_weibo_comment;
		
		ImageView imv_user_avatar;
		TextView txv_user_name;
		TextView txv_weibo_content;
	}
}
