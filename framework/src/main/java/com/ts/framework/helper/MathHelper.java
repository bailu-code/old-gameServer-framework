package com.ts.framework.helper;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数学工具
 * @author wl
 */
public class MathHelper {
    public static final int RANDOM_DENOMINATOR = 10000;//概率随机默认分母

    /**
     * 向上取整
     */
    public static int ceil(float num) {
        return (int) num == num ? (int) num : (int) num + 1;
    }

    /**
     * 转换Object Integer to int
     */
    public static int toInt(Object object) {
        if (object == null) {
            return 0;
        } else {
            return (int) object;
        }
    }

    /**
     * 转换Object Float to float
     */
    public static float toFloat(Object object) {
        if (object == null) {
            return 0f;
        } else {
            return (float) object;
        }
    }

    /**
     * 转换Object Long to long
     */
    public static long toLong(Object object) {
        if (object == null) {
            return 0L;
        } else {
            return (long) object;
        }
    }

    /**
     * 转换Object to String
     */
    public static String toString(Object object) {
        if (object == null) {
            return "";
        } else {
            return (String) object;
        }
    }

    /**
     * 随机一个int
     */
    public static int randomInt(int n) {
        if (n <= 0) {
            throw new RuntimeException("randInt n " + n + " <= 0");
        }
        return ThreadLocalRandom.current().nextInt(n);
    }

    /**
     * 随机一个(0<=rand<1) 范围的随机float
     */
    public static float randomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    /**
     * 随意一个双精度的浮点数
     */
    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * 是否满足几率，分母为一万
     */
    public static boolean randomTrue(int odds) {
        return ThreadLocalRandom.current().nextInt(RANDOM_DENOMINATOR) < odds;
    }

    /**
     * 是否不满足几率，分母为一万
     */
    public static boolean randomFalse(int odds) {
        return ThreadLocalRandom.current().nextInt(RANDOM_DENOMINATOR) >= odds;
    }

    /**
     * 随机true
     */
    public static boolean randomTrue(float odds) {
        return odds > 0 && randomFloat() < odds;
    }

    /**
     * 随机false
     */
    public static boolean randomFalse(float odds) {
        return odds <= 0 || randomFloat() >= odds;
    }

    /**
     * 随机true
     */
    public static boolean randomTrue(double odds) {
        return odds > 0 && randomDouble() < odds;
    }

    /**
     * 随机false
     */
    public static boolean randomFalse(double odds) {
        return odds <= 0 || randomDouble() >= odds;
    }

    /**
     * 随机是否为true
     */
    public static boolean randomTrue() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 随机是否为false
     */
    public static boolean randomFalse() {
        return !ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 以中心点，在矩形范围内随机点
     */
    public static Point randomPoint(int posX, int posY, int radius) {
        return new Point(posX - radius + randomInt(radius * 2), posY - radius + randomInt(radius * 2));
    }

    /**
     * 求两个数之间的随机值。范围[min,max)
     */
    public static int randBetween(int min, int max) {
        if (min == max) {
            return min;
        } else if (min < max) {
            return ThreadLocalRandom.current().nextInt(min, max);
        } else {
            return ThreadLocalRandom.current().nextInt(max, min);
        }
    }

    /**
     * 求两个数之间的随机值。范围[min,max)
     */
    public static float randBetween(float min, float max) {
        if (min == max) {
            return min;
        } else if (min < max) {
            return (float) ThreadLocalRandom.current().nextDouble(min, max);
        } else {
            return (float) ThreadLocalRandom.current().nextDouble(max, min);
        }
    }

    /**
     * 计算起点到目标点的方向
     */
    public static Float toDirection(int startX, int startY, int endX, int endY) {
        float directionX = endX - startX;
        float directionY = endY - startY;
        double dis = Point.distance(endX, endY, startX, startY);
        if (dis == 0) {
            return new Float(0, 0);
        }
        directionX /= dis;
        directionY /= dis;
        return new Float(directionX, directionY);
    }

    /**
     * 根据两点和距离确定目标移动终点
     *
     * @param directionX 矢量方向点X坐标
     * @param directionY 矢量方向点Y坐标
     * @param startX    移动开始点X坐标
     * @param startY    移动开始点Y坐标
     * @param distance  移动距离
     * @return 终点
     */
    public static Float toVectorDirection(int directionX, int directionY, int startX, int startY, float distance) {
        float endX, endY;
        float m = startX - directionX;
        float n = startY - directionY;
        float c = (float) Math.sqrt(m * m + n * n);
        endX = distance * m / c + startX;
        endY = distance * n / c + startY;
        return new Float(endX, endY);
    }

    /**
     * 求2条线段交点
     *
     * @param x1 , y1, x2, y2 组成第一条线段
     * @param x3 , y3, x4, y4 组成第二条线段
     */
    public static Point2D.Double intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double temp = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        Point2D.Double point = new Point2D.Double(x1, y1);
        point.x += (x2 - x1) * temp;
        point.y += (y2 - y1) * temp;
        return point;
    }

    /**
     * 求2条线段交点
     */
    public static Point2D.Double intersection(Line2D.Double line1, Line2D.Double line2) {
        return intersection(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.y1, line2.x2, line2.y2);
    }

    /**
     * value是否属于区间[start, end)
     */
    public static boolean between(int start, int end, int value) {
        return value >= start && value < end;
    }

    /**
     * 分割数据，5/2 = 3，4/2 = 2
     *
     * @param max  数据上限
     * @param unit 单位
     */
    public static int num(int max, int unit) {
        return max % unit == 0 ? max / unit : max / unit + 1;
    }

    /**
     * 根据向量求夹角，返回值为弧度
     */
    public static double angle(double x1, double y1, double x2, double y2) {
        return Math.acos((x1 * x2 + y1 * y2) / Math.sqrt((x1 * x1 + y1 * y1) * (x2 * x2 + y2 * y2)));
    }

    /**
     * 根据向量求夹角，返回度数
     */
    public static int angleDegrees(double x1, double y1, double x2, double y2) {
        return (int) (angle(x1, y1, x2, y2) * 180 / 3.14);
    }

    /**
     * 判断点是否在线段上
     */
    public static boolean ptOnSegment(double p1x, double p1y, double p2x, double p2y, double x, double y) {
        float d = (float) (Point.distance(p1x, p1y, x, y) + Point.distance(p2x, p2y, x, y));
        float dd = (float) Point.distance(p1x, p1y, p2x, p2y);
        return d == dd;
    }

    /***
     * 随机命中概率下标
     */
    public static int randomIndex(int[] pros) {
        int sum = 0;
        for (int pro : pros) {
            sum += pro;
        }

        int random = randomInt(sum);
        for (int i = 0; i < pros.length; i++) {
            if (random < pros[i]) {
                return i;
            }
            random -= pros[i];
        }
        return 0;
    }

    /**
     * 随机指定范围内N个不重复的数 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] randomArray(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

}
