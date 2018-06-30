package com.ts.framework.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Md5加密工具
 *
 * @author wl
 */
public class MD5Helper {

    public static String md5_32(Object... parameters) throws NoSuchAlgorithmException {
        StringBuilder sBuilder = new StringBuilder();
        for (Object parameter : parameters) {
            sBuilder.append(parameter.toString());
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(sBuilder.toString().getBytes());
        byte[] bytes = md.digest();

        int i;

        StringBuilder buf = new StringBuilder("");
        for (byte b : bytes) {
            i = b;
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }

        return buf.toString();
    }

    public static String md5_16(Object... parameters) throws NoSuchAlgorithmException {
        return md5_32(parameters).substring(8, 24);
    }

    /**
     * 对http参数进行加密，key值进行排序key1=value key2=value的方式
     */
    public static String markSign(Map<String, String> map, String secretKey) throws NoSuchAlgorithmException {
        List<String> list = new ArrayList<>(map.size());
        list.addAll(map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList()));
        Collections.sort(list);
        list.add(secretKey);
        return md5_16(list.toArray(), secretKey);
    }

    /**
     * 计算md5值，按值顺序排序
     */
    public static String markSign(Object... params) throws NoSuchAlgorithmException {
        List<String> list = new ArrayList<>(params.length);
        for (Object param : params) {
            list.add(param.toString());
        }
        Collections.sort(list);
        return md5_16(list.toArray());
    }

}
