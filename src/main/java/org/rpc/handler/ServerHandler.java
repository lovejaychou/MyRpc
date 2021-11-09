package org.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.rpc.constant.RpcConstant;
import org.rpc.model.MethodMessage;
import org.rpc.model.RpcProtoModel;
import org.rpc.util.*;

/*
* Author: zmh
* function: solve the read of socket
* time 2021/10/30
* */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    public static int encodeWay = 1;//默认为json

    public ServerHandler(){

    }

    public ServerHandler(int encodeWay){
        this.encodeWay = encodeWay;
    }

    //当channel接收到数据后，调用的回调方法
    /**
     * 1、魔数         1个字节,协议的标志
     * 2、rpc版本号    1个字节，rpc的版本号
     * 3、流Id        8个字节，tpc连接复用，用来表示对应的请求
     * 4、statusCode  1个字节
     * 4、offset      8个字节，因为可能存在大文件，分帧传递，要不大文件的传递会影响其他请求的传递   XX
     * 5、序列化方式    4个bit，参数的序列化方式
     * 6、压缩方式      4个bit，对参数进行压缩，减少网络传输时间
     * 7、payload长度  8个字节，帧的长度
     *
     * 目前先不实现 offset 和 压缩
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("server read");
        final Channel channel = ctx.channel();
        MethodRunnable runnable = new MethodRunnable(channel, msg);
        int res = MyThreadPoolExecutor.handler(runnable);

        if (res == -1){//队列满了

            ByteBuf rcvByteBuf = (ByteBuf)msg;

            RpcProtoModel model = ProtoHandler.protoDecoder(rcvByteBuf);
            byte[] sendMessage = Serializer.encode(null,encodeWay);
            System.out.println("server-------streamId:"+model.streamId+"-----blocking queue is full");
            ByteBuf sebdByteBuf = ProtoHandler.protoEncoder(model.streamId,sendMessage,Serializer.JSON_SERIALIZER, RpcConstant.QUEUE_FULL,RpcConstant.RESPONSE);

            ChannelSendUtil.send(channel, sebdByteBuf);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server complete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server active");
        super.channelActive(ctx);
    }
}
