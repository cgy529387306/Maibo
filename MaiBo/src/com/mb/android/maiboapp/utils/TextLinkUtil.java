package com.mb.android.maiboapp.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.mb.android.maiboapp.BaseWebViewActivity;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.activity.UserProfileActivity;
import com.mb.android.maiboapp.constants.ProjectConstants;

/**
 * 姓名跳转工具类
 * 
 * @author yw
 * 
 */
public class TextLinkUtil {
	/** 备忘录TextView的名字，可点击，左标签 */
	public static final String TAG_LEFT = "@";
	/** 备忘录TextView的名字，可点击，右标签 */
	public static final String TAG_RIGHT = " ";
	public static final String TAG_RIGHT2 = ": ";
	private static Context mContext;

	/**
	 * TextView，内容中带标签部分，转成StringSpan，可点击（与EditText的处理方式不同）
	 * 
	 * @param tv
	 *            需要处理的view
	 * @param content
	 *            内容中带标签
	 * @param clickableSpan
	 *            SPAN的点击事件
	 */
	public static void handleMemoContent(TextView tv, String content,
			Context context) {
		mContext = context;
		tv.setClickable(true);
//		tv.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setFocusable(false);
		SpannableStringBuilder ss2 = setClickSpan(content, context);
		// 超链接
		tv.setText(Html.fromHtml(content));
		CharSequence text = tv.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) text;
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
//			ss2.clearSpans();// should clear old spans
			// 循环把链接发过去
			for (URLSpan url : urls) {
				MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
				ss2.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
		}
		tv.setText(ss2);
	}

	/**
	 * 算法思路： <br>
	 * 判断是否存在一对标签，<br>
	 * 如果不存在，则结束；<br>
	 * 如果存在，则将此段截取掉，处理，然后继续处理下一段<br>
	 * 
	 * @param tv
	 * @param content
	 */
	public static SpannableStringBuilder setClickSpan(String content1,
			final Context context) {
		String content=content1+" ";
		ArrayList<HashMap<String, String>> listTag = new ArrayList<HashMap<String, String>>();
		int leftTagLength = TAG_LEFT.length();
		int lastEnd = 0;
		while (content.contains(TAG_LEFT) && content.contains(TAG_RIGHT)) {
			HashMap<String, String> map = new HashMap<String, String>();
			int indexTagStart = content.indexOf(TAG_LEFT);
			int indexTagEnd = content.indexOf(TAG_RIGHT, indexTagStart);
			String s1 = content.substring(0, indexTagStart);// 名字前
			String s2 = content.substring(indexTagStart, indexTagEnd + 1);// @名字
			String id = content.substring(indexTagStart + 1, indexTagEnd);// 名字
			int currentStart = indexTagStart + lastEnd;
			int currentEnd = currentStart + s2.length();
			lastEnd = currentEnd;
			map.put("id", id);
			map.put("start", currentStart + "");
			map.put("end", currentEnd + "");
			map.put("content", s1 + s2);
			content = content.substring(indexTagEnd + leftTagLength);
			listTag.add(map);
		}
		while (content.contains(TAG_LEFT) && content.contains(TAG_RIGHT2)) {
			HashMap<String, String> map = new HashMap<String, String>();
			int indexTagStart = content.indexOf(TAG_LEFT);
			int indexTagEnd = content.indexOf(TAG_RIGHT2, indexTagStart);
			String s1 = content.substring(0, indexTagStart);// 名字前
			String s2 = content.substring(indexTagStart, indexTagEnd + 1);// @名字
			String id = content.substring(indexTagStart + 1, indexTagEnd);// 名字
			int currentStart = indexTagStart + lastEnd;
			int currentEnd = currentStart + s2.length();
			lastEnd = currentEnd;
			map.put("id", id);
			map.put("start", currentStart + "");
			map.put("end", currentEnd + "");
			map.put("content", s1 + s2);
			content = content.substring(indexTagEnd + leftTagLength);
			listTag.add(map);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listTag.size(); i++) {
			String s2 = listTag.get(i).get("content");
			sb.append(s2);
		}
		// ImageSpan;
		SpannableStringBuilder ss2 = new SpannableStringBuilder(sb.toString()
				+ content);
		for (int i = 0; i < listTag.size(); i++) {
			int start = Integer.parseInt(listTag.get(i).get("start"));
			int end = Integer.parseInt(listTag.get(i).get("end"));
			final String user_name = listTag.get(i).get("id");
			ss2.setSpan(new ClickableSpan() {// 点击事件
						@Override
						public void onClick(View widget) {
							Bundle bundle = new Bundle();
							bundle.putString(
									ProjectConstants.BundleExtra.KEY_USER_ID,
									"");
							String name = user_name.replace(":", "");
							bundle.putString(
									ProjectConstants.BundleExtra.KEY_USER_NAME,
									name);
							LogHelper.e("6666"+name+"6666");
							NavigationHelper.startActivity((Activity) context,
									UserProfileActivity.class, bundle, false);
						}
					}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			ss2.setSpan(new ForegroundColorSpan(context.getResources()
					.getColor(R.color.blue_light)), start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);// 字体颜色
			ss2.setSpan(new UnderlineSpan() {
				@Override
				public void updateDrawState(TextPaint ds) {
					super.updateDrawState(ds);
					ds.setUnderlineText(false);// 去除下划线
				}
			}, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return ss2;
	}
	
	   private static class MyURLSpan extends ClickableSpan {   
		   
	        private String mUrl;   
	   
	        MyURLSpan(String url) {   
	            mUrl = url;   
	        }   
	   
	        @Override   
	        public void onClick(View widget) {   
	        	Bundle bundle = new Bundle();
				bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_URL, mUrl);
				bundle.putString(ProjectConstants.BundleExtra.KEY_WEB_DETAIL_TITLE, "加载中");
				bundle.putBoolean(ProjectConstants.BundleExtra.KEY_IS_SET_TITLE, false);
				NavigationHelper.startActivity((Activity)mContext,BaseWebViewActivity.class, bundle, false);
	        }   
	    }   
}