package com.company;

import com.company.Server;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Server - 1  Client - 2");
        if(sc.nextInt() == 1){
            Server server = new Server();
            server.start();
        } else {
            Client client = new Client();
            client.start();
        }

    }
}
