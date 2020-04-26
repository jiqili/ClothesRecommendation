package com.baidu.speech.myapplication;

public class url {
//    private static String string = "http://10.0.2.2:8080";
//    private static String string = "http://192.168.2.241:8080";
private static String string = "http://139.196.167.232:8080";
    public static String getPath(String childPath){
        return string + childPath;
    }
}
