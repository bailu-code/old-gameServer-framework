package com.ts.framework.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 常用字符串处理方法集合
 * @author wl
 */
public class StringHelper {
    /**
     * 逗号,
     */
    public static final String SPLIT_1 = ",";
    /**
     * 分号;
     */
    public static final String SPLIT_2 = ";";
    /**
     * 中划线-
     */
    public static final String SPLIT_3 = "-";
    /**
     * 冒号:
     */
    public static final String SPLIT_4 = ":";
    /**
     * 中曲线~
     */
    public static final String SPLIT_5 = "~";
    /**
     * 竖线|
     */
    public static final String SPLIT_6 = "\\|";
    /**
     * 下划线
     **/
    public static final String SPLIT_7 = "_";

    private static final String[] splits = {SPLIT_1, SPLIT_2, SPLIT_3, SPLIT_4, SPLIT_5, SPLIT_6, SPLIT_7};

    private static final String content = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 字符串转int数组
     */
    public static int[] toIntArray(String split, String src) {
        if (StringUtils.isEmpty(src)) {
            return new int[0];
        }
        if (split == null) {
            return new int[]{Integer.valueOf(src)};
        }
        String[] tmp = src.split(split);
        int[] num = new int[tmp.length];
        for (int i = 0; i < num.length; i++) {
            if (StringUtils.isEmpty(tmp[i])) {
                num[i] = 0;
            } else {
                num[i] = Integer.valueOf(tmp[i]);
            }
        }
        return num;
    }

    /**
     * 字符串转float数组
     */
    public static float[] toFloatArray(String split, String src) {
        if (StringUtils.isEmpty(src)) {
            return new float[0];
        }
        if (split == null) {
            return new float[]{Float.valueOf(src)};
        }
        String[] tmp = src.split(split);
        float[] num = new float[tmp.length];
        for (int i = 0; i < num.length; i++) {
            if (StringUtils.isEmpty(tmp[i])) {
                num[i] = 0f;
            } else {
                num[i] = Float.valueOf(tmp[i]);
            }
        }
        return num;
    }

    /**
     * 字符串转double数组
     */
    public static double[] toDoubleArray(String split, String src) {
        if (StringUtils.isEmpty(src)) {
            return new double[0];
        }
        if (split == null) {
            return new double[]{Double.parseDouble(src)};
        }
        String[] tmp = src.split(split);
        double[] num = new double[tmp.length];
        for (int i = 0; i < num.length; i++) {
            if (StringUtils.isEmpty(tmp[i])) {
                num[i] = 0f;
            } else {
                num[i] = Double.valueOf(tmp[i]);
            }
        }
        return num;
    }

    /**
     * 字符串转long数组
     */
    public static long[] toLongArray(String split, String src) {
        if (StringUtils.isEmpty(src)) {
            return new long[0];
        }
        if (split == null) {
            return new long[]{Long.valueOf(src)};
        }
        String[] tmp = src.split(split);
        long[] num = new long[tmp.length];
        for (int i = 0; i < num.length; i++) {
            if (StringUtils.isEmpty(tmp[i])) {
                num[i] = 0L;
            } else {
                num[i] = Long.valueOf(tmp[i]);
            }
        }
        return num;
    }

    /**
     * 转换为字符串数组
     */
    public static String[] toArray(String src) {
        if (StringUtils.isEmpty(src)) {
            return new String[0];
        }
        String split = findSplit(src);
        if (split == null) {
            return new String[]{src};
        }
        return src.split(split);
    }

    /**
     * 转换字符串数组
     */
    public static String[] toArray(String src, String split) {
        if (StringUtils.isEmpty(src)) {
            return new String[0];
        }
        return src.split(split);
    }

    /**
     * 找出字符串中的分隔符，注意：只能用于基础数组分割，外部保证只有一种分隔符存在
     */
    public static String findSplit(String src) {
        if (src == null) {
            return null;
        }
        for (String split : splits) {
            if (src.contains(split)) {
                return split;
            }
        }
        return null;
    }

    /**
     * 随机生成一个唯一uuid
     */
    public static String randUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 转换连续的整型数组，如1:3-5:8，返回1,3,4,5,8
     */
    public static List<Integer> continuousIntArray(String[] strings) {
        List<Integer> intList = new ArrayList<>();
        for (String str : strings) {
            intList.addAll(continuousIntArray(str));
        }
        return intList;
    }

    /**
     * 转换连续的整形数组，比如2-5，返回的int list包含2，3，4，5
     */
    public static List<Integer> continuousIntArray(String str) {
        List<Integer> intList = new ArrayList<>();
        if (StringUtils.contains(str, StringHelper.SPLIT_3)) {
            // 顺时针("-")
            int[] array = StringHelper.toIntArray(StringHelper.SPLIT_3, str);
            if (array.length != 2) {
                throw new RuntimeException("error continuous, " + str);
            }
            if (array[0] > array[1]) {
                throw new RuntimeException("error continuous, " + str);
            }
            do {// 22-2 22.23.0.1.2
                intList.add(array[0]);
            } while (array[0]++ != array[1]);// 在目标位置跳出循环
        } else {
            // 单个
            intList.add(Integer.valueOf(str));
        }
        return intList;
    }

    /**
     * 构造多个字段组成的key
     */
    public static String buildKey(Object... objects) {
        return ArrayUtils.toString(objects);
    }

    /**
     * 转换int数组
     */
    public static int[] toIntArray(String[] arr) {
        if (arr == null)
            return null;
        int[] arrInt = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            arrInt[i] = Integer.valueOf(arr[i]);
        }
        return arrInt;
    }

    /**
     * 随机指定长度的字符串
     */
    public static String randomMultiStr(int min, int max) {
        if (max < 0 || min < 0) {
            throw new RuntimeException("min or max must >=0");
        }
        int len = MathHelper.randBetween(min, max);
        StringBuilder buffer = new StringBuilder();
        int strLen = content.length();
        for (int i = 0; i < len; i++) {
            buffer.append(content.charAt(MathHelper.randBetween(0, strLen)));
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        // System.out.println(ArrayUtils.toString(continuousIntArray("9-8")));

        for (int i = 0; i < 1000000; i++) {
            randUUID();
        }

        long now = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            randUUID();
        }
        long useTime = System.currentTimeMillis() - now;
        System.out.println("useTime: " + useTime + ", avg: " + useTime / 10000f);
    }
}
