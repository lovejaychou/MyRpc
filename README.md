# MyRpc
build a rpc frame

基础技术

1、Netty

2、使用线程池和任务队列来处理rpc调用任务

<<<<<<< HEAD
3、支持多种编码和解码方式（json、Java原生、protobuf,kryo）,

4、分布式  zookeeper
=======
3、支持多种编码和解码方式（json、Java原生）

4、分布式（后续实现）
>>>>>>> 96283036d8dae08cd4b15ce8efa9fab453f32271

5、将数据包分帧发送，然后在服务器端进行组装，因为TCP是保证顺序传达的，所以对于一个数据帧只需要新加入一个结束标记就可以了
      对于大数据包来说，数据帧的发送间隔要随着数据包的大小进行动态调整，让小数据包的帧先被运输
      因为大数据包对回答的延迟的容忍度比较高（后续实现）
                      
6、利用LengthFieldBasedFrameDecoder来解决粘包和分包


