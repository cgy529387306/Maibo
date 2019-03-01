package com.tandy.android.fw2.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * ProgressDialog助手类
 * User: pcqpcq
 * Date: 13-9-4
 * Time: 上午9:13
 */
public class ProgressDialogHelper {

    private static ProgressDialog mProgressDialog;

    private static ProgressDialog getProgressDialog(Activity activity) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(activity, "", "", false, true);
        }
        return mProgressDialog;
    }

    /**
     * 显示ProgressDialog，无标题
     *
     * @param messageResId 消息资源id
     */
    public static void showProgressDialog(Activity activity, int messageResId) {
        showProgressDialog(activity, "", activity.getString(messageResId), false, true);
    }

    /**
     * 显示ProgressDialog，无标题
     *
     * @param message 消息
     */
    public static void showProgressDialog(Activity activity, String message) {
        showProgressDialog(activity, "", message, false, true);
    }

    /**
     * 显示ProgressDialog
     *
     * @param titleResId   标题资源id
     * @param messageResId 消息资源id
     */
    public static void showProgressDialog(Activity activity, int titleResId, int messageResId) {
        showProgressDialog(activity, activity.getString(titleResId), activity.getString(messageResId), false, true);
    }

    /**
     * 显示ProgressDialog
     *
     * @param title   标题
     * @param message 消息
     */
    public static void showProgressDialog(Activity activity, String title, String message) {
        showProgressDialog(activity, title, message, false, true);
    }

    /**
     * 显示ProgressDialog，自定义
     *
     * @param title         标题
     * @param message       消息
     * @param indeterminate 进度的确定性
     * @param cancelable    Sets whether this dialog is cancelable with the BACK key.
     */
    public static void showProgressDialog(Activity activity, String title, String message, boolean indeterminate, boolean cancelable) {
        showProgressDialog(activity, title, message, indeterminate, cancelable, true);
    }

    /**
     * 显示ProgressDialog，自定义
     *
     * @param title         标题
     * @param message       消息
     * @param indeterminate 进度的确定性
     * @param cancelable    Sets whether this dialog is cancelable with the BACK key.
     * @param onceMore      出错时是否再执行一次
     */
    private static void showProgressDialog(Activity activity, String title, String message, boolean indeterminate, boolean cancelable, boolean onceMore) {
        if (Helper.isNotNull(activity) && !activity.isFinishing()) {
            try {
                getProgressDialog(activity).dismiss();
                getProgressDialog(activity).setTitle(title);
                getProgressDialog(activity).setMessage(message);
                getProgressDialog(activity).setIndeterminate(indeterminate);
                getProgressDialog(activity).setCancelable(cancelable);
                getProgressDialog(activity).show();
            } catch (Exception e) {
                e.printStackTrace();
                dismissProgressDialog();
                mProgressDialog = null;
                if (onceMore) {
                    showProgressDialog(activity, title, message, indeterminate, cancelable, false);
                }
            }
        }
    }

    /**
     * 关闭进度框
     */
    public static void dismissProgressDialog() {
        try {
            if (Helper.isNotNull(mProgressDialog)) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
