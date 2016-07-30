package me.cjd.utils;

import com.jfinal.kit.PropKit;

/**
 * 邮箱发送线程
 * @author Mr.cjd
 */
public class MailSender extends Thread {
	
	// 用户设置信息
	
	private String toMail;
	private String title;
	private String content;
	
	// 配置文档获取信息
	private boolean isSSL;
	private String chartset;
	private String mail;
	private String pswd;
	private String smtp;
	private String port;
	
	/**
	 * 初始化
	 */
	public void init(){
		this.isSSL = PropKit.getBoolean("senderIsSSL");
		this.chartset = PropKit.get("senderChartset");
		this.mail = PropKit.get("senderMail");
		this.pswd = PropKit.get("senderPswd");
		this.smtp = PropKit.get("senderSmtp");
		this.port = PropKit.get("senderPort");
	}
	
	/**
	 * 实例邮件发送线程构造
	 * @param title 邮件标题
	 * @param content 邮件内容
	 */
	public MailSender(String toMail, String title, String content) {
		super();
		this.toMail = toMail;
		this.title = title;
		this.content = content;
		// 初始化
		this.init();
		// 启动 线程
		this.start();
	}
	
	@Override
	public void run() {
		// 发送 邮件
		MailUtil.sendMail(this.mail, this.smtp, this.port, 
			this.mail, this.pswd, this.toMail, this.title, this.content, this.chartset, this.isSSL);
	}
	
}
