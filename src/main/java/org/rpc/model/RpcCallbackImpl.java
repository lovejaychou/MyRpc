package org.rpc.model;

import org.rpc.test.Person;

public class RpcCallbackImpl implements RpcCallback<Person>{


    public void callback(Person res) {
        System.out.println(res.toString());
    }
}
