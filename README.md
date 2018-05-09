# QuotationDataServer
QuotationDataServer
1.通过zeroMQ接收上游系统数据
2.将收到的数据通过zeroMQ发送出去
3.将收到的数据通过netty server发送出去
4.注册netty server 服务端，管理netty client连接
