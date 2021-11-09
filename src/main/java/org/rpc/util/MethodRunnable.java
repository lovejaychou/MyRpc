package org.rpc.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.rpc.constant.RpcConstant;
import org.rpc.handler.MethodHandler;
import org.rpc.model.MethodMessage;
import org.rpc.model.RpcProtoModel;


import static org.rpc.handler.ServerHandler.encodeWay;

public class MethodRunnable implements Runnable {

    private Channel channel;
    private Object msg;

    public MethodRunnable(Channel channel, Object msg){

        this.channel = channel;
        this.msg = msg;
    }

    public void run() {

        ByteBuf rcvByteBuf = (ByteBuf)msg;

        RpcProtoModel model = ProtoHandler.protoDecoder(rcvByteBuf);
        MethodMessage rcvMessage = (MethodMessage) Serializer.decode(model.message,model.serialWay,MethodMessage.class);

        Object result = MethodHandler.invokeMethod(rcvMessage);
        byte[] sendMessage = Serializer.encode(result,encodeWay);
        System.out.println("server-------streamId:"+model.streamId);
        ByteBuf sebdByteBuf = ProtoHandler.protoEncoder(model.streamId,sendMessage,Serializer.JSON_SERIALIZER, RpcConstant.OK,RpcConstant.RESPONSE);

        ChannelSendUtil.send(channel, sebdByteBuf);

    }
}
