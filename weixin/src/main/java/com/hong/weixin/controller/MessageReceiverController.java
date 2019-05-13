package com.hong.weixin.controller;

import java.io.StringReader;
import javax.xml.bind.JAXB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hong.commons.domain.InMessage;
import com.hong.weixin.service.MessageTypeMapper;

@RestController

@RequestMapping("/hongzw/weixin/receiver")
public class MessageReceiverController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageReceiverController.class);

	@Autowired
	@Qualifier("inMessageTemplate")
	private RedisTemplate<String, InMessage> inMessageTemplate;
	
	@GetMapping // 只处理GET请求
	public String echo(//
			@RequestParam("signature") String signature, //
			@RequestParam("timestamp") String timestamp, //
			@RequestParam("nonce") String nonce, //
			@RequestParam("echostr") String echostr//
	) {
	
		System.out.println("hello kemao!");
		return echostr;
	}
	
	@PostMapping
	public String onMessage(
			@RequestParam("signature") String signature, //
			@RequestParam("timestamp") String timestamp, //
			@RequestParam("nonce") String nonce, //
			@RequestBody String xml) {
		//收到消息
		LOG.trace("收到的消息原文:\n{}\n----------------------------------------",xml);
		
		String type = xml.substring(xml.indexOf("<MsgType><![CDATA[") + 18);
		type = type.substring(0, type.indexOf("]]></MsgType>"));
		Class<InMessage> cla = MessageTypeMapper.getClass(type); 
		
		// 使用JAXB完成XML转换为Java对象的操作
		InMessage inMessage = JAXB.unmarshal(new StringReader(xml), cla);

		LOG.debug("转换得到的消息对象 \n{}\n", inMessage.toString());
		inMessageTemplate.convertAndSend("hongzw_" + inMessage.getMsgType(), inMessage);

			
		return "success";	
	}
}

