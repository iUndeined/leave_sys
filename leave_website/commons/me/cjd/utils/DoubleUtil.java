package me.cjd.utils;

public class DoubleUtil {
	
	public final static double get(Double db){
		if (db == null) {
			return 0;
		}
		return db.doubleValue();
	}
	
}
