package com.web.demo.util;

import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Base64编解码工具集
 *
 * @author bfan
 * @version V1.0
 * @date 2018年6月16日
 */
@Log4j2
public final class Base64Util {
    /**
     * 编码器
     */
    private final static Base64.Encoder encoder = Base64.getEncoder();

    //解码器
    private final static Base64.Decoder decoder = Base64.getDecoder();

    /**
     * BASE64编码码
     *
     * @param message 需要编码的字符串
     * @return String 编码后的字符串
     */
    public static String encode(String message) {
        if (StringUtil.isBlank(message)) {
            return null;
        }

        try {
            byte[] bytes = message.getBytes("UTF-8");
            return encoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("BASE64编码失败", e);
            return null;
        }
    }

    /**
     * BASE64编码码
     *
     * @param bytes 需要编码的字节数组
     * @return String 编码后的字符串
     */
    public static String encode(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }

        try {
            return encoder.encodeToString(bytes);
        } catch (Exception e) {
            log.error("BASE64编码失败", e);
            return null;
        }
    }


    public static String imageEncode(String imageurl) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String strImageToBase64 = "";
        HttpURLConnection conn = null;
        try {
            // 创建URL
            URL url = new URL(imageurl);
            byte[] by = new byte[1024];
            // 创建链接
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 对字节数组Base64编码
            strImageToBase64 = encoder.encodeToString(data.toByteArray());
            // 关闭流
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("图片转换失败imageurl：" + imageurl);
            log.error("图片转base64报错：" + e);
            return null;
        } finally {
            conn.disconnect();
        }
        return strImageToBase64;

    }

    /**
     * BASE64解码器
     *
     */
    public static byte[] decode(String  imageBase64) {
        if (imageBase64==null || imageBase64.equals("")) {
            return null;
        }
        try {
            return decoder.decode(imageBase64);
        } catch (Exception e) {
            log.error("BASE64解码失败", e);
            return null;
        }
    }


}
