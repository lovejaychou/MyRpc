package org.rpc.model;

public interface Future {

    public Object get();
    public void set(Object o);
    public boolean isDone();
}
