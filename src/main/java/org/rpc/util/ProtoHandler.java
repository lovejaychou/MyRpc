package org.rpc.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.rpc.model.MethodMessage;
import org.rpc.model.RpcProtoModel;

import java.util.Arrays;

/**
 *
 * the encoding and decoding of the rpc protocol
 */
public class ProtoHandler {

    /**
     * 1、魔数         1个字节,协议的标志
     * 2、rpc版本号    1个字节，rpc的版本号
     * 3、流Id        8个字节，tpc连接复用，用来表示对应的请求
     * 4、offset      8个字节，因为可能存在大文件，分帧传递，要不大文件的传递会影响其他请求的传递
     * 5、序列化方式    4个bit，参数的序列化方式
     * 6、压缩方式      4个bit，对参数进行压缩，减少网络传输时间
     * 7、payload长度  8个字节，帧的长度
     * 8、statusCode  1个字节  200为成功，400为错误
     *
     * 目前先不实现 offset 和 压缩
     * */
    private final static byte magic = (byte)255;
    private final static byte version = (byte)1;



    /*
    * the client envelop the information of the class and the method that the client wants to call  and the params into a ByteBuf
    * */
    public static ByteBuf protoEncoder(long streamId, byte[] message,int serialWay,int status,byte send){
        ByteBuf byteBuf = Unpooled.buffer();


        int payLoadLen = message.length;

        byteBuf.writeByte(magic);//1

        byteBuf.writeByte(version);//2

        byteBuf.writeLong(streamId);

        byteBuf.writeByte(getSerial(serialWay));

        byteBuf.writeInt(status);

        byteBuf.writeByte(send);

        byteBuf.writeInt(payLoadLen);

        byteBuf.writeBytes(message);


        return byteBuf;
    }

    /*
    * turn the protocol into the real class and method information as well as params
    * */
    public static RpcProtoModel protoDecoder(ByteBuf byteBuf){

        byte magic = byteBuf.readByte();

        byte version = byteBuf.readByte();

        long streamId = byteBuf.readLong();

        byte serialWay = byteBuf.readByte();

        int status = byteBuf.readInt();

        byte send = byteBuf.readByte();

        int payLoadLen = byteBuf.readInt();

        byte[] payLoad = new byte[payLoadLen];

        byteBuf.readBytes(payLoad);

        return new RpcProtoModel(magic,version,streamId,serialWay,payLoad,send,status);
    }

    public static byte getSerial(int serialWay){
        return (byte) (1<<serialWay);
    }

}
