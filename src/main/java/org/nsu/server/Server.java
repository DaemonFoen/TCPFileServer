package org.nsu.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Server {

    private final int port;
    private static final String UPLOAD_DIR = "uploads";
    private final List<ConnectionHandler> clients;

    public static void main(String[] args){
        Server server = new Server(ServerCLI.parse(args));
        server.start();
    }

    public Server(int port) {
        clients = new ArrayList<>();
        this.port = port;
    }

    public void start() {
        Thread thread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.printf("server port - %d%n", port);
                Socket clientSocket;
                while (true){
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("User connected : " + clientSocket.getInetAddress());
                    ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket,clients.size()+1);
                    clients.add(connectionHandler);
                    Thread clientThread = new Thread(connectionHandler);
                    clientThread.start();
                }
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }

    private class ConnectionHandler implements Runnable {
        private final Socket clientSocket;
        private final int number;
        public ConnectionHandler(Socket clientSocket, int number) {
            this.clientSocket = clientSocket;
            this.number = number;
        }

        @Override
        public void run() {
            try (
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    if (!uploadDir.mkdirs()) {
                        throw new RuntimeException("Can't create upload folder");
                    }
                }
                File file = new File(uploadDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);

                byte[] buffer = new byte[10000];
                long startTime = System.currentTimeMillis();
                long totalKBytesRead = 0;
                double averageTransferSpeed = 0;
                int countTS = 0;
                int bytesRead;
                long elapsedTime = 1;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalKBytesRead += bytesRead;
                    elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= 3000) {
                        double transferSpeed = (totalKBytesRead / (1024.0 * 1024.0)) / (elapsedTime / 1000.0);
                        averageTransferSpeed += transferSpeed;
                        countTS++;
                        System.out.printf("User %d Speed: %.2f MB/s, Average speed: %.2f MB/s%n", number,transferSpeed,averageTransferSpeed/countTS);
                        startTime = System.currentTimeMillis();
                        totalKBytesRead = 0;
                    }
                }
                double transferSpeed = (totalKBytesRead / (1024.0 * 1024.0)) / (elapsedTime / 1000.0);
                averageTransferSpeed += transferSpeed;
                countTS++;
                System.out.printf("User %d Speed: %.2f MB/s, Average speed: %.2f MB/s%n", number,transferSpeed,averageTransferSpeed/countTS);
                fos.close();
                dos.writeBoolean(file.length() == fileSize);
                System.out.printf("User %d close connection %n", number);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            finally {

                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.error(e);
                }
                clients.remove(this);
            }
        }
    }
}