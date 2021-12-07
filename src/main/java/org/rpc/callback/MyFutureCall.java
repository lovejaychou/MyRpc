package org.rpc.callback;

import org.rpc.model.Future;

import java.util.Map;

public class MyFutureCall implements RpcFutureCall{

    private Map<Long, Object> data;
    private long id;
    public MyFutureCall(Map<Long, Object> data,long id){
        this.data = data;
        this.id = id;
    }

    public Object call() {
        return data.get(id);
    }
}
