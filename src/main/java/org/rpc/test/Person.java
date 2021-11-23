package org.rpc.test;

import org.rpc.thread.ClientThreadPoolExecutor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Person implements Serializable {

    private double age;
    private String name;

    public Person(){

    }
    public Person(String name, double age){
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public Person buildPerson(String name,double age){
        return new Person(name,age);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class clazz = Class.forName("org.rpc.test.Person");
        for(Method method:clazz.getMethods()){
            System.out.println(method.getName()+"---"+Arrays.toString(method.getParameterTypes()) );
        }
    }
}
