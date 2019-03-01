package com.mb.android.maiboapp.fragment;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mb.android.maiboapp.R;

public class ConversationListDynamicFragment extends Fragment {
	
	@Override
	public void onResume() {
		super.onResume();
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.conversationlist, container, false);
		return view;
	}
	
	private void init(){
		ConversationListFragment fragment = new ConversationListFragment();
		Uri uri = Uri
				.parse("rong://"
						+ getActivity().getApplicationInfo().packageName)
				.buildUpon()
				.appendPath("conversationlist")
				.appendQueryParameter(
						Conversation.ConversationType.PRIVATE.getName(),
						"false") // 设置私聊会话非聚合显示
				.appendQueryParameter(
						Conversation.ConversationType.GROUP.getName(), "true")// 设置群组会话聚合显示
				.appendQueryParameter(
						Conversation.ConversationType.DISCUSSION.getName(),
						"false")// 设置讨论组会话非聚合显示
				.appendQueryParameter(
						Conversation.ConversationType.SYSTEM.getName(), "false")// 设置系统会话非聚合显示
				.build();
		fragment.setUri(uri);

		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.add(R.id.rong_content, fragment);
		transaction.commit();
	}
}