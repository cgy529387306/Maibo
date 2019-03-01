package com.mb.android.maiboapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mb.android.maiboapp.R;
import com.tandy.android.fw2.utils.ResourceHelper;

/**
 * Fragment ViewPager 适配器
 * 
 * @author cgy
 */
public class PagerFragmentAdapter extends FragmentPagerAdapter{
	
	private Fragment[] mPagerFragments;
	
	public PagerFragmentAdapter(FragmentManager fm, Fragment[] pagerFragments) {
		super(fm);
		mPagerFragments = pagerFragments;
	}

	@Override
	public Fragment getItem(int position) {
		return mPagerFragments[position];
	}

	@Override
	public int getCount() {
		return mPagerFragments.length;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return ResourceHelper.getString(R.string.app_name);
	}
}
