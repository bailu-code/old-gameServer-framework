package com.ts.framework.work;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;

/**
 * 动态参数封装
 *
 * @author wl
 */
public class Args implements Serializable {
	private static final long serialVersionUID = -5332168288129674527L;

	private Object[] args;// 参数列表
	private int index = 0;// 索引

	/**
	 * 封装参数
	 */
	public static Args valueOf(Object... args) {
		Args a = new Args();
		a.args = args;
		return a;
	}

	/**
	 * 复制一个副本
	 */
	public Args copy() {
		return Args.valueOf(args);
	}

	/**
	 * 获取动态参数，获取一次，下标+1，如果已获取完，则返回null
	 */
	@SuppressWarnings("unchecked")
	public <T> T read() throws ClassCastException, ArrayIndexOutOfBoundsException {
		if (index >= args.length) {
			return null;
		}
		return (T) args[index++];
	}

	@SuppressWarnings("unchecked")
	public <T> T read(int index) throws ClassCastException, ArrayIndexOutOfBoundsException {
		if (index >= args.length) {
			return null;
		}
		return (T) args[index];
	}

	/**
	 * 重置下标
	 */
	public void resetIndex() {
		index = 0;
	}

	/**
	 * 剩余可读数量
	 */
	public int readableLen() {
		return args.length - index;
	}

	/**
	 * 是否还有可读数据
	 */
	public boolean hasRead() {
		return args.length > index;
	}

	@Override
	public String toString() {
		return ArrayUtils.toString(args);
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
