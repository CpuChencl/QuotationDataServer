package com.lion.mq;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lion.frame.logger.Log4jManager;
import com.lion.frame.mq.MqConfig;
import com.lion.frame.mq.ZmqSender;

@Lazy(false)
@Component
public class MdMqSender {
	private static final Logger log = Log4jManager.get();
	
	@Value("${mdSndMq.addr}")
	private String addr;
	
	@Value("${mdSndMq.port}")
	private int port;
	
	@Value("${mdSndMq.model}")
	private String model;
	
	@Value("${mdSndMq.type}")
	private String type;
	
	private ZmqSender  mdSender;
	
	@PostConstruct
	public void init(){
		MqConfig tradeSenderConfig = new MqConfig();
		tradeSenderConfig.setAddr(addr);
		tradeSenderConfig.setPort(port);
		tradeSenderConfig.setModel(model);
		tradeSenderConfig.setMqType(type);
		mdSender = new ZmqSender(tradeSenderConfig);
	}
	
	public void send(byte[] msg){
		mdSender.send(msg);
	}
}