package com.handmark.pulltorefresh.library.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public abstract class AbstractRefreshAdapter<T> extends BaseAdapter {
	private LinkedList<T> mItemList = new LinkedList<T>();
	private Context mContext;
	private PullToRefreshListView mLsvCommon;
	private View mViewLoading;
	
	public PullToRefreshListView getListView() {
		return this.mLsvCommon;
	}
	
	public View getViewLoading() {
		return this.mViewLoading;
	}
	
	public void setItemList(LinkedList<T> itemList){
		this.mItemList = itemList;
	}
	
	public LinkedList<T> getItemList(){
		return this.mItemList;
	}
	
	public Context getContext(){
		return this.mContext;
	}
	public AbstractRefreshAdapter(Context context){
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		if (mItemList != null && mItemList.size() > 0){
			return mItemList.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		if (0 <= position && position < getCount()){
			return mItemList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * 添加新列表数据到头部
	 * @param list
	 * @return
	 */
	public boolean addToHead(List<T> list){
		if (list != null && list.size() > 0){
			if (this.mItemList.addAll(0, list)){
				((Activity) mContext).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
				return true;
			}
		}
		return false;
	}
	/**
	 * 添加新列表数据到尾部
	 * @param list
	 * @return
	 */
	public boolean addToFoot(List<T> list){
		if (list != null && list.size() > 0){
			if (this.mItemList.addAll(this.mItemList.size(), list)){
				((Activity) mContext).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						notifyDataSetChanged();
					}
				});
				return true;
			}
		}
		return false;
	}
	
	public void setOnItemLongClickObject(PullToRefreshListView lsvCommon, View viewLoading) {
		this.mLsvCommon = lsvCommon;
		this.mViewLoading = viewLoading;
	}
}