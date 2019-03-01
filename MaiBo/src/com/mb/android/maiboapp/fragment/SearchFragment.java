package com.mb.android.maiboapp.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.mb.android.maiboapp.BaseNetFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.widget.ClearableEditText;
import com.tandy.android.fw2.utils.Helper;

/**
 * 搜索
 */
public class SearchFragment extends BaseNetFragment {
	private TextView txv_search_user,txv_search_weibos;
	private View line_search_user,line_search_weibos;
	private TextView txv_search_button;
	private ClearableEditText edt_search_input;
	public String searchKey = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frg_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	initUI();
    	showWeibos();
    }
    
    private void initUI() {
    	RelativeLayout rel_search_user = findView(R.id.rel_search_user);
        RelativeLayout rel_search_weibos = findView(R.id.rel_search_weibos);
        txv_search_user = findView(R.id.txv_search_user);
        txv_search_weibos = findView(R.id.txv_search_weibos);
        line_search_user = findView(R.id.line_search_user);
        line_search_weibos = findView(R.id.line_search_weibos);
        txv_search_button = findView(R.id.txv_search_button);
        edt_search_input = findView(R.id.edt_search_input);
        
        rel_search_user.setOnClickListener(mClickListener);
        rel_search_weibos.setOnClickListener(mClickListener);
        txv_search_button.setOnClickListener(mClickListener);
        
        edt_search_input.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					doSearch();
					return true;
				}
				return false;
			}
		});
    }
    
    private void showUser(){
    	txv_search_user.setSelected(true);
    	txv_search_weibos.setSelected(false);
    	line_search_user.setVisibility(View.VISIBLE);
    	line_search_weibos.setVisibility(View.GONE);
    	replaceFragmentTo(SearchUserFragment.class.getName());
    }
    
    private void showWeibos(){
    	txv_search_user.setSelected(false);
    	txv_search_weibos.setSelected(true);
    	line_search_user.setVisibility(View.GONE);
    	line_search_weibos.setVisibility(View.VISIBLE);
    	replaceFragmentTo(SearchWeibosFragment.class.getName());
    }
    
    /**
     * 替换当前页面的Fragment为指定目标
     *
     * @param fragmentName
     */
    private void replaceFragmentTo(String fragmentName) {
        Fragment mFragment = getChildFragmentManager().findFragmentByTag(
                fragmentName);
        if (Helper.isNull(mFragment)) {
            try {
                mFragment = Fragment.instantiate(getActivity(),fragmentName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Helper.isNotNull(mFragment)) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.frg_content, mFragment, fragmentName);
            ft.commitAllowingStateLoss();
        }
    }
    
    
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rel_search_user:
                    showUser();
                    break;
                case R.id.rel_search_weibos:
                    showWeibos();
                    break;
                case R.id.txv_search_button:
                	doSearch();
                	break;
                default:
                    break;
            }
        }
    };

    private void doSearch(){
    	searchKey = edt_search_input.getText().toString().trim();
    	if (Helper.isNotEmpty(getVisibleFragment())) {
			if (getVisibleFragment() instanceof SearchUserFragment) {
				((SearchUserFragment)getVisibleFragment()).reload();
			}else if (getVisibleFragment() instanceof SearchWeibosFragment) {
				((SearchWeibosFragment)getVisibleFragment()).reload();
			}
		}
    }
    
    public Fragment getVisibleFragment(){
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
    
}
