package com.lion.frame.netty.socket.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.lion.frame.logger.Log4jManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = Log4jManager.get();
	
	private static Map<String, ChannelHandlerContext> channelMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
	private static Map<ChannelId, String> channelId = new ConcurrentHashMap<ChannelId, String>();
	
	public static Map<String,List<String>> sysClientInfo = new ConcurrentHashMap<String,List<String>>();
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		// channel失效，从Map中移除
		NettyServerHandler.removeChannel(ctx);
		log.info("连接关闭:[{}]", new Object[]{ctx});
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx,Object receiveMsg) throws Exception{
		ByteBuf buf = (ByteBuf) receiveMsg;  
        byte[] data = new byte[buf.readableBytes()];  
        buf.readBytes(data);
        String clientMsg = new String(data).trim();
		log.info("request msg : [{}]", clientMsg);
		if(!StringUtils.isEmpty(clientMsg)){
			String [] clientInfo = clientMsg.split(":");
			String sys = clientInfo[0];
			String contractStr = clientInfo[1];
			if(!StringUtils.isEmpty(sys)){
				sysClientInfo.remove(sys);
				List<String> contractList = new ArrayList<String>();
				if(!StringUtils.isEmpty(contractStr)){
					if(contractStr.contains("*") && contractStr.length()==1){
						contractList.add("*");
					} else{
						String []con = contractStr.split(",");
						if(con != null && con.length > 0){
							contractList = Arrays.asList(contractStr.split(","));
						}
					}
				}
				sysClientInfo.put(sys, contractList);
				NettyServerHandler.putChannel(sys,ctx);
			}
		}
	}
	
	public static void add(String clientId, ChannelHandlerContext socketChannel) {
		channelMap.put(clientId, socketChannel);
	}

	public static ChannelHandlerContext get(String operatorId) {
		return channelMap.get(operatorId);
	}
	
	public static void removeChannel(ChannelHandlerContext ctx) {
		String operatorId = channelId.get(ctx.channel().id());
		if (operatorId != null) {
			channelMap.remove(operatorId);
		}
		channelId.remove(ctx.channel().id());
	}

	public static void putChannel(String operatorId, ChannelHandlerContext ctx) {
		channelId.put(ctx.channel().id(), operatorId);
		channelMap.put(operatorId, ctx);
	}
	
	public static Map<String, ChannelHandlerContext> getChannelMap() {
		return channelMap;
	}

	public static Map<ChannelId, String> getChannelId() {
		return channelId;
	}
	
	public static String getSysKey(ChannelHandlerContext ctx){
		return channelId.get(ctx.channel().id());
	}
}