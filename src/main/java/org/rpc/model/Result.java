package org.rpc.model;

public class Result {

    public int status;
    public Object result;

    public Result(int status,Object result){
        this.status = status;
        this.result = result;
    }
}
