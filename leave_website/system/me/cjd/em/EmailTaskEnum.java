package me.cjd.em;

public enum EmailTaskEnum {
	
	STATUS_NO(0, "任务执行中"),
	STATUS_OK(1, "任务已执行");
	
	public int value;
	public String name;
	
	private EmailTaskEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
}
