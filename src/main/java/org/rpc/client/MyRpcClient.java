package org.rpc.client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.rpc.constant.RpcConstant;
import org.rpc.handler.ClientHandler;
import org.rpc.handler.ServerHandler;
import org.rpc.model.MethodMessage;
import org.rpc.model.Result;
import org.rpc.test.Person;
import org.rpc.util.ProtoHandler;
import org.rpc.util.Serializer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<Long,Thread> threadMap = null;
    private volatile Map<Long,Object> data = null;
    private Map<Long,Class> resultMap = null;

    public MyRpcClient(String ip,int port){
        this.ip = ip;
        this.port = port;
        lock = new ReentrantLock();
        threadMap = new HashMap<Long, Thread>();
        data = new ConcurrentHashMap<Long, Object>();
        resultMap = new ConcurrentHashMap<Long, Class>();
    }

    public MyRpcClient(){
        lock = new ReentrantLock();
        threadMap = new HashMap<Long, Thread>();
        data = new ConcurrentHashMap<Long, Object>();
        resultMap = new ConcurrentHashMap<Long, Class>();
    }


    public void init() throws InterruptedException {
        bootstrap = new Bootstrap();
        EventLoopGroup client = new NioEventLoopGroup();

        bootstrap.group(client)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //用于解码，lengthFieldOffset是长度字段的偏移字节量、lengthFieldLength是长度字段占用的字节个数
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                        //LengthFieldPrepender是一个outBoundHandler，它会帮我们把数据包的长度对应的长度填写到长度字段中
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new ClientHandler(threadMap,data,resultMap));
                    }
                });
        ChannelFuture future = bootstrap.connect(ip, port).sync();
        channel = future.channel();


        //future.channel().closeFuture().sync();
    }


    public void test(final Object[] params){

        new Thread(){
            @Override
            public void run() {
                System.out.println(Arrays.toString(params));
                MethodMessage message = new MethodMessage("org.rpc.test","Person","buildPerson",params);
                long streamId = send(message,Person.class);
                System.out.println("client send the message successfully"+Arrays.toString(params)+"---"+streamId);

                Result result = (Result) receiveBlock(streamId);
                if(result.status == RpcConstant.OK){
                    System.out.println(((Person)result.result).toString());
                }else {
                    System.out.println(streamId+"stream has a error call,the status is"+result.status);
                }

            }
        }.start();


    }

    //发送对应的信息,自定义的rpc传输协议
    public long send(MethodMessage msg,Class resultClass){

        long sendId = -1;
        try {
            lock.lock();
            sendId = streamId++;
            resultMap.put(sendId,resultClass);
            byte[] message = Serializer.encode(msg, Serializer.JDK_SERIALIZER);
            ByteBuf byteBuf = ProtoHandler.protoEncoder(sendId,message,Serializer.JDK_SERIALIZER, RpcConstant.OK,RpcConstant.SEND);
            channel.writeAndFlush(byteBuf);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return sendId;
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

    public static void main(String[] args) throws InterruptedException {
        final MyRpcClient client = new MyRpcClient();
        client.init();







        new Thread(){
            @Override
            public void run() {
                Object[] params = new Object[2];
                params[0] = "zmhddddddddddddddddddddddddddddddddddddddddddddd";
                params[1] = 22;
                client.test(params);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Object[] params1 = new Object[2];
                params1[0] = "zmh2333333333333333333333333333333333333333333333333333333333333333333333";
                params1[1] = 24;
                client.test(params1);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "zmh35555555555555555555555555555555555555555555555555555555555555555555555";
                params3[1] = 26;
                client.test(params3);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "zmh35555555555555555555555555555555555555555555555555555555555555555555555";
                params3[1] = 26;
                client.test(params3);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "zmh35555555555555555555555555555555555555555555555555555555555555555555555";
                params3[1] = 26;
                client.test(params3);
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "zmh35555555555555555555555555555555555555555555555555555555555555555555555";
                params3[1] = 26;
                client.test(params3);
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "zmh35555555555555555555555555555555555555555555555555555555555555555555555";
                params3[1] = 26;
                client.test(params3);
            }
        }.start();

    }
}
