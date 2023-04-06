package com.company.methods;

import com.company.RSA;
import com.sun.net.httpserver.HttpExchange;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MethodDelete extends Method{
    @Override
    public void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://"+host+":"+port+"/"+filename))
                .DELETE()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpHeaders headers = response.headers();
        for (String key : headers.map().keySet()){
            if (key.equals("message"))
                System.out.println(key + " : " + headers.map().get(key));
        }

        System.out.println("Status code: " + response.statusCode());
        System.out.println(response.body());
    }

    @Override
    public void doMethodForServer(HttpExchange exchange, String filename, HashMap<String, String> keyMap, long publicKey, long rKey) throws IOException {
        int status = 200;
        byte[] response = new byte[0];
        try {
            Files.delete(Paths.get(getDir() + filename));
        }catch (FileNotFoundException | NoSuchFileException e) {
            status = 404;
            exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("There is no file with name: "+filename + " or the directory contain files")));
        } catch(IOException e) {
            status = 400;
            exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("There is no file with name: "+filename + " or the directory contain files")));
        }
        exchange.sendResponseHeaders(status, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
