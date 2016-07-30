package me.cjd.statics;

public enum LogsType {
	
	apply(0, "请假"),
	approval(1, "审批"),
	cancel(2, "取消"),
	invalid(3, "作废");
	
	public int type;
	public String name;

	private LogsType(int type, String name) {
		this.type = type;
		this.name = name;
	}

}
