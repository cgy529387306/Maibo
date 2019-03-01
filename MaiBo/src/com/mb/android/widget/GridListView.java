package com.mb.android.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.mb.android.maiboapp.entity.PhotosEntity;
import com.mb.android.maiboapp.rongcloud.activity.ImagePagerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GridListView extends ViewGroup {

	/**
	 * 子项大小
	 */
	private int mItemWidth;
	/**
	 * 间隙大小
	 */
	private int mPadding = 0;
	/**
	 * 列数
	 */
	private int mClunNum = 0;
	/**
	 * 数据源
	 */
	private List<PhotosEntity> mData;

	public GridListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPadding = (int) (4 * getResources().getDisplayMetrics().density);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 宽度获取
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		mItemWidth = (widthSize - 2 * mPadding) / 3;

		// 测量子View
		int childCount = getChildCount();
		if (1 == childCount) {
			View child = getChildAt(0);
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			int height = child.getMeasuredHeight();
			setMeasuredDimension(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
		} else {
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);

				child.measure(MeasureSpec.makeMeasureSpec(mItemWidth,
						MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						mItemWidth, MeasureSpec.EXACTLY));
			}
			// 测量当前View
			if (mClunNum == 0) {
				mClunNum = 1;
			}
			int rowNum = childCount / mClunNum;
			rowNum = childCount % mClunNum == 0 ? rowNum : rowNum + 1;
			int heightSize = (rowNum - 1) * mPadding + rowNum * mItemWidth;
			setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
					heightSize, MeasureSpec.EXACTLY));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		if (count == 1) {
			View view = getChildAt(0);
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		} else {
			layoutChild(count, mClunNum);
		}
	}

	private void layoutChild(int childCount, int clunNum) {
		if (clunNum == 0) {
			return;
		}
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			int xPosition = i % clunNum;
			int yPosition = i / clunNum;
			int l = mPadding * xPosition + mItemWidth * xPosition;
			int t = mPadding * yPosition + mItemWidth * yPosition;
			childView.layout(l, t, l + mItemWidth, t + mItemWidth);
		}
	}

	/**
	 * 设置数据源
	 * 
	 * @param photos
	 */
	public void setData(List<PhotosEntity> photos) {
		this.mData = photos;
		if (null == photos) {
			return;
		}
		int size = photos.size();
		if (size == 0) {
			return;
		}

		if (size == 1) {
			mClunNum = 1;
		} else {
			mClunNum = 3;
		}

		removeAllViews();
		// 添加子View
		final List<String> paths = new ArrayList<String>();
		for (int i = 0; i < mData.size(); i++) {
			paths.add(mData.get(i).getPicture_img());
		}
		for (int i = 0; i < mData.size(); i++) {
			final int pos = i;
			ImageView imvItem = new ImageView(getContext());
			imvItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			// imvItem.setImageResource(mData.get(i).get);
			// imvItem.setBackgroundResource(R.drawable.bg_imv);
//			imvItem.setScaleType(ScaleType.CENTER_CROP);
			ImageLoader.getInstance().displayImage(
					mData.get(i).getPicture_img(), imvItem);
			imvItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getContext(), ImagePagerActivity.class);
					Bundle bundle=new Bundle();
					bundle.putSerializable(ImagePagerActivity.EXTRA_IMAGE_URLS, (Serializable) paths);
					bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, pos);
					intent.putExtras(bundle);
					getContext().startActivity(intent);					
				}
			});
			imvItem.setScaleType(ScaleType.CENTER_CROP);
			addView(imvItem);
		}
	}
}