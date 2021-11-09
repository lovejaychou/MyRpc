package org.rpc.test;

import java.io.Serializable;

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
}
