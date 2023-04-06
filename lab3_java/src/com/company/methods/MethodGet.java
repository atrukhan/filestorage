package com.company.methods;

import com.company.RSA;
import com.company.methods.Method;
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

public class MethodGet extends Method {

    @Override
    public void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException{
            //RSA rsa = new RSA(privateKey, publicKey, r);
            //filename = String.valueOf(rsa.rsaEncrypt(filename.getBytes(), publicKeyEncrypt, rEncrypt));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://"+host+":"+port+"/"+filename))
                    .header("X-HTTP-Method-Override", "GET")
                    .POST(HttpRequest.BodyPublishers.ofString("id="+id))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            HttpHeaders headers = response.headers();
            for (String key : headers.map().keySet()){
                if (key.equals("message"))
                    System.out.println(key + " : " + headers.map().get(key));
            }

            System.out.println("Status code: " + response.statusCode());
//            System.out.println(response.body());

            byte[] b = response.body();
            for (int i = 0; i < b.length; i++){
                System.out.print(b[i]+" ");
            }
            System.out.println();
            byte[] byt = RSA.rsaDecrypt(b, privateKey, rKey);
            for (int i = 0; i < byt.length; i++){
                System.out.print(byt[i]+" ");
            }
            System.out.println();
            String res = new String(byt);

            System.out.println(res);
    }

    @Override
    public void doMethodForServer(HttpExchange exchange, String filename, HashMap<String, String> keyMap, long publicKey, long rKey) throws IOException {
        int status = 200;
        byte[] response = new byte[0];
        try {
            String[] params = getParams(exchange.getRequestBody(), new String[]{"id"});
            String[] keyParams = getParams(new StringBuilder(keyMap.get(params[0])), new String[]{"publicKey", "rKey"});
            response = RSA.rsaEncrypt(Files.readAllBytes(Paths.get(getDir() + filename)), Long.decode(keyParams[0]), Long.decode(keyParams[1]));
            System.out.println(new String(response));
        }catch (FileNotFoundException | NoSuchFileException e) {
            status = 404;
            exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("There is no file with name: " + filename)));
        } catch(IOException e) {
            status = 400;
            exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("There is no file with name: " + filename)));
        }
        exchange.sendResponseHeaders(status, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

}
