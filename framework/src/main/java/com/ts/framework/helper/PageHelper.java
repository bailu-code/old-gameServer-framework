package com.ts.framework.helper;

import java.util.List;

/**
 * 分页辅助工具
 * @author wl
 */
public class PageHelper {

    /**
     * 根据开始点和结束点进行分页，单页数量=start-end
     *
     * @param all   数据集合
     * @param start 开始点，从0开始
     * @param end   结束点
     */
    public static <T> List<T> pageBySpace(List<T> all, int start, int end) {
        if (start < 0 || end < 0 || start > end || start > all.size()) {
            return null;
        }
        end = end > all.size() ? all.size() : end;
        return all.subList(start, end);
    }

    /**
     * 根据页数和单页数量进行分页
     *
     * @param all     数据集合
     * @param page    需要的页数，从0开始
     * @param pageNum 单页数量
     */
    public static <T> List<T> pageByNum(List<T> all, int page, int pageNum) {
        int start = page * pageNum;
        int end = start + pageNum;
        return pageBySpace(all, start, end);
    }

    /**
     * 获取总页数
     *
     * @param all     数据集合
     * @param pageNum 单页数量
     */
    public static <T> int getPageMax(List<T> all, int pageNum) {
        if (pageNum <= 0) {
            throw new RuntimeException("求分页最大值出错，pageNum不能小于或等于零！");
        }
        if (all.size() == 0) {
            return 1;
        }
        return all.size() % pageNum == 0 ? all.size() / pageNum : all.size() / pageNum + 1;
    }
}
