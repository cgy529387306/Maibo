package com.android.volley.toolbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * EDIT 新增
 * @author pcqpcq
 * @version 1.0.0
 * @since 14-3-27 下午3:00
 */
public class Stack {

    /**
     * 进行gzip压缩
     */
    public static byte[] gzipWrapPostData(byte[] postData) {
        GZIPOutputStream gos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(bos);
            gos.write(postData);
            gos.flush();
            gos.finish();

            return bos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gos.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 进行gzip解压
     */
    public static byte[] gzipUnwrapPostData(byte[] wrapData) {
        try {
            byte buf[] = new byte[1024];

            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            ByteArrayInputStream bis = new ByteArrayInputStream(wrapData);
            GZIPInputStream gzis = new GZIPInputStream(bis);
            int res = 0;
            while (res >= 0) {
                res = gzis.read(buf, 0, buf.length);
                if (res > 0) {
                    byteout.write(buf, 0, res);
                }
            }
            gzis.close();

            return byteout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
