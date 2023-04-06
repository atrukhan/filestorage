package com.company;

import com.company.RSA;
import com.company.methodsHandler.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Server {



    private static String IP_ADDRESS = "localhost";
    private static String dir = Paths.get("").toAbsolutePath().toString() + "\\filesstorage\\";
    private static int status = 200;
    private static byte[] response;
    private static HashMap<String, String> publicKeyMap = new HashMap<String, String>();
    private static HashMap<String, MethodsHandler> methodsMap = new HashMap<String, MethodsHandler>();
    private static long publicKey = 0;
    private static long privateKey = 0;
    private static long r = 0;

    static {
        try {

            IP_ADDRESS = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

//    private static long generateUserID(){
//        long id = 0;
//        Random random = new Random();
//        id = random.nextInt(100000);
//        if(publicKeyMap.get(Long.toString(id)) != null){
//            id = generateUserID();
//        }
//        return id;
//    }


    public static void start(){
        methodsMap.put("GET", new MethodGetHandler());
        methodsMap.put("PUT", new MethodPutHandler());
        methodsMap.put("POST", new MethodPostHandler());
        methodsMap.put("DELETE", new MethodDeleteHandler());
        methodsMap.put("MOVE", new MethodMoveHandler());
        methodsMap.put("COPY", new MethodCopyHandler());
        methodsMap.put("KEY", new MethodKeyHandler());
        boolean flag = false;
        while (!flag){
            try {
                System.out.println("Enter port number");
                Scanner in = new Scanner(System.in);
                HttpServer server = HttpServer.create(new InetSocketAddress(in.nextInt()), 5);
                flag = true;
                System.out.println(IP_ADDRESS + ":" + server.getAddress().getPort());
                RSA rsa = new RSA();
                publicKey = rsa.getPublicKey();
                privateKey = rsa.getPrivateKey();
                r = rsa.getR();
                server.createContext("/", new MyHandler());

                server.setExecutor(null); // creates a default executor
                server.start();

            }catch (InputMismatchException e){
                System.out.println("Wrong input");
            }catch (BindException e){
                System.out.println("Port is already in use");
            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }

    private static class MyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            status = 200;
            response = new byte[0];
            String requestURI = exchange.getRequestURI().toString();

            System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol());
            Headers headers = exchange.getRequestHeaders();
            String method = exchange.getRequestMethod();
            for (String key : headers.keySet()) {
                System.out.println(key + " : " + headers.get(key));
                if (key.equals("X-http-method-override"))
                    method = headers.get(key).get(0);
            }

//            String filename = requestURI;
            String filename = requestURI.replaceFirst("/", ""); //requestURI.substring(.firstIndexOf('/') + 1));

            methodsMap.get(method).getObject().doMethodForServer(exchange, filename, publicKeyMap, publicKey, r);
            if (!methodsMap.containsKey(method)) {
                status = 405;
                exchange.sendResponseHeaders(status, response.length); //требуется для исп. след функции getResponseBody
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
        }

        private static String[] getParams(InputStream body, String[] params) throws IOException{
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
    }
}