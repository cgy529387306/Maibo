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

import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserLongMbActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.ArticleEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.TextLinkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class ArticleAdapter extends BaseAdapter{
	private Activity activity;
	private List<ArticleEntity> dataList = new ArrayList<ArticleEntity>();
	public ArticleAdapter(Activity act,List<ArticleEntity> list) {
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_article, null);
			holder.imv_article_thumb = (ImageView) convertView.findViewById(R.id.imv_article_thumb);
			holder.txv_article_title = (TextView) convertView.findViewById(R.id.txv_article_title);
			holder.txv_article_content = (TextView) convertView.findViewById(R.id.txv_article_content);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ArticleEntity entity = (ArticleEntity) getItem(position);
		ImageLoader.getInstance().displayImage(entity.getImg_url(), holder.imv_article_thumb);
		holder.txv_article_title.setText(entity.getTitle());
		TextLinkUtil.handleMemoContent(holder.txv_article_content,
				entity.getContent(), activity);
		return convertView;
	}
	
	static class ViewHolder{
		ImageView imv_article_thumb;
		TextView txv_article_title;
		TextView txv_article_content;
	}
	

}
