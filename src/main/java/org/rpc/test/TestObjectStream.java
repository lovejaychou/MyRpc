package org.rpc.test;

import com.google.gson.Gson;

import java.io.*;
import java.util.Arrays;

public class TestObjectStream {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
//        outputStream.writeObject(new Person("zmh",22));
//
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        System.out.println(bytes.length);
//
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
//
//        Person o = (Person)inputStream.readObject();
//        System.out.println(o.toString());


        byte[] bytes = convertToByteArray(305);

        System.out.println(Arrays.toString(bytes));

        int x = convertToInt(bytes);
        System.out.println(x);
    }


    public static byte[] convertToByteArray(int num){
        byte[] bytes = new byte[4];
        long base = 255;
        int i = 0;
        while(num > 0){
            System.out.println(base&num);
            bytes[i++] = (byte)(base&num);
            num>>=8;
        }
        return bytes;
    }

    public static int convertToInt(byte[] bytes){
        int streamId = 0;
        int base = 0;
        for(int i = 0;i < 4;i++){
            streamId |= (bytes[i]<<base);
            base+=8;
        }

        return streamId;
    }
}
