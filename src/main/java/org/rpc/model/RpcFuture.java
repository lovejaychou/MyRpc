package org.rpc.model;

import org.rpc.callback.RpcFutureCall;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture implements Future,Runnable{
    //state == -1表示还未完成
    //      == 1 表示已完成
    private int state = -1;
    private Thread thread = null;
    private Object res;
    private ReentrantLock lock = new ReentrantLock();

    private RpcFutureCall rpcFutureCall = null;
    public RpcFuture(RpcFutureCall rpcFutureCall){
        this.rpcFutureCall = rpcFutureCall;
    }

    public Object get() {

        while (!isDone()){
            thread = Thread.currentThread();
            LockSupport.park();
        }

        return res;
    }

    public void set(Object res) {
        lock.lock();
        this.res = res;
        state = 1;
        if (thread != null){
            LockSupport.unpark(thread);
        }
        lock.unlock();
    }

    public boolean isDone() {
        return state == 1;
    }

    public void run() {
        set(rpcFutureCall.call());
    }
}

class WaitNode{
    private Thread thread;

}