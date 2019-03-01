package com.mb.android.maiboapp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.RecommendEntity;
import com.mb.android.maiboapp.utils.NavigationHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tandy.android.fw2.utils.Helper;

/**
 * 头部轮播适配器
 * @author cgy
 *
 */
public class HeadWheelAdapter extends PagerAdapter {

	private Context mContext;
	private List<RecommendEntity> mList;
	public HeadWheelAdapter(Context context,List<RecommendEntity> list) {
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return  mList.size();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		ImageView imvItem = obtainHeadWheelItems(mList.get(position%mList.size()));
        ((ViewPager) container).addView(imvItem);  
        return imvItem;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	/**
	 * 获取头部轮播子项
	 * @param recommendTopList
	 * @return
	 */
	private ImageView obtainHeadWheelItems(final RecommendEntity entity) {
			
			ImageView imvItem = new ImageView(mContext);
			imvItem.setScaleType(ScaleType.FIT_XY);
			imvItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(Helper.isNotEmpty(entity.getUrl())){
						Bundle bundle = new Bundle();
						bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, entity.getUrl());
						bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_TITLE, entity.getTitle());
						bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, true);
						NavigationHelper.startActivity((Activity)mContext,BaseWebViewActivity.class, bundle, false);
					}
				}
			});
			ImageLoader.getInstance().displayImage(entity.getImg_url(),imvItem);
			return imvItem;
	}
	
}