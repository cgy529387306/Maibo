package com.tandy.android.fw2.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * 通知栏相关方法
 * User: pcqpcq
 * Date: 13-9-3
 * Time: 下午5:07
 */
public class NotificationHelper {

    /**
     * 状态栏提示的默认ID
     */
    private static int NOTIFICATION_DEFAULT_ID = Helper.createIntTag();

    /**
     * 显示状态栏提示
     *
     * @param pContext      上下文
     * @param pIcon         通知栏icon
     * @param pTickerText   提示时显示的滚动信息,null时则不滚动显示
     * @param pContentTitle 内容标题
     * @param pContentText  内容
     * @param pLaunchIntent Intent of the activity to be launched.
     */
    public static void showNotification(
            final Context pContext
            , final int pIcon
            , final CharSequence pTickerText
            , final CharSequence pContentTitle
            , final CharSequence pContentText
            , final Intent pLaunchIntent
    ) {
        showNotification(pContext, NOTIFICATION_DEFAULT_ID, pIcon, pTickerText, pContentTitle, pContentText, pLaunchIntent);
    }

    /**
     * 显示状态栏提示
     *
     * @param pContext      上下文
     * @param pId           ID,0则采用默认值
     * @param pIcon         通知栏icon
     * @param pTickerText   提示时显示的滚动信息,null时则不滚动显示
     * @param pContentTitle 内容标题
     * @param pContentText  内容
     * @param pLaunchIntent Intent of the activity to be launched.
     */
    public static void showNotification(
            final Context pContext
            , final int pId
            , final int pIcon
            , final CharSequence pTickerText
            , final CharSequence pContentTitle
            , final CharSequence pContentText
            , final Intent pLaunchIntent
    ) {
        showNotification(pContext, pId, pIcon, pTickerText, pContentTitle, pContentText, pLaunchIntent, true);
    }

    /**
     * 显示状态栏提示
     *
     * @param pContext      上下文
     * @param pId           ID,0则采用默认值
     * @param pIcon         通知栏icon
     * @param pTickerText   提示时显示的滚动信息,null时则不滚动显示
     * @param pContentTitle 内容标题
     * @param pContentText  内容
     * @param pLaunchIntent Intent of the activity to be launched.
     * @param pCancelLast   是否立即取消上一个提示
     */
    public static void showNotification(
            final Context pContext
            , final int pId
            , final int pIcon
            , final CharSequence pTickerText
            , final CharSequence pContentTitle
            , final CharSequence pContentText
            , final Intent pLaunchIntent
            , final boolean pCancelLast
    ) {
        showNotification(pContext, pId, pIcon, pTickerText, pContentTitle, pContentText, null, pLaunchIntent, pCancelLast);
    }

    /**
     * 显示状态栏提示
     *
     * @param pContext      上下文
     * @param pId           ID,0则采用默认值
     * @param pIcon         通知栏icon
     * @param pTickerText   提示时显示的滚动信息,null时则不滚动显示
     * @param pContentTitle 内容标题
     * @param pContentText  内容
     * @param pContentView  点击显示的内容View,无则null
     * @param pLaunchIntent Intent of the activity to be launched.
     * @param pCancelLast   是否立即取消上一个提示
     */
    public static void showNotification(
            final Context pContext
            , final int pId
            , final int pIcon
            , final CharSequence pTickerText
            , final CharSequence pContentTitle
            , final CharSequence pContentText
            , final RemoteViews pContentView
            , final Intent pLaunchIntent
            , final boolean pCancelLast
    ) {
        int id = pId == 0 ? NOTIFICATION_DEFAULT_ID : pId;
        NotificationManager notificationManager = (NotificationManager) pContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (pCancelLast) {
            notificationManager.cancel(id);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(pContext, (int) System.currentTimeMillis(), pLaunchIntent, PendingIntent.FLAG_ONE_SHOT);
        // 配置通知Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(pContext);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(pIcon);
        builder.setTicker(pTickerText);
        builder.setContentTitle(pContentTitle);
        builder.setContentText(pContentText);
        builder.setContentIntent(pendingIntent);
        if (Helper.isNotNull(pContentView)) {
            builder.setContent(pContentView);
        }
        // 构建通知的文本样式
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.setBuilder(builder);
        bigStyle.bigText(pContentText);
        // 发通知
        Notification notification = bigStyle.build();
        notificationManager.notify(id, notification);
    }

}
