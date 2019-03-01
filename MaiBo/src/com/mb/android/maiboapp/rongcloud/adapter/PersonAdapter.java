package com.mb.android.maiboapp.rongcloud.adapter;

import io.rong.imkit.RongIM;

import java.util.List;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.entity.UserEntity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.ToastHelper;

public class PersonAdapter extends BaseAdapter implements SectionIndexer {
	private Context mContext;
	private String[] mNicks;
	private List<UserEntity> list;

	public PersonAdapter(Context mContext, List<UserEntity> list) {
		this.mContext = mContext;
		initNameArray(list);
		// 排序(实现了中英文混排)
		// Arrays.sort(mNicks, new PinyinComparator());//数据源可事先排好顺序
	}

	/**
	 * 排序
	 * 
	 * @param arrayList
	 * @return
	 */
	private List<UserEntity> sortContacts(List<UserEntity> arrayList) {
		int y = arrayList.size();
		for (int i = 0; i < arrayList.size(); i++) {
			for (int j = 0; j < y - 1; j++) {
				UserEntity map1 = arrayList.get(j);
				UserEntity map2 = arrayList.get(j + 1);
				if (converterToFirstSpell(map1.getUser_name().toString())
						.compareTo(
								converterToFirstSpell(map2.getUser_name()
										.toString())) >= 0) {
					arrayList.set(j, map2);
					arrayList.set(j + 1, map1);
				}
			}
			y--;
		}
		return arrayList;
	}

	private void initNameArray(List<UserEntity> list) {
		this.list = sortContacts(list);
		mNicks = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			mNicks[i] = list.get(i).getUser_name().toString();
		}
	}

	@Override
	public int getCount() {
		return mNicks.length;
	}

	@Override
	public Object getItem(int position) {
		return mNicks[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String nickName = mNicks[position];
		ViewHolder viewHolder = null;
		// if (convertView == null) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.mm_item_lv_contact, null);
		viewHolder = new ViewHolder();
		viewHolder.tvCatalog = (TextView) convertView
				.findViewById(R.id.contactitem_catalog);
		viewHolder.ivAvatar = (ImageView) convertView
				.findViewById(R.id.contactitem_avatar_iv);
		viewHolder.tvNick = (TextView) convertView
				.findViewById(R.id.contactitem_nick);
		viewHolder.tvPhone = (TextView) convertView
				.findViewById(R.id.contactitem_phone);
		viewHolder.llContent = (LinearLayout) convertView
				.findViewById(R.id.contactitem_layout);
		convertView.setTag(viewHolder);
		// } else {
		// viewHolder = (ViewHolder) convertView.getTag();
		// }
		String catalog = converterToFirstSpell(nickName).substring(0, 1);
		if (position == 0) {
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(catalog);
			viewHolder.tvCatalog.setTag(catalog);
		} else {
			String lastCatalog = converterToFirstSpell(mNicks[position - 1])
					.substring(0, 1);
			if (catalog.equals(lastCatalog)) {// 如果首字母与前一个item相同则隐藏字母栏
				viewHolder.tvCatalog.setVisibility(View.GONE);
				viewHolder.tvCatalog.setTag(catalog);
			} else {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
				viewHolder.tvCatalog.setTag(catalog);
			}
		}

		ImageLoader.getInstance().displayImage(
				list.get(position).getAvatar_hd(), viewHolder.ivAvatar);
		viewHolder.tvNick.setText(list.get(position).getUser_name().toString());
		// MyListener listener = new MyListener(position);
		// viewHolder.llContent.setOnClickListener(listener);

		return convertView;
	}

	private class MyListener implements OnClickListener {

		private int position;

		public MyListener(int position) {
			super();
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			ToastHelper.showToast(list.get(position).toString());
			String member_id = list.get(position).getMember_id().toString();
			String user_name = list.get(position).getUser_name().toString();
			/**
			 * 启动单聊界面。
			 * 
			 * @param context
			 *            应用上下文。
			 * @param targetUserId
			 *            要与之聊天的用户 Id。
			 * @param title
			 *            聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
			 */
			RongIM.getInstance().startPrivateChat(mContext, member_id,
					user_name);
		}

	}

	/**
	 * 转化为拼音
	 * 
	 * @param chines
	 * @return
	 */
	public String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if (Helper.textFilter(chines).equals("")) {
			chines = "未知";
		}
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName.toUpperCase();
	}

	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView tvPhone;// 昵称
		LinearLayout llContent;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < mNicks.length; i++) {
			String l = converterToFirstSpell(mNicks[i]).substring(0, 1);
			char firstChar = l.toUpperCase(Locale.CHINA).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}
