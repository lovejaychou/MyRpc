package org.rpc.model;

/*
* Author: zmh
* function: clarify the form of the rpc proto
* time: 2021/11/1
* */
public class RpcProtoModel {

    public long magic;
    public byte version;
    public long streamId;
    public byte serialWay;
    public byte send;
    public int status;
    public byte[] message;

    public RpcProtoModel(long magic, byte version, long streamId, byte serialWay, byte[] message,byte send,int status) {
        this.magic = magic;
        this.version = version;
        this.streamId = streamId;
        this.serialWay = serialWay;
        this.message = message;
        this.send = send;
        this.status = status;
    }
}
