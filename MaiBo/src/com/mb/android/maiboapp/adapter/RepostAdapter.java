package com.mb.android.maiboapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.TextLinkUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

/**
 * 转发列表
 * 
 * @author yw
 * 
 */
public class RepostAdapter extends BaseAdapter {

	private List<WeiboEntity> list;
	private Context context;

	public RepostAdapter(List<WeiboEntity> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setList(List<WeiboEntity> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_comments, null);
			holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			holder.txv_user_name = (TextView) convertView
					.findViewById(R.id.txv_user_name);
			holder.txv_add_time = (TextView) convertView
					.findViewById(R.id.txv_add_time);
			holder.txv_content = (TextView) convertView
					.findViewById(R.id.txv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WeiboEntity entity = list.get(position);
		holder.txv_user_name.setText(entity.getMember().getUser_name());
		TextLinkUtil.handleMemoContent(holder.txv_content,
				entity.getContent(), context);
		holder.txv_add_time.setText(entity.getAdd_time());
		if (Helper.isNotNull(entity.getMember())) {
			ImageLoader.getInstance().displayImage(
					entity.getMember().getAvatar_large(), holder.iv_head);
		}

		return convertView;
	}

	class ViewHolder {
		TextView txv_user_name;
		TextView txv_add_time;
		TextView txv_content;
		ImageView iv_head;
	}
}
