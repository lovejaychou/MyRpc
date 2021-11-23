package org.rpc.test;

import org.rpc.client.MyRpcClient;
import org.rpc.constant.RpcConstant;
import org.rpc.model.MethodMessage;
import org.rpc.model.Result;
import org.rpc.callback.RpcCallback;

import java.util.Arrays;

public class TestClient2 {

    public static void main(String[] args) throws InterruptedException {
        final MyRpcClient client = new MyRpcClient();
        client.init();

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
                params3[1] = 26;
                test(client,params3);
            }
        }.start();

        Thread.sleep(2000);

        new Thread(){
            @Override
            public void run() {
                Object[] params3 = new Object[2];
                params3[0] = "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
                params3[1] = 22;
                test2(client,params3);
            }
        }.start();

    }

    public static void test(final MyRpcClient client,final Object[] params) {

        new Thread() {
            @Override
            public void run() {
                System.out.println(Arrays.toString(params));
                MethodMessage message = new MethodMessage("org.rpc.test", "Person", "buildPerson", params);
                long streamId = client.send(message, Person.class);
                System.out.println("client send the message successfully" + Arrays.toString(params) + "---" + streamId);

                Result result = (Result) client.receiveBlock(streamId);
                if (result.status == RpcConstant.OK) {
                    System.out.println(((Person) (result.result)).toString());
                } else {
                    System.out.println(streamId + "stream has a error call,the status is" + result.status);
                }

            }
        }.start();

    }

    public static void test2(final MyRpcClient client,final Object[] params) {

        new Thread() {

            @Override
            public void run() {
                System.out.println(Arrays.toString(params));
                System.out.println("-------------------------"+Thread.currentThread().getName());
                MethodMessage message = new MethodMessage("org.rpc.test", "Person", "buildPerson", params);
                client.sendAsync(message, Person.class,new RpcCallback<Person>(){

                    public void callback(Person res) {
                        System.out.println(Thread.currentThread().getName());
                        System.out.println(res.toString());
                    }

                });

            }
        }.start();

    }

}
