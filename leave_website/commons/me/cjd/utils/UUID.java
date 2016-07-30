package me.cjd.utils;

public class UUID {
	
	public final static String random(){
		return java.util.UUID.randomUUID().toString().toUpperCase();
	}
	
}
