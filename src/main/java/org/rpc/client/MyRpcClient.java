package org.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.rpc.constant.RpcConstant;
import org.rpc.handler.ClientHandler;
import org.rpc.model.MethodMessage;
import org.rpc.callback.RpcCallback;
import org.rpc.thread.ClientThreadPoolExecutor;
import org.rpc.util.ProtoHandler;
import org.rpc.util.Serializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class MyRpcClient {

    private Bootstrap bootstrap = null;
    private String ip = "127.0.0.1";
    private int port = 8888;
    private Channel channel;
    private long streamId = 1000000000000000000l;
    private Lock lock = null;
    private final static int ENCODE_WAY = Serializer.JDK_SERIALIZER;
    private Map<Long,Object> threadMap = null;
    private volatile Map<Long,Object> data = null;
    private Map<Long,Class> resultMap = null;


    public MyRpcClient(String ip,int port){
        this.ip = ip;
        this.port = port;
        lock = new ReentrantLock();
        threadMap = new ConcurrentHashMap<Long, Object>();
        data = new ConcurrentHashMap<Long, Object>();
        resultMap = new ConcurrentHashMap<Long, Class>();
    }

    public MyRpcClient(){
        lock = new ReentrantLock();
        threadMap = new HashMap<Long, Object>();
        data = new ConcurrentHashMap<Long, Object>();
        resultMap = new ConcurrentHashMap<Long, Class>();
    }


    public void init() throws InterruptedException {
        bootstrap = new Bootstrap();
        ClientThreadPoolExecutor.init(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory(),new LinkedBlockingQueue<Runnable>(1024));
        EventLoopGroup client = new NioEventLoopGroup();

        try {
            bootstrap.group(client)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //用于解码，lengthFieldOffset是长
                            // 度字段的偏移字节量、lengthFieldLength是长度字段占用的字节个数
                            socketChannel.pipeline().addFirst(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            //LengthFieldPrepender是一个outBoundHandler，它会帮我们把数据包的长度对应的长度填写到长度字段中
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                            socketChannel.pipeline().addLast(new ClientHandler(threadMap,data,resultMap));
                        }
                    });
            try {
                ChannelFuture future = bootstrap.connect(ip, port).sync();
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        System.out.println("连接成功");
                    }
                });
                channel = future.channel();
            }catch (Exception e){
                e.printStackTrace();
            }


            //future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //client.shutdownGracefully();
        }


    }


    //发送对应的信息,自定义的rpc传输协议
    public long send(MethodMessage msg,Class resultClass){

        long sendId = -1;
        try {
            lock.lock();
            sendId = streamId++;
            resultMap.put(sendId,resultClass);
            byte[] message = Serializer.encode(msg, MyRpcClient.ENCODE_WAY);
            ByteBuf byteBuf = ProtoHandler.protoEncoder(sendId,message,MyRpcClient.ENCODE_WAY, RpcConstant.OK,RpcConstant.SEND);
            channel.writeAndFlush(byteBuf);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return sendId;
    }

    //异步调用，需要用户传入对应的回调函数。当接收到对应的数据后，rpc框架会自动使用线程池，调用对应的回调函数
    public void sendAsync(MethodMessage msg, Class resultClass, RpcCallback callback){
        long streamId = send(msg,resultClass);
        threadMap.put(streamId,callback);
    }

    //阻塞的获取数据，如果数据还未回来，就挂起
    public Object receiveBlock(long sendId){
        while(!data.containsKey(sendId)){
            Thread thread = Thread.currentThread();
            threadMap.put(sendId,thread);
            LockSupport.park();
        }

        System.out.println("线程被唤醒了"+sendId);
        return data.get(sendId);
    }

    //非阻塞的获取数据，如果数据还没有回来，就直接返回数据
    public Object receive(long sendId){
        return data.get(sendId);
    }

}
