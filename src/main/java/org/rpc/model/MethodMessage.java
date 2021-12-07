package org.rpc.model;

import java.io.Serializable;

/*
* Author: zmh
* function: the model of RcpMessage via the tcp
* time: 2021/10/30
* */
public class MethodMessage implements Serializable {

    private String classPath;
    private String className;
    private String method;
    private Object[] params;
    private Class[] types;

    public MethodMessage(){

    }

    public String getClassPath() {
        return classPath;
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }

    public Class[] getTypes(){
        return types;
    }

    public MethodMessage(String classpath, String className, String method, Object[] params,Class[] types){
        this.classPath = classpath;
        this.className = className;
        this.params = params;
        this.method = method;
        this.types = types;
    }

}
