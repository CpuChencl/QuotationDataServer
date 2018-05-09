package com.lion.sp.mqprocess;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lion.frame.netty.socket.server.NettyServerHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ProcessRtnNettyMsg implements Callable<String>{

	private Logger log = LoggerFactory.getLogger(ProcessRtnNettyMsg.class);
	
	private byte[] b;
	private Map<String, byte[]> mdByte;
	private String contructNo;
	private int count;
	public ProcessRtnNettyMsg(byte[] b,Map<String, byte[]> mdByte,String contructNo,int count){
		this.b = b;
		this.mdByte = mdByte;
		this.contructNo = contructNo;
		this.count = count;
	}
	
	@Override
	public String call() throws Exception {
		try {
			parseResp(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 *  0001 - 成交回报
	 *  0002 - 
	 */
	private void parseResp(byte[] req){
		
		Map<String, ChannelHandlerContext> map = NettyServerHandler.getChannelMap();
		List<ChannelHandlerContext> chList = new ArrayList<ChannelHandlerContext>(map.values());
		byte [] split = "$$$".getBytes();
		for(ChannelHandlerContext ch : chList){
			List<String> contractList = NettyServerHandler.sysClientInfo.get(NettyServerHandler.getSysKey(ch));
			for(String c : contractList){
				if(c.equals("*")){
					byte[] data3 = new byte[req.length + split.length];  
				    System.arraycopy(req, 0, data3, 0, req.length);  
				    System.arraycopy(split, 0, data3, req.length, split.length); 
					ch.writeAndFlush(Unpooled.copiedBuffer(data3));
					ByteBuffer bb = ByteBuffer.wrap(req);
//					FbEsTapAPIQuoteWhole marketData = FbEsTapAPIQuoteWhole.getRootAsFbEsTapAPIQuoteWhole(bb);
//					String contructNo = marketData.Contract().Commodity().CommodityNo() + marketData.Contract().ContractNo1();
					if(count == 800){
//				    	log.info("fromMQByPro:futureCode:[{"+contructNo+"}],markettime:[{"+marketData.DateTimeStamp()+"}],qlprice:[{"+marketData.QLastPrice()+"}]");
				    }
				} else {
					if(mdByte.get(c)==null){
						continue;
					}
					if(c.equals(contructNo)){
						ByteBuffer bb = ByteBuffer.wrap(req);
//						FbEsTapAPIQuoteWhole marketData = FbEsTapAPIQuoteWhole.getRootAsFbEsTapAPIQuoteWhole(bb);
						byte[] data3 = new byte[req.length + split.length];  
					    System.arraycopy(req, 0, data3, 0, req.length);  
					    System.arraycopy(split, 0, data3, req.length, split.length);  
//						String contructNo = marketData.Contract().Commodity().CommodityNo() + marketData.Contract().ContractNo1();
						if(count == 800){
//					    	log.info("fromMQByPro:futureCode:[{"+contructNo+"}],markettime:[{"+marketData.DateTimeStamp()+"}],qlprice:[{"+marketData.QLastPrice()+"}]");
					    }
						ch.writeAndFlush(Unpooled.copiedBuffer(data3));
					}
				}
			}
		}
	}
}