package com.ts.framework.helper;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 关键字过滤查询工具
 * @author wl
 */
public class SensitiveWordHelper {

    @SuppressWarnings("rawtypes")
    private static HashMap<String, HashMap<Character, HashMap>> filterMap = new HashMap<>();

    private static final char END_POINT = (char) (1);

    /**
     * 计算字符串的长度
     */
    public static int length(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 检测名称是否只为英文+汉字+数字
     */
    public static boolean checkName(String name) {
        // 处理输入字符串5：过滤出英文+中文+数字
        Pattern pattern = Pattern.compile("[0-9a-zA-Z\u4E00-\u9FA5]+");
        return pattern.matcher(name).matches();
    }

    /**
     * 是否包含敏感词<br>
     * 性能测试：(100万次)在1591个关键字中包含“XXX”，检测出测试用例“我是XXX”平均耗时200ns，对于随机用例“冰雪聪明”平均耗时120ns。
     *
     * @param filterType 过滤器类型
     * @param info       输入文字
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean isSensitive(String filterType, String info) {
        if (info == null || info.length() == 0) {
            return false;
        }
        HashMap<Character, HashMap> filter = filterMap.get(filterType);
        //该过滤器不存在
        if (filter == null) {
            return false;
        }

        char[] charArray = info.toCharArray();
        //从每一个字开始遍历词
        for (int i = 0; i < charArray.length; i++) {
            HashMap<Character, HashMap> character = filter.get(charArray[i]);
            //当前字符不匹配
            if (character == null) {
                continue;
            }
            //已匹配到最后一个字符，找到
            if (character.containsKey(END_POINT)) {
                return true;
            }
            HashMap<Character, HashMap> temp = character;
            for (int j = i + 1; j < charArray.length; j++) {
                if (!temp.containsKey(charArray[j])) {
                    break;//后续字符匹配失败
                }
                HashMap<Character, HashMap> next = temp.get(charArray[j]);
                if (next.containsKey(END_POINT)) {
                    return true;//匹配到最后一个字符
                } else {
                    temp = next;
                }
            }
        }

        return false;
    }


    /**
     * 初始化敏感词过滤器
     *
     * @param filterType 过滤类型
     * @param words      敏感词列表
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void initSensitiveWord(String filterType, String[] words) {
        HashMap<Character, HashMap> filter = new HashMap<>();
        filterMap.put(filterType, filter);
        //遍历每一个词
        for (String word : words) {
            char[] charArray = word.trim().toCharArray();
            HashMap<Character, HashMap> temp = filter;
            //遍历词中的每一个字
            for (int i = 0; i < charArray.length; i++) {
                HashMap<Character, HashMap> character = temp.computeIfAbsent(charArray[i], k -> new HashMap<>());
                //最后一个字
                if (i == charArray.length - 1) {
                    character.put(END_POINT, null);
                } else {
                    temp = character;
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(checkName("da23大厦"));
    }
}
