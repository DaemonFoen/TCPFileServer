package org.nsu;


import org.nsu.server.ServerCLI;
import org.nsu.server.Server;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(ServerCLI.parse(args));
        server.start();
    }

}