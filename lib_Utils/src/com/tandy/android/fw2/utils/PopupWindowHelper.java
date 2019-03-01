package com.tandy.android.fw2.utils;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * PopupWindow助手类
 * User: pcqpcq
 * Date: 13-9-4
 * Time: 上午9:22
 */
public class PopupWindowHelper {

    /**
     * 创建PopupWindow(可touch,区域外touch,可聚焦)
     *
     * @param pActivity     创建的Activity,非context
     * @param pViewLayoutId 内部View布局id
     * @return PopupWindow
     */
    public static PopupWindow createPopupWindow(
            Activity pActivity
            , int pViewLayoutId
    ) {
        return createPopupWindow(
                pActivity, pViewLayoutId
                , WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                , true, true, true);
    }

    /**
     * 创建PopupWindow(可touch,区域外touch,可聚焦)
     *
     * @param pActivity     创建的Activity,非context
     * @param pViewLayoutId 内部View布局id
     * @param pWidth        创建的宽度(可采用LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT之类)
     * @param pHeight       创建的高度(可采用LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT之类)
     * @return PopupWindow
     */
    public static PopupWindow createPopupWindow(
            Activity pActivity
            , int pViewLayoutId
            , int pWidth
            , int pHeight
    ) {
        return createPopupWindow(
                pActivity, pViewLayoutId, pWidth, pHeight, true, true, true);
    }

    /**
     * 创建PopupWindow
     *
     * @param pActivity         创建的Activity,非context
     * @param pViewLayoutId     内部View布局id
     * @param pWidth            创建的宽度(可采用LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT之类)
     * @param pHeight           创建的高度(可采用LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT之类)
     * @param pTouchable        是否可touch
     * @param pOutsideTouchable 是否可在区域外touch
     * @param pFocusable        是否可聚焦
     * @return PopupWindow
     */
    public static PopupWindow createPopupWindow(
            Activity pActivity
            , int pViewLayoutId
            , int pWidth
            , int pHeight
            , boolean pTouchable
            , boolean pOutsideTouchable
            , boolean pFocusable
    ) {
        PopupWindow result = null;
        View contentView = pActivity.getLayoutInflater().inflate(pViewLayoutId, null);
        result = new PopupWindow(contentView, pWidth, pHeight, pFocusable);
        result.setTouchable(pTouchable);
        result.setOutsideTouchable(pOutsideTouchable);
        return result;
    }

}
