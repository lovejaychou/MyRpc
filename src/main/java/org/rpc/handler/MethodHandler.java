package org.rpc.handler;


import org.rpc.model.MethodMessage;
import org.rpc.model.RpcProtoModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/*
* 用来执行方法
* */
public class MethodHandler {

    public static Object invokeMethod(MethodMessage message){

        String classPath = message.getClassPath();
        String className = message.getClassName();
        String methodName = message.getMethod();
        Object[] params = message.getParams();
        Class[] types = message.getTypes();

        Class objClass = findClass(classPath,className);

        System.out.println("invokeMethod*******"+methodName);
        Method objMethod = null;
//        for (Method method:objClass.getMethods()){
//            if (method.getName().equals(methodName)){
//                objMethod = method;
//            }
//        }
        try {
            objMethod = objClass.getMethod(methodName,types);
        } catch (NoSuchMethodException e) {
            System.out.println("there is no method ->"+className+"/"+methodName);
            return null;
        }

        Object result = null;
        try {
            System.out.println(Arrays.toString(params));
            result = objMethod.invoke(objClass.newInstance(),params);
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException---"+ Arrays.toString(params));
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException----"+ Arrays.toString(params));
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("InstantiationException----"+ Arrays.toString(params));
            e.printStackTrace();
        }

        return result;
    }

    public static Class findClass(String classPath,String className){
        Class objClass = null;
        try {
            objClass = Class.forName(classPath+"."+className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("-----未找到名字为"+classPath+"."+className+"的类-----");
            return null;
        }

        return objClass;
    }
}
