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
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class MethodCopy extends Method{
    @Override
    public void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException {
        Scanner in = new Scanner(System.in);
        System.out.println("Input new path:");
        String newPath = in.nextLine();
        System.out.println("Input new filename:");
        String newFileName = in.nextLine();


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://"+host+":"+port+"/"+filename))
                .header("X-HTTP-Method-Override", "COPY")
                .POST(HttpRequest.BodyPublishers.ofString("newPath=" + newPath + "&newFilename=" + newFileName))
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
            String[] params = getParams(exchange.getRequestBody(), new String[]{"newPath", "newFilename"});
            Path path = Paths.get(getDir() + params[0] +"/"+ params[1]);
            if (Files.exists(path)){
                status = 400;
                exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("File with new name is already exists ")));
                throw new IOException();
            }
            if(!Files.exists(Paths.get(getDir() + filename))){
                status = 400;
                exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("There is no file with name: "+filename)));
                throw new IOException();
            }else {
                new File(getDir() + params[0]).mkdirs();

                Files.copy(Paths.get(getDir() + filename), path);
            }
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
