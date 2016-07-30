package me.cjd.utils;

import java.util.Map;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class Cache {
	
	private final static Map<String, Object> cahce = new HashMap<>();
	
	public final static <T> T get(String key){
		return (T) cahce.get(key);
	}
	
	public final static <T> T remove(String key){
		return (T) cahce.remove(key);
	}
	
	public final static <T> T put(String key, Object value){
		return (T) cahce.put(key, value);
	}
	
}