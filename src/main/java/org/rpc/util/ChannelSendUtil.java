package org.rpc.util;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class ChannelSendUtil {

    public static void send(Channel channel, ByteBuf byteBuf){
        channel.writeAndFlush(byteBuf);
    }
}
