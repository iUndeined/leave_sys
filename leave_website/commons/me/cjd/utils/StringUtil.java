package me.cjd.utils;

public class StringUtil {
	
	/**
	 * 判断字符串是空的
	 * @param str 目标字串
	 * @return 
	 */
	public static final boolean isEmpty(String str){
		return str == null || str.trim().equals("");
	}
	
	/**
	 * 判断字符串不是空的
	 * @param str 目标字串
	 * @return 
	 */
	public static final boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
}
