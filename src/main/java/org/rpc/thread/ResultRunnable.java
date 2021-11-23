package org.rpc.thread;

import org.rpc.callback.RpcCallback;

public class ResultRunnable implements Runnable {

    private Object rpcCallback;
    private Object result;

    public ResultRunnable(Object callback, Object result){
        this.rpcCallback = callback;
        this.result = result;
    }

    public void run() {
        RpcCallback callback = (RpcCallback)rpcCallback;
        callback.callback(result);
    }
}
