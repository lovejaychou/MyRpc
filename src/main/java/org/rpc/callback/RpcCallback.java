package org.rpc.callback;

public interface RpcCallback<T> {

    public void callback(T res);
}
