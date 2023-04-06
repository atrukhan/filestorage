package com.company;

import com.company.methodsHandler.*;
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import com.company.RSA;

public class Client {

    private static Scanner in = new Scanner(System.in);
    private static String host = "localhost";
    private static int port = 4444;

    private static long privateKey = 0;
    private static long publicKey = 0;
    private static long r = 0;
    private static long id = 0;

    private static long publicKeyEncrypt = 0;
    private static long rEncrypt = 0;

    private static HashMap<String, MethodsHandler> methodsMap = new HashMap<String, MethodsHandler>();


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static String[] getParams(StringBuilder bodystr, String[] params) throws IOException{

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

    private static void keyExchange() throws IOException, InterruptedException, ConnectException {
        RSA rsa = new RSA();
        publicKey = rsa.getPublicKey();
        privateKey = rsa.getPrivateKey();
        r = rsa.getR();
        System.out.println("pb = " + publicKey + "pv = " + privateKey + " r = " + r);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://"+host+":"+port))
                .header("X-HTTP-Method-Override", "KEY")
                .POST(HttpRequest.BodyPublishers.ofString("publicKey="+publicKey+"&rKey="+r))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();

        System.out.println("Status code: " + response.statusCode());
//        System.out.println(response.body());
        String[] params = getParams(new StringBuilder(response.body().toString()), new String[]{"publicKey", "rKey", "id"});
        publicKeyEncrypt = Long.decode(params[0]);
        rEncrypt = Long.decode(params[1]);
        id = Long.decode(params[2]);
    }



    public static void start() throws IOException, InterruptedException, ConnectException {
        methodsMap.put("GET", new MethodGetHandler());
        methodsMap.put("PUT", new MethodPutHandler());
        methodsMap.put("POST", new MethodPostHandler());
        methodsMap.put("DELETE", new MethodDeleteHandler());
        methodsMap.put("MOVE", new MethodMoveHandler());
        methodsMap.put("COPY", new MethodCopyHandler());

//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter host");
//        host = scanner.nextLine();
//        System.out.println("Enter port number");
//        port = scanner.nextInt();
//        scanner.nextLine();
        keyExchange();
        System.out.println("Write: GET / PUT / POST / DELETE / MOVE / COPY / EXIT");
        String req = "";
        String[] splReq = {"", ""};

        while (!splReq[0].equals("EXIT")){
            try {
                req = in.nextLine();

                splReq = req.split(" ");
                methodsMap.get(splReq[0]).getObject().doMethod(host, port, id, privateKey, r, splReq[1]);
            } catch (FileNotFoundException | NoSuchElementException e){

            }
        }
    }
}


