package org.rpc.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
import org.rpc.model.MethodMessage;
import org.rpc.model.RpcProtoModel;

import java.io.*;

public class Serializer {
    public final static int JDK_SERIALIZER = 0;
    public final static int JSON_SERIALIZER = 1;
    public final static int PROTOBUF_SERIALIZER = 2;
    public final static int HESSIAN_SERIALIZER = 3;
    public final static int KYRO_SERIALIZER = 4;
    private static Gson gson = new Gson();
    private static Kryo kryo = new Kryo();

    static{
        kryo.setRegistrationRequired(false);
    }


    public static byte[] jdkEncode(Object o)  {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(o);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("the jdk encoding occur errors");
            e.printStackTrace();
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();
        System.out.println("jdk encoding : the length of byte array is " + bytes.length);

        return bytes;
    }

    public static Object jdkDecode(byte[] bytes){

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = null;

        Object o = null;
        try {
            inputStream = new ObjectInputStream(byteArrayInputStream);
            o = inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("jdk decoding occur errors because of the byteArrayInputStream");
        } catch (ClassNotFoundException e) {
            System.out.println("jdk decoding occur errors because the class is not known");
            e.printStackTrace();
        }

        return o;
    }

    public static byte[] jsonEncode(Object message){
        return gson.toJson(message).getBytes();
    }

    public static Object jsonDecode(String message,Class clazz){
        return gson.fromJson(message,clazz);
    }

    public static byte[] encode(Object message,int encodeWay){
        if (encodeWay == Serializer.JDK_SERIALIZER){
            return Serializer.jdkEncode(message);
        }else if(encodeWay == Serializer.JSON_SERIALIZER){
            return Serializer.jsonEncode(message);
        }else if(encodeWay == Serializer.KYRO_SERIALIZER){
            return Serializer.kyroEncode(message);
        }
        System.out.println("-------unknown encoding way--------");
        return null;
    }

    public static Object decode(byte[] message,int encodeWay,Class clazz){
        if (encodeWay == (1<<Serializer.JSON_SERIALIZER)){
            return Serializer.jsonDecode(new String(message),clazz);
        }else if (encodeWay == (1<<Serializer.JDK_SERIALIZER)){
            return Serializer.jdkDecode(message);
        }else if(encodeWay == (1<<Serializer.KYRO_SERIALIZER)){
            return Serializer.kyroDecode(message,clazz);
        }
        System.out.println("-------unknown decoding way--------");
        return null;
    }

    public static byte[] kyroEncode(Object message){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeClassAndObject(output,message);
        output.close();

        byte[] bytes = outputStream.toByteArray();
        System.out.println("encode-----"+bytes.length+"*************************");
        System.out.println("kyroencoding... ");
        return bytes;
    }

    public static Object kyroDecode(byte[] message,Class clazz){

        System.out.println("decode-----"+message.length+"*************************");
        System.out.println("kyrodecoding... the class name is " + clazz.getName());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
        Input input = new Input(inputStream);
        input.close();
        return kryo.readClassAndObject(input);
    }

//    public Object hessianEncode(Object o){
//
//    }
//
//    public Object hessianDecode(Object o){
//
//    }
//
//    public Object protobufEncode(Object o){
//
//    }
//
//    public Object protobufDecode(Object o){
//
//    }


}
