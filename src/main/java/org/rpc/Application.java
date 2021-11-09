package org.rpc;

import org.rpc.server.MyRpcServer;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        MyRpcServer rpcServer = new MyRpcServer(8888);
        rpcServer.init();
    }
}
