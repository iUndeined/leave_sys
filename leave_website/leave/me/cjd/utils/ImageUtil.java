package me.cjd.utils;

public class ImageUtil {
	
	public final static String getPrefix(String fileName){
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	public final static String getCacheName(String token, String originalFileName){
		return "leave_" + token + "_scrip" + getPrefix(originalFileName);
	}
	
	public final static String get(int leaveId, String originalFileName){
		return "leave_" + leaveId + "_scrip" + getPrefix(originalFileName);
	}
	
}
