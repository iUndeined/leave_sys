package me.cjd.service;

import me.cjd.pojo.Logs;
import me.cjd.statics.LogsType;
import me.cjd.utils.DateUtil;

public class LogsService {
	
	public final static LogsService me = new LogsService();
	
	/**
	 * 插入日志
	 * @param type 日志类型
	 * @param fromId 来源id
	 * @param manId 操作人id
	 * @param man 操作人名称
	 * @param content 说明之类的东西
	 */
	public void insert(LogsType type, Integer fromId, int manId, String man, String content){
		new Logs()
		.setType(type.name)
		.setFromId(fromId)
		.setMan(man)
		.setManId(manId)
		.setContent(content)
		.setCreatedDate(DateUtil.current())
		.save();
	}
	
}
