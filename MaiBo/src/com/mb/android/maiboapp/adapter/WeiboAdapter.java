package com.mb.android.maiboapp.adapter;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.activity.WeiboDetailActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.mb.android.maiboapp.utils.ProjectHelper;
import com.mb.android.maiboapp.utils.TextLinkUtil;
import com.mb.android.maiboapp.utils.TimeUtil;
import com.mb.android.widget.GridListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

public class WeiboAdapter extends BaseAdapter {
	private Activity activity;
	private ItemHandler itemHandler;
	private List<WeiboEntity> list;
	private int type;
	public WeiboAdapter(Activity act, List<WeiboEntity> list,int type) {
		this.activity = act;
		this.list = list;
		this.type = type;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setList(List<WeiboEntity> list) {
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (Helper.isNull(convertView)) {
		ViewHolder holder = new ViewHolder();
		convertView = LayoutInflater.from(activity).inflate(
				R.layout.item_weibo, parent, false);
		holder.grd_item = (GridListView) convertView
				.findViewById(R.id.grd_item);
		holder.imv_user_avater = (ImageView) convertView
				.findViewById(R.id.imv_user_avater);
		holder.txv_user_name = (TextView) convertView
				.findViewById(R.id.txv_user_name);
		holder.txv_weibo_time = (TextView) convertView
				.findViewById(R.id.txv_weibo_time);
		holder.txv_weibo_source = (TextView) convertView
				.findViewById(R.id.txv_weibo_source);
		holder.txv_weibo_content = (TextView) convertView
				.findViewById(R.id.txv_weibo_content);
		holder.lin_weibo_repost = (LinearLayout) convertView
				.findViewById(R.id.lin_weibo_repost);
		holder.lin_weibo_comment = (LinearLayout) convertView
				.findViewById(R.id.lin_weibo_comment);
		holder.lin_weibo_good = (LinearLayout) convertView
				.findViewById(R.id.lin_weibo_good);
		holder.txv_weibo_repost = (TextView) convertView
				.findViewById(R.id.txv_weibo_repost);
		holder.txv_weibo_comment = (TextView) convertView
				.findViewById(R.id.txv_weibo_comment);
		holder.txv_weibo_good = (TextView) convertView
				.findViewById(R.id.txv_weibo_good);
		holder.imv_weibo_good = (ImageView) convertView
				.findViewById(R.id.imv_weibo_good);
		holder.frame_relay = (FrameLayout) convertView
				.findViewById(R.id.frame_relay);
		holder.imv_weibo_operate = (ImageView) convertView.findViewById(R.id.imv_weibo_operate);
		if (type == 0) {
			holder.imv_weibo_operate.setVisibility(View.VISIBLE);
		}else {
			holder.imv_weibo_operate.setVisibility(View.GONE);
		}
		holder.txv_weibo_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                CharSequence text = ((TextView) v).getText();
                Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                TextView widget = (TextView) v;
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        ret = true;
                    }
                }
                return ret;
            }
        });
		final WeiboEntity entity = list.get(position);
		List<PhotosEntity> photos = entity.getPhotos();
		WeiboEntity relay = entity.getRelay();
		if (relay != null && relay.getId() != null) {
			ViewHolder2 holder2 = new ViewHolder2();
			View view = LayoutInflater.from(activity).inflate(
					R.layout.view_weibo_relay, null);
			holder.frame_relay.addView(view);
			final String id = relay.getId();
			holder.frame_relay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putString("id", id);
					NavigationHelper.startActivity(activity,
							WeiboDetailActivity.class, bundle, false);
				}
			});
			holder2.txv_weibo_content1 = (TextView) view
					.findViewById(R.id.txv_weibo_content1);
			holder2.grd_item1 = (GridListView) view
					.findViewById(R.id.grd_item1);
			holder2.txv_weibo_content1.setText("@"+relay.getMember().getUser_name()
					+ ": " + relay.getContent());
			TextLinkUtil.handleMemoContent(holder2.txv_weibo_content1,
					"@"+relay.getMember().getUser_name()
					+ ": " + relay.getContent(), activity);
			holder.grd_item.setVisibility(View.GONE);
			List<PhotosEntity> photos2 = relay.getPhotos();
			holder2.grd_item1.setData(photos2);
		} else {
			if (photos != null && photos.size() > 0) {
				holder.grd_item.setData(photos);
			} else {
				holder.grd_item.setVisibility(View.GONE);
			}
		}
		if (Helper.isNotNull(entity.getMember())) {
			ImageLoader.getInstance().displayImage(
					entity.getMember().getAvatar_large(),
					holder.imv_user_avater);
		}
		holder.txv_weibo_time.setText(TimeUtil.getTimeStr(entity.getAdd_time()));
		TextLinkUtil.handleMemoContent(holder.txv_weibo_content,
				entity.getContent(), activity);
		holder.txv_weibo_comment.setText(entity.getComment_num());
		holder.txv_weibo_good.setText(entity.getPraise_num());
		holder.txv_weibo_repost.setText(entity.getRelay_num());
		holder.txv_user_name.setText(entity.getMember().getUser_name());
		if (Helper.isEmpty(entity.getSource_name())) {
			holder.txv_weibo_source.setText("来自脉搏");
		} else {
			holder.txv_weibo_source.setText("来自" + entity.getSource_name());
		}
		if (entity.getIsPraise() > 0) {
			holder.imv_weibo_good.setImageDrawable(activity.getResources()
					.getDrawable(R.drawable.ic_weibo_good_selected));
			holder.imv_weibo_good.setTag(true);
		} else {
			holder.imv_weibo_good.setImageDrawable(activity.getResources()
					.getDrawable(R.drawable.ic_weibo_good));
			holder.imv_weibo_good.setTag(false);
		}
		MyListener listener = new MyListener(position, holder);
		holder.lin_weibo_repost.setOnClickListener(listener);
		holder.lin_weibo_comment.setOnClickListener(listener);
		holder.lin_weibo_good.setOnClickListener(listener);
		holder.imv_user_avater.setOnClickListener(listener);
		holder.imv_weibo_operate.setOnClickListener(listener);
		return convertView;
	}

	private class MyListener implements OnClickListener {
		private int position;
		private ViewHolder holder;

		public MyListener(int position, ViewHolder holder) {
			super();
			this.position = position;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			if (Helper.isNotNull(itemHandler)) {
				switch (v.getId()) {
				case R.id.lin_weibo_repost:
					itemHandler.onRepost(position);
					break;
				case R.id.lin_weibo_comment:
					itemHandler.onComment(position);
					break;
				case R.id.lin_weibo_good:
					ProjectHelper.startScaleAnimation(holder.imv_weibo_good);
					if (Helper.isNotNull(itemHandler)) {
						itemHandler.onGood(position);
						int num = Integer.parseInt(holder.txv_weibo_good
								.getText().toString());
						boolean isPraise = (Boolean) holder.imv_weibo_good
								.getTag();
						if (isPraise) {
							holder.imv_weibo_good.setImageDrawable(activity
									.getResources().getDrawable(
											R.drawable.ic_weibo_good));
							holder.txv_weibo_good.setText((num - 1) + "");
							holder.imv_weibo_good.setTag(false);
						} else {
							holder.imv_weibo_good.setImageDrawable(activity
									.getResources().getDrawable(
											R.drawable.ic_weibo_good_selected));
							holder.txv_weibo_good.setText((num + 1) + "");
							holder.imv_weibo_good.setTag(true);
						}
					}
					break;
				case R.id.imv_user_avater:
					WeiboEntity entity = list.get(position);
					Bundle bundle = new Bundle();
					bundle.putString(ProjectConstants.BundleExtra.KEY_USER_ID,
							entity.getMember().getMember_id());
					bundle.putString(
							ProjectConstants.BundleExtra.KEY_USER_NAME, entity
									.getMember().getUser_name());
					NavigationHelper.startActivity(activity,
							UserProfileActivity.class, bundle, false);
					break;
				case R.id.imv_weibo_operate:
					itemHandler.onDelete(position);
				default:
					break;
				}
			}
		}

	}

	static class ViewHolder {
		GridListView grd_item;
		ImageView imv_user_avater;
		TextView txv_user_name;
		TextView txv_weibo_time;
		TextView txv_weibo_source;
		TextView txv_weibo_content;
		LinearLayout lin_weibo_repost;
		LinearLayout lin_weibo_comment;
		LinearLayout lin_weibo_good;
		ImageView imv_weibo_operate;
		TextView txv_weibo_repost;
		TextView txv_weibo_comment;
		TextView txv_weibo_good;
		ImageView imv_weibo_good;
		FrameLayout frame_relay;
	}

	static class ViewHolder2 {
		GridListView grd_item1;
		ImageView imv_user_avater1;
		TextView txv_user_name1;
		TextView txv_weibo_time1;
		TextView txv_weibo_source1;
		TextView txv_weibo_content1;
		LinearLayout lin_weibo_repost1;
		LinearLayout lin_weibo_comment1;
		LinearLayout lin_weibo_good1;
		TextView txv_weibo_repost1;
		TextView txv_weibo_comment1;
		TextView txv_weibo_good1;
		ImageView imv_weibo_good1;
	}

	public void setItemHandler(ItemHandler itemHandler) {
		this.itemHandler = itemHandler;
	}

	public interface ItemHandler {
		// 转发
		void onRepost(int pos);

		// 评论
		void onComment(int pos);

		// 点赞
		void onGood(int pos);
		
		// 删除
		void onDelete(int pos);
	}
}
