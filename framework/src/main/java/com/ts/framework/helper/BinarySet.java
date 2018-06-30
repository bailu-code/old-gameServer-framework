package com.ts.framework.helper;

import java.util.BitSet;

/**
 * 二进制数据体
 * @author wl
 */
public class BinarySet {
    private BitSet bitSet;// 数据存储体
    private int bitPreWord;// 每个数据占据的长度

    /**
     * 仅用于json初始化用，请使用其他带参数的构造方法
     */
    @Deprecated
    public BinarySet() {
    }

    /**
     * @param bitPreWord 每个数据占据的长度
     */
    public BinarySet(int bitPreWord) {
        this.bitPreWord = bitPreWord;

        bitSet = new BitSet();
    }

    /**
     * @param bitPreWord 每个数据占据的长度
     * @param bytes      初始数据
     */
    public BinarySet(int bitPreWord, byte[] bytes) {
        this.bitPreWord = bitPreWord;

        bitSet = BitSet.valueOf(bytes);
    }

    /**
     * 读取数据
     *
     * @param index 数据标签
     */
    public int read(int index) {
        return read(index, bitPreWord, 0);
    }

    /**
     * 读取数据
     *
     * @param index  数据标签
     * @param len    读取长度
     * @param offset 偏移量
     */
    public int read(int index, int len, int offset) {
        index *= bitPreWord;
        int value = 0;
        for (int i = len - 1; i >= 0; i--) {
            value = value * 2 + (bitSet.get(index + i + offset) ? 1 : 0);
        }
        return value;
    }

    /**
     * 写入数据
     *
     * @param index 数据标签
     * @param value 写入的值
     */
    public void write(int index, int value) {
        write(index, value, bitPreWord, 0);
    }

    /**
     * 写入数据
     *
     * @param index  数据标签
     * @param value  写入的值
     * @param len    读取长度
     * @param offset 偏移量
     */
    public void write(int index, int value, int len, int offset) {
        index *= bitPreWord;
        for (int i = 0; i < len; i++) {
            bitSet.set(index + i + offset, (value & 1) == 1);
            value = value >> 1;
        }
    }

    /**
     * 拥有数量
     */
    public int getLen() {
        return bitSet.length() / bitPreWord;
    }

    public byte[] getBitSet() {
        return bitSet.toByteArray();
    }

    @Override
    public String toString() {
        return bitSet.toString();
    }

    public void setBitSet(byte[] bitSet) {
        this.bitSet = BitSet.valueOf(bitSet);
    }

    public int getBitPreWord() {
        return bitPreWord;
    }

    public void setBitPreWord(int bitPreWord) {
        this.bitPreWord = bitPreWord;
    }

}
