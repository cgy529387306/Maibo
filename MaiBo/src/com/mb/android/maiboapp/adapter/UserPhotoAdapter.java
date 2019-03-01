package com.mb.android.maiboapp.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.rongcloud.activity.ImagePagerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class UserPhotoAdapter extends BaseAdapter{
	private Activity activity;
	private List<PhotosEntity> dataList = new ArrayList<PhotosEntity>();
	public UserPhotoAdapter(Activity act, List<PhotosEntity> list) {
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_user_photo, null);
			holder.imv_user_photo = (ImageView) convertView.findViewById(R.id.imv_user_photo);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		PhotosEntity entity = (PhotosEntity) getItem(position);
		ImageLoader.getInstance().displayImage(entity.getBmiddle(), holder.imv_user_photo);
		final int pos = position;
		final List<String> paths = new ArrayList<String>();
		for (int i = 0; i < dataList.size(); i++) {
			paths.add(dataList.get(i).getBmiddle());
		}
		holder.imv_user_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ImagePagerActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable) paths);
				bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, pos);
				intent.putExtras(bundle);
				activity.startActivity(intent);					
			}
		});
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imv_user_photo;
	}
}
