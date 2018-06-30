package com.ts.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 文档数据存储
 * @author wl
 */
public class DataFile {
	private static Logger logger = LoggerFactory.getLogger(DataFile.class);

	private Map<String, String> dataMap = new HashMap<>();// 数据缓存

	/**
	 * 是否存有指定key
	 */
	public boolean hasKey(String key) {
		return dataMap.containsKey(key);
	}

	/**
	 * 获取数据
	 */
	public String get(String key) {
		return dataMap.get(key);
	}

	/**
	 * 存储数据
	 */
	public void put(String key, String value) {
		dataMap.put(key, value);
	}

	/**
	 * 加载数据文件
	 */
	public void load(String path) throws IOException {
		dataMap.clear();

		File file = new File(path);
		if (!file.exists()) {
			logger.warn("not found dataFile with {}", path);
			return;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
			String key;
			while ((key = br.readLine()) != null) {
				dataMap.put(key, br.readLine());
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	/**
	 * 存储到文件
	 */
	public void save(String path) throws IOException {
		File file = new File(path);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file))));
			for (Entry<String, String> entry : dataMap.entrySet()) {
				bw.write(entry.getKey());
				bw.newLine();
				bw.write(entry.getValue());
				bw.newLine();
			}
			bw.flush();
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

	public static void main(String[] args) {
		String path = "log/data.def";

		DataFile dataFile = new DataFile();
		try {
			dataFile.load(path);

			System.out.println(dataFile.get("11"));

			for (int i = 0; i < 1000; i++) {
				dataFile.put(String.valueOf(i), StringHelper.randUUID());
			}

			dataFile.save(path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
