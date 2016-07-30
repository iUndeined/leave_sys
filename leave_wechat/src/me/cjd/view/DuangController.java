package me.cjd.view;

import me.cjd.http.core.HttpUtil;
import me.cjd.http.inter.IParam;
import me.cjd.http.params.StrParam;
import me.cjd.message.NewsMsg;
import me.cjd.message.TextMsg;
import me.cjd.pojo.Balance;
import me.cjd.service.BalanceService;
import me.cjd.utils.StringUtil;
import me.cjd.utils.Wechat;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Logger;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

public class DuangController extends QyMsgController {
	
	private static Logger logger = Logger.getLogger(DuangController.class);

	@Override
	protected void processInTextMsg(InTextMsg inTextMsg) {
		OutTextMsg outMsg = new OutTextMsg(inTextMsg);
		String fromUser = inTextMsg.getFromUserName();
		String msg = inTextMsg.getContent();
		
		if (StrKit.notBlank(msg)) {
			String[] strs = msg.split("-");
			if (strs.length == 2 ||
				strs.length == 3) {
				try {
					// 解析 快捷审批
					Integer.valueOf(strs[0]);
					String result = strs[1];
					String reason = strs.length == 3 ? strs[2] : null;
					// 发送 审批请求
					HttpUtil.post("http://localhost/leave/gunForWechat", new IParam[]{
						new StrParam("id", strs[0]),
						new StrParam("result", result),
						new StrParam("reason", reason)
					});
					// 
					Wechat.sendTextMsg(new TextMsg(fromUser, "您的处理结果已成功递交！"));
				} catch (Exception e) {}
			} else if (strs.length > 3) {
				outMsg.setContent("审批意见请不要空格");
				this.render(outMsg);
				return;
			}
			
//			Wechat.sendNewsMsg(new NewsMsg(fromUser, "审批请求通知", "2015年12月5日\r\n你刚刚申请了....巴拉巴拉", "http://www.baidu.com/"));
		}
		
		this.renderText("passed");
	}

	@Override
	protected void processInImageMsg(InImageMsg inImageMsg) {
	}

	@Override
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
	}

	@Override
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
	}

	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
	}

	@Override
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
	}

	@Override
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
	}

	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
	}

	@Override
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
			logger.debug("关注：" + inFollowEvent.getFromUserName());
			OutTextMsg outMsg = new OutTextMsg(inFollowEvent);
			outMsg.setContent("这里是请假系统微信平台，欢迎您的加入！！");
			this.render(outMsg);
		}
	}

	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
	}

	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
	}

	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
	}

	@Override
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		String event = inMenuEvent.getEvent().toUpperCase();
		String eventKey = inMenuEvent.getEventKey().toUpperCase();
		
		switch (event) {
			case "CLICK":
				switch (eventKey) {
					case "QUERY":
						OutTextMsg msg = new OutTextMsg(new InTextMsg(inMenuEvent.getToUserName(), inMenuEvent.getFromUserName(), null, null));
						msg.setContent("正在查询您的假期信息，请稍候。。。");
						this.render(msg);
						
						new QueryLeaveThread(inMenuEvent.getFromUserName()).start();
						
						break;
				}
				break;
		}
	}

	@Override
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
	}

	@Override
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
	}
	
}

class QueryLeaveThread extends Thread {
	
	private String touser;
	
	public QueryLeaveThread(String touser) {
		super();
		this.touser = touser;
	}

	@Override
	public void run() {
		Balance balance = BalanceService.findByEmplId(1);
		
		String enter = "\r\n";
		StringBuffer buffer = new StringBuffer();
		
		buffer
		.append("您的假期信息：").append(enter)
		.append("当年剩余年期 ").append(balance.getCurrRestAl()).append(enter)
		.append("当年剩余年期 ").append(balance.getCurrRestLil()).append(enter)
		.append("当年已请年期 ").append(balance.getCurrYearApplyAl()).append(enter)
		.append("当年已请调休 ").append(balance.getCurrYearApplyLil());
		
		Wechat.sendTextMsg(new TextMsg(this.touser, buffer.toString()));
	}
}