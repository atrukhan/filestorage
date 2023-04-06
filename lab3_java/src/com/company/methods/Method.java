package com.company.methods;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;

public abstract class Method {

    private static String dir = Paths.get("").toAbsolutePath().toString() + "\\filesstorage\\";

    public static String getDir() {
        return dir;
    }

    public static String[] getParams(InputStream body, String[] params) throws IOException{
        StringBuilder bodystr = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(body));
        String line;
        while ((line = in.readLine()) != null) {
            bodystr.append(URLDecoder.decode(line));
        }

        String[] result = new String[params.length];
        int i = 0;
        for (String param : params) {
            int start = bodystr.indexOf(param + "=") + param.length() + 1;
            if (start > bodystr.lastIndexOf("&")) {
                result[i++] = bodystr.substring(start);
            } else {
                int end = bodystr.substring(start).indexOf("&");
                result[i++] = bodystr.substring(start, end + start);
            }
        }
        in.close();
        return result;
    }

    public static String[] getParams(StringBuilder bodystr, String[] params) throws IOException{

        String[] result = new String[params.length];
        int i = 0;
        for (String param : params) {
            int start = bodystr.indexOf(param + "=") + param.length() + 1;
            if (start > bodystr.lastIndexOf("&")) {
                result[i++] = bodystr.substring(start);
            } else {
                int end = bodystr.substring(start).indexOf("&");
                result[i++] = bodystr.substring(start, end + start);
            }
        }
        return result;
    }


    public static long generateUserID(HashMap<String, String> publicKeyMap){
        long id = 0;
        Random random = new Random();
        id = random.nextInt(100000);
        if(publicKeyMap.get(Long.toString(id)) != null){
            id = generateUserID(publicKeyMap);
        }
        return id;
    }


    public abstract void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException;
    public abstract void doMethodForServer(HttpExchange exchange, String filename, HashMap<String, String> keyMap, long publicKey, long rKey)throws IOException;
}
