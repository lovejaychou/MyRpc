package org.rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.rpc.constant.RpcConstant;
import org.rpc.model.Future;
import org.rpc.model.Result;
import org.rpc.callback.RpcCallback;
import org.rpc.model.RpcFuture;
import org.rpc.model.RpcProtoModel;
import org.rpc.thread.ClientThreadPoolExecutor;
import org.rpc.thread.ResultRunnable;
import org.rpc.util.ProtoHandler;
import org.rpc.util.Serializer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/*
 * Author: zmh
 * function: solve the read of socket
 * time 2021/10/30
 * */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Map<Long,Object> threadMap = null;
    private Map<Long,Object> data = null;
    private Map<Long,Class> resultMap = null;
    public ClientHandler(Map<Long,Object> threadMap,Map<Long,Object> data,Map<Long,Class> resultMap){
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
        final Object o1 = threadMap.get(o.streamId);
        System.out.println("客户端收到回复:"+o.streamId);
        if(o.status == RpcConstant.OK){
            final Object res = Serializer.decode(o.message,o.serialWay,objClass);
            if(o1 instanceof Thread){
                data.put(o.streamId, new Result(o.status,res));
                LockSupport.unpark((Thread)o1);
            }else if(o1 instanceof RpcCallback){
                ResultRunnable runnable = new ResultRunnable((RpcCallback) o1,res);
                ClientThreadPoolExecutor.handler(runnable);
            }else if (o1 instanceof RpcFuture){
                Thread.sleep(3000);
                data.put(o.streamId, new Result(o.status,res));
                ClientThreadPoolExecutor.handler((RpcFuture)o1);
            }
        }else{
            data.put(o.streamId, new Result(o.status,null));
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("client is inactive"+new Date().getTime());
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