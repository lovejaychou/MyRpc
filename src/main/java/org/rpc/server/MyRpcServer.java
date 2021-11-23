package org.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.rpc.handler.ServerHandler;
import org.rpc.thread.MyThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/*
* @Author: zmh
* function: provide a tcp server base on the nio mode of netty.
* time: 2021/10/29
* */
public class MyRpcServer {

    //server的tcp端口
    private int port = 8888;
    //接收新socket的nio模型group，一个NioEventLoop对应着一个selector。一个NioEventLoopGroup中有多个NioEventLoop
    private EventLoopGroup accept = null;
    //接收客户端数据的nio模型group
    private EventLoopGroup read = null;

    //Nio Server Socket对象
    private ServerBootstrap serverBootstrap = null;

    private int coreThreadSize;

    private BlockingQueue<Runnable> queue;

    private ThreadFactory threadFactory;


    public MyRpcServer(int port){
        this(port,Runtime.getRuntime().availableProcessors(),new LinkedBlockingQueue<Runnable>(1024), Executors.defaultThreadFactory());
    }

    public MyRpcServer(int port,int coreThreadSize){
        this(port,coreThreadSize,new LinkedBlockingQueue<Runnable>(1024), Executors.defaultThreadFactory());
    }

    public MyRpcServer(int port,int coreThreadSize,BlockingQueue<Runnable> queue){
        this(port,coreThreadSize,queue, Executors.defaultThreadFactory());
    }

    public MyRpcServer(int port,int coreThreadSize,BlockingQueue<Runnable> queue,ThreadFactory threadFactory){
        this.port = port;
        this.coreThreadSize = coreThreadSize;
        this.queue = queue;
        this.threadFactory = threadFactory;
    }


    //初始化RpcServer
    public void init() throws InterruptedException {
        accept = new NioEventLoopGroup(1);
        read = new NioEventLoopGroup();
        MyThreadPoolExecutor.init(coreThreadSize,threadFactory,queue);
        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(accept,read)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });

            System.out.println("server has started succesfully,the port is "+port);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("netty has a error");
        }finally {
            System.out.println("netty has closed");
            accept.shutdownGracefully();
            read.shutdownGracefully();
        }

    }
}
