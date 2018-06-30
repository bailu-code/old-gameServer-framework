package com.ts.framework.ratio;

import com.ts.framework.helper.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 比例随机工具
 * @author wl
 */
public class RatioHelper {

    /**
     * 随机出一个
     *
     * @param ratios 随机集合数组
     * @return 选中的id
     */
    public static int randomDefault(RatioDefault[] ratios) {
        return random(ratios).getId();
    }

    /**
     * 随机出一个
     *
     * @param ratios 随机集合
     * @return 选中的id
     */
    public static int randomDefault(List<RatioDefault> ratios) {
        return random(ratios).getId();
    }

    /**
     * 从份额列表中随机一个
     *
     * @param items 随机集合
     * @return 选中的id
     */
    public static <T extends Ratio> T random(T[] items) {
        List<T> randList = Arrays.asList(items);
        return random(randList);
    }

    /**
     * 采用圆桌算法（固定总份额，随机元素份额相加值，与总份额无关，可超出（必中一个），可小于总份额（可能不中））随机出一个（注：可能出现未命中的情况，则返回null）
     *
     * @param items      随机集合
     * @param roundRatio 圆桌总份额值
     * @return 命中的元素，如果所有元素份额相加小于总份额，则可能没有命中，返回null
     */
    public static <T extends Ratio> T randomRoundTable(T[] items, int roundRatio) {
        List<T> randList = Arrays.asList(items);
        return randomRoundTable(randList, roundRatio);
    }

    /**
     * 从份额列表中随机出一个
     *
     * @param randList 随机集合
     * @return 选中的id
     */
    public static <T extends Ratio> T random(List<T> randList) {
        return randomOne(randList, totalRatio(randList));
    }

    /**
     * 采用圆桌算法（固定总份额，随机元素份额相加值，与总份额无关，可超出（必中一个），可小于总份额（可能不中））随机出一个（注：可能出现未命中的情况，则返回null）
     *
     * @param randList   随机集合
     * @param roundRatio 圆桌总份额值
     * @return 命中的元素，如果所有元素份额相加小于总份额，则可能没有命中，返回null
     */
    public static <T extends Ratio> T randomRoundTable(List<T> randList, int roundRatio) {
        return randomOne(randList, roundRatio);
    }

    /**
     * 从份额列表中随机出多个，可能重复
     *
     * @param items 随机集合
     * @param num   需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> random(T[] items, int num) {
        List<T> randList = Arrays.asList(items);
        return random(randList, num);
    }

    /**
     * 采用圆桌算法随机出多个（注：可能出现实际返回数量少于需求数量的情况，除非能保证圆桌上的道具总份额始终大于圆桌的总份额）
     *
     * @param items      随机集合
     * @param num        需要随机出的数量
     * @param roundRatio 圆桌总份额值
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomRoundTable(T[] items, int num, int roundRatio) {
        List<T> randList = Arrays.asList(items);
        return randomRoundTable(randList, num, roundRatio);
    }

    /**
     * 从份额列表中随机出多个，可能出现相同元素
     *
     * @param randList 随机集合
     * @param num      需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> random(List<T> randList, int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be positive, " + num);
        }
        if (randList.isEmpty()) {
            throw new NullPointerException("randList is empty");
        }

        int maxRatio = totalRatio(randList);
        ArrayList<T> ratios = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            T hit = randomOne(randList, maxRatio);
            if (hit != null) {// 命中
                ratios.add(hit);
            }
        }
        return ratios;
    }

    /**
     * 采用圆桌算法随机出多个（注：可能出现实际返回数量少于需求数量的情况，除非能保证圆桌上的道具总份额始终大于圆桌的总份额）
     *
     * @param randList   随机集合
     * @param num        需要随机出的数量
     * @param roundRatio 圆桌总份额值
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomRoundTable(List<T> randList, int num, int roundRatio) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be positive, " + num);
        }
        if (randList.isEmpty()) {
            throw new NullPointerException("randList is empty");
        }

        ArrayList<T> ratios = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            T hit = randomOne(randList, roundRatio);
            if (hit != null) {// 命中
                ratios.add(hit);
            }
        }
        return ratios;
    }

    /**
     * 从份额列表中随机出多个，每个元素只会出现一次
     *
     * @param items 随机集合
     * @param num   需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomMutex(T[] items, int num) {
        List<T> randList = Arrays.asList(items);
        return randomMutex(randList, num);
    }

    /**
     * 从份额列表中随机出多个，每个元素只会出现一次
     *
     * @param randList 随机集合
     * @param num      需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomMutex(List<T> randList, int num) {
        return randomMutexFix(new LinkedList<>(randList), num);
    }

    /**
     * 从份额列表中随机出多个，每个元素只会出现一次，该方法会改变传入的items
     *
     * @param items 随机集合
     * @param num   需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomMutexFix(T[] items, int num) {
        List<T> randList = Arrays.asList(items);
        return randomMutexFix(randList, num);
    }

    /**
     * 从份额列表中随机出多个，每个元素只会出现一次，该方法会改变传入的randList
     *
     * @param randList 随机集合
     * @param num   需要随机出的数量
     * @return 命中列表
     */
    public static <T extends Ratio> ArrayList<T> randomMutexFix(List<T> randList, int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be positive, " + num);
        }
        if (randList.isEmpty()) {
            throw new NullPointerException("randList is null");
        }

        ArrayList<T> ratios = new ArrayList<>(num);
        while (ratios.size() < num && !randList.isEmpty()) {
            T t = random(randList);
            ratios.add(t);
            randList.remove(t);
        }
        return ratios;
    }

    /**
     * 统计总份额
     *
     * @param randList 随机集合
     * @return 总份额
     */
    private static <T extends Ratio> int totalRatio(List<T> randList) {
        int maxRatio = 0;// 最大份额
        for (T t : randList) {
            maxRatio += t.getRatio();
        }
        return maxRatio;
    }

    /**
     * 随机一个
     *
     * @param randList 随机集合
     * @param totalRatio 总份额
     * @return 命中的元素
     */
    private static <T extends Ratio> T randomOne(List<T> randList, int totalRatio) {
        if (totalRatio <= 0) {
            throw new IllegalArgumentException("totalRatio " + totalRatio + "<= 0");
        }
        if (randList.isEmpty()) {
            throw new NullPointerException("randList is null");
        }

        // 随机份额
        int randomRatio = MathHelper.randomInt(totalRatio);
        for (T t : randList) {
            if (randomRatio < t.getRatio()) {
                return t;
            } else {
                randomRatio -= t.getRatio();// 去掉未命中的份额
            }
        }
        return null;// 未命中
        // throw new RuntimeException("randomRatio " + randomRatio +
        // ", maxRatio " + totalRatio);
    }
}
