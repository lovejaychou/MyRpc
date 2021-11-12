package org.rpc.model;

public interface RpcCallback<T> {

    public void callback(T res);
}
