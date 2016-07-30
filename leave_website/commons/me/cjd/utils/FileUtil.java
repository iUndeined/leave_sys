package me.cjd.utils;

import java.io.File;

public class FileUtil {
	
	public static String onlyName(File file){
		if (file == null) {
			return "";
		}
		
		String name = file.getName();
		Integer lastIdx = name.lastIndexOf(".");
		
		if (lastIdx < 1) {
			return name;
		}
		
		return name.substring(0, lastIdx);
	}
	
	/**
	 * 获取文件扩展名
	 * @param file 获取的文件
	 * @return
	 */
	public static String getExtension(File file){
		if (file == null) {
			return "";
		}
		
		String name = file.getName();
		Integer firstIdx = name.lastIndexOf(".");
		Integer lastIdx = name.length();
		
		if (firstIdx < 1) {
			return "";
		}
		
		return name.substring(++ firstIdx, lastIdx);
	}
	
	/**
	 * 扩展名匹配
	 * @param file 匹配的文件
	 * @param matchExtension 匹配的扩展名
	 * @return 是还是不是
	 */
	public static boolean hasExtension(File file, String matchExtension){
		if (matchExtension == null) { return false; }
		return getExtension(file).toLowerCase().equals(matchExtension.toLowerCase());
	}
	
}
