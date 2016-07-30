package me.cjd.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	
	public static void sendMail(
			String fromMail, 
			String fromSmtp,
			String fromPort,
			String user, 
			String password,  
            String toMail,  
            String mailTitle,  
            String mailContent,
            String chartset,
            boolean isSSL) {  
		// 可以加载一个配置文件
		Properties props = new Properties(); 
		// 使用smtp：简单邮件传输协议  
		props.put("mail.smtp.host", fromSmtp); // 存储发送邮件服务器的信息  
		props.put("mail.smtp.auth", "true"); //同时通过验证  
		props.put("mail.smtp.port", fromPort);
		props.put("mail.transport.protocol", "smtp");
		
		// ssl 配置
		if (isSSL) {
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.socketFactory.port", fromPort);
	        props.put("mail.smtp.socketFactory.fallback", "false");
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		
		Session session = Session.getInstance(props);//根据属性新建一个邮件会话  
//		session.setDebug(true); //有他会打印一些调试信息。  
		
		MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象 
		try {
			message.setFrom(new InternetAddress(fromMail));//设置发件人的地址  
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));//设置收件人,并设置其接收类型为TO  
			message.setSubject(mailTitle, chartset);//设置标题  
			//设置信件内容  
			//message.setText(mailContent); //发送 纯文本 邮件 todo  
			message.setContent(mailContent, "text/html;charset=" + chartset); //发送HTML邮件，内容样式比较丰富  
			message.setSentDate(new Date());//设置发信时间  
			message.saveChanges();//存储邮件信息  
			
			//发送邮件  
			//Transport transport = session.getTransport("smtp");  
			Transport transport = session.getTransport();  
			transport.connect(user, password);  
			transport.sendMessage(message, message.getAllRecipients());//发送邮件,其中第二个参数是所有已设好的收件人地址  
			transport.close();  
		} catch (Exception e) {
			throw new RuntimeException("发送邮件过程发生错误！！", e);
		}
	}
	
	public static void main(String[] args) {
		/*sendMail("t_j_m_001@139.com", "smtp.139.com", PORT, 
				"t_j_m_001@139.com", "cjd2613187",  
                "869535088@qq.com",  
                "Java Mail 测试邮件",  
                "<a>html 元素</a>：<b>邮件内容</b>");*/
	}
	
}