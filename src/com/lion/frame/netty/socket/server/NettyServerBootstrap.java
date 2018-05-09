package com.lion.frame.netty.socket.server;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.lion.frame.logger.Log4jManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

@Lazy(false)
@Component
public class NettyServerBootstrap {
	private static final Logger log = Log4jManager.get();
	@Value("${SOCKET.SERVER.PORT}")
	private int port;
	
	@PostConstruct
	public void bind() throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		/*
		 * ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数。
		 * 函数listen(int socketfd, int backlog)用来初始化服务端可连接队列。
		 * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
		 * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
		 */
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);//
		// 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		/*
		 * 容量动态调整的接收缓冲区分配器，它会根据之前Channel接收到的数据报大小进行计算，
		 * 如果连续填充满接收缓冲区的可写空间，则动态扩展容量。如果连续2次接收到的数据报都小于指定值，
		 * 则收缩当前的容量，以节约内存
		 */
		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
		// 保持长连接状态
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);// 2小时无数据激活心跳机制
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				ChannelPipeline pipeline = socketChannel.pipeline();
				ByteBuf delimiter = Unpooled.copiedBuffer("$$$".getBytes());
				pipeline.addLast(new DelimiterBasedFrameDecoder(5120,delimiter));
				pipeline.addLast(new NettyServerHandler());
			}
		});
		ChannelFuture f = bootstrap.bind(port).sync();// 绑定端口，同步等待成功
		if (f.isSuccess()) {
			log.info("---------------socket服务启动成功---------------");
		}else{
			log.info("---------------socket服务启动失败---------------");
		}
	}
}