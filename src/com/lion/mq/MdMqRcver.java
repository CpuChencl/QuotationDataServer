package com.lion.mq;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.lion.frame.mq.IMqCallInterface;
import com.lion.frame.mq.MqConfig;
import com.lion.frame.mq.ZmqRcver;
import com.lion.sp.mqprocess.ProcessRtnNettyMsg;

@Lazy(false)
@Component
public class MdMqRcver {
	private static final Logger log = LoggerFactory.getLogger(MdMqRcver.class);

	@Value("${mdRcvMq.addr}")
	private String addr;

	@Value("${mdRcvMq.port}")
	private int port;

	@Value("${mdRcvMq.model}")
	private String model;

	@Value("${mdRcvMq.type}")
	private String type;
	
	@Autowired
	private MdMqSender mdMqSender;

	private ZmqRcver mdRcver;
	
	private volatile static int count = 0;
	
	public static Map<String, byte[]> mdByte = new HashMap<String, byte[]>();
	
	private ExecutorService executor;

	@PostConstruct
	public void init() {
		MqConfig mdRcverConfig = new MqConfig();
		mdRcverConfig.setAddr(addr);
		mdRcverConfig.setPort(port);
		mdRcverConfig.setModel(model);
		mdRcverConfig.setMqType(type);
		executor = Executors.newFixedThreadPool(8);
		
		mdRcver = new ZmqRcver(mdRcverConfig, new IMqCallInterface() {
			@Override
			public void onMessage(byte[] req) {
				count ++;
				ByteBuffer bb = ByteBuffer.wrap(req);
//				FbEsTapAPIQuoteWhole marketData = FbEsTapAPIQuoteWhole.getRootAsFbEsTapAPIQuoteWhole(bb);
//				if (marketData.QLastPrice() == 0) {// 如果当前价为0则过滤到
//					return;
//				}
				String contructNo = "";//marketData.Contract().Commodity().CommodityNo() + marketData.Contract().ContractNo1();
				mdMqSender.send(req);
			    mdByte.put(contructNo, req);
			    ProcessRtnNettyMsg process = new ProcessRtnNettyMsg(req, mdByte, contructNo,count);
			    if(count == 800){
//			    	log.info("fromMQByRec:futureCode:[{"+contructNo+"}],markettime:[{"+marketData.DateTimeStamp()+"}],qlprice:[{"+marketData.QLastPrice()+"}]");
			    	count = 0;
			    }
			    executor.submit(process);
			}
		});
	}
}