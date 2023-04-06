package com.company.methods;

import com.company.RSA;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class MethodPost extends Method{
    @Override
    public void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException {
        Scanner in = new Scanner(System.in);
        System.out.println("Input message:");
        String mes = in.nextLine();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://"+host+":"+port+"/"+filename))
                .POST(HttpRequest.BodyPublishers.ofString("content="+mes))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();
        for (String key : headers.map().keySet()){
            if (key.equals("message"))
                System.out.println(key + " : " + headers.map().get(key));
        }

        System.out.println("Status code: " + response.statusCode());
        System.out.println(response.body());
        in.close();
    }

    @Override
    public void doMethodForServer(HttpExchange exchange, String filename, HashMap<String, String> keyMap, long publicKey, long rKey) throws IOException {
        int status = 200;
        byte[] response = new byte[0];
        try {
            Path path = Paths.get(getDir() + filename);
            String param = "";
            if(filename.contains("/")){
                param = filename.substring(0, filename.lastIndexOf("/"));
            }
            if (!Files.exists(path)){
                new File(getDir() + param).mkdirs();
                Files.createFile(path);
            }
            Files.write(path, getParams(exchange.getRequestBody(), new String[]{"content"})[0].getBytes(), StandardOpenOption.APPEND);
        }catch (FileNotFoundException | NoSuchFileException e) {
            status = 404;
        } catch(IOException e) {
            status = 400;
        }
        exchange.sendResponseHeaders(status, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
