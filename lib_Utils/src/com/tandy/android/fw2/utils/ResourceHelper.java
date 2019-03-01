package com.tandy.android.fw2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.tandy.android.fw2.utils.app.WpApplication;

/**
 * Resource相关方法
 * User: pcqpcq
 * Date: 13-9-3
 * Time: 下午5:59
 */
public class ResourceHelper {

    /**
     * 取得资源字符串
     *
     * @param resId 资源id
     * @return 字符串
     */
    public static String getString(int resId) {
        return WpApplication.getInstance().getString(resId);
    }

    /**
     * 取得资源字符串
     *
     * @param resId      资源id
     * @param formatArgs 格式化参数
     * @return 字符串
     */
    public static String getString(int resId, Object... formatArgs) {
        return WpApplication.getInstance().getString(resId, formatArgs);
    }

    /**
     * 取得指定名称的图片资源ID
     *
     * @param imageName 图片名称
     * @return id
     */
    public static int getImageResId(String imageName) {
        return WpApplication.getInstance().getResources().getIdentifier(imageName, "drawable", WpApplication.getInstance().getPackageName());
    }

    /**
     * 取得指定名称的bitmap
     *
     * @param imageName 图片名称
     * @return 图片对象
     */
    public static Bitmap getBitmapByName(String imageName) {
        Bitmap result = null;
        try {
            int resId = getImageResId(imageName);
            if (resId > 0) {
                result = BitmapFactory.decodeResource(WpApplication.getInstance().getResources(), resId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 取得指定名称的Drawable
     *
     * @param imageName 图片名称
     * @return 图片对象
     */
    public static Drawable getDrawableByName(String imageName) {
        Drawable result = null;
        try {
            int resId = getImageResId(imageName);
            if (resId > 0) {
                result = WpApplication.getInstance().getResources().getDrawable(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取平铺背景
     * @param resId Bitmap的id
     * @see <a href="http://stackoverflow.com/questions/7586209/xml-drawable-bitmap-tilemode-bug">xml-drawable-bitmap-tilemode-bug</a>
     */
    public static Drawable getRepeatBackground(int resId) {
        Context context = WpApplication.getInstance();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);
        return drawable;
    }

    /**
     * 取得指定名称的字符串资源ID
     *
     * @param stringName 字符串名称
     * @return id
     */
    public static int getStringResId(String stringName) {
        //return TandyApplication.getInstance().getResources().getIdentifier(stringName, "string", TandyApplication.getInstance().getPackageName());
        return getResId(stringName, "string");
    }

    /**
     * 取得指定名称的布局资源id
     *
     * @param layoutName 布局名字
     * @return id
     */
    public static int getLayoutResId(String layoutName) {
        return getResId(layoutName, "layout");
    }

    /**
     * 取得指定名称的动画资源id
     *
     * @param animName 动画名字
     * @return id
     */
    public static int getAnimResId(String animName) {
        return getResId(animName, "anim");
    }

    /**
     * 取得指定名称的资源id
     *
     * @param idName 资源名
     * @return id
     */
    public static int getIdResId(String idName) {
        return getResId(idName, "id");
    }

    /**
     * 取得指定名称的样式id
     *
     * @param styleableString 样式名
     * @return id
     */
    public static int getStyleableResId(String styleableString) {
        return getResId(styleableString, "styleable");
    }

    /**
     * 通过名字和类型取得res id
     *
     * @param resName 资源名
     * @param type    类型
     * @return id
     */
    private static int getResId(String resName, String type) {
        return WpApplication.getInstance().getResources()
                .getIdentifier(resName, type, WpApplication.getInstance().getPackageName());
    }

    /**
     * 取得指定名称的数组资源
     *
     * @param arrayName 数组名称
     * @return 数组
     */
    public static String[] getStringArray(String arrayName) {
        Resources r = WpApplication.getInstance().getResources();
        return r.getStringArray(r.getIdentifier(arrayName, "array", WpApplication.getInstance().getPackageName()));
    }

}
