package com.company.methods;

import com.company.RSA;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MethodKey extends Method{
    @Override
    public void doMethod(String host, int port, long id, long privateKey, long rKey, String filename) throws IOException, InterruptedException, ConnectException {

    }

    @Override
    public void doMethodForServer(HttpExchange exchange, String filename, HashMap<String, String> keyMap, long publicKey, long rKey) throws IOException {
        int status = 200;
        byte[] response = new byte[0];
        try {
            long id = generateUserID(keyMap);
            String[] params = getParams(exchange.getRequestBody(), new String[]{"publicKey", "rKey"});
            keyMap.put(Long.toString(id), "publicKey="+params[0]+"&rKey="+params[1]);
            response = ("publicKey="+publicKey + "&rKey=" + rKey + "&id=" + id).getBytes();
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
