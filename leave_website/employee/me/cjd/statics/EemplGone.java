package me.cjd.statics;

public enum EemplGone {
	
	live(0, "存活"),
	gone(1, "删除");
	
	private int status;
	private String name;
	
	private EemplGone(int status, String name) {
		this.status = status;
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
