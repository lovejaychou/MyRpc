package org.rpc.handler;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.xml.XmlElementStart;
import org.rpc.constant.RpcConstant;
import org.rpc.model.Result;
import org.rpc.model.RpcProtoModel;
import org.rpc.util.ProtoHandler;
import org.rpc.util.Serializer;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/*
 * Author: zmh
 * function: solve the read of socket
 * time 2021/10/30
 * */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Map<Long,Thread> threadMap = null;
    private Map<Long,Object> data = null;
    private Map<Long,Class> resultMap = null;
    public ClientHandler(Map<Long,Thread> threadMap,Map<Long,Object> data,Map<Long,Class> resultMap){
        this.threadMap = threadMap;
        this.data = data;
        this.resultMap = resultMap;
    }

    //当channel接收到数据后，调用的回调方法
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        ByteBuf byteBuf = (ByteBuf)msg;

        RpcProtoModel o = ProtoHandler.protoDecoder(byteBuf);
        Class objClass = resultMap.get(o.streamId);
        Thread thread = threadMap.get(o.streamId);
        System.out.println("客户端收到回复:"+o.streamId);
        if(o.status == RpcConstant.OK){
            data.put(o.streamId, new Result(o.status,Serializer.decode(o.message,o.serialWay,objClass)));
        }else{
            data.put(o.streamId, new Result(o.status,null));
        }

        LockSupport.unpark(thread);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client complete");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client active");
    }
}