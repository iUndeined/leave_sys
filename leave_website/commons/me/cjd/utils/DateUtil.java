package me.cjd.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public final static Timestamp current(){
		return new Timestamp(System.currentTimeMillis());
	}
	
	private final static SimpleDateFormat getFmt(String fmt){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public final static String toStr(Timestamp time){
		return getFmt("yyyy-MM-dd HH:mm:ss").format(new Date(time.getTime()));
	}
	
	public final static Date toDate(Timestamp time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime());
		return calendar.getTime();
	}
	
	public final static Timestamp addDayForTask(int day){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, day);
		calendar.set(Calendar.HOUR, 8);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return new Timestamp(calendar.getTime().getTime());
	}
	
}
