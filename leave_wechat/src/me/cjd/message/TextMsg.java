package me.cjd.message;

import me.cjd.utils.Config;

public class TextMsg extends BaseMsg {
	
	private Text text;
	
	public TextMsg(String touser, String text) {
		super(touser, "text", String.valueOf(Config.getQyApi().getAppId()));
		this.text = new Text(text);
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}
	
}

class Text {
	
	private String content;
	
	public Text(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
