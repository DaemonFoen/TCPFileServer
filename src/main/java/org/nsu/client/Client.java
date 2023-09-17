package org.nsu.client;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import lombok.extern.log4j.Log4j2;
import org.nsu.server.ClientCLI;

@Log4j2
public class Client {

    private final InetAddress serverIP;
    private final int serverPort;
    private final Path filePath;

    public static void main(String[] args) {
        Client client = new Client(ClientCLI.parse(args));
        client.start();
    }

    public Client(Args args){
        serverIP = args.serverAddress();
        serverPort = args.port();
        filePath = args.path();
    }


    public void start(){
        Thread thread = new Thread(() -> {
            try (
                    Socket socket = new Socket(serverIP, serverPort);
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
            ) {

                File file = new File(String.valueOf(filePath));
                if (!file.exists()) {
                    log.error("File not found.");
                    return;
                }

                dos.writeUTF(String.valueOf(filePath.getFileName()));
                dos.writeLong(file.length());

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                socket.shutdownOutput();
                log.info("Передача с клиента закончена");
                boolean success = dis.readBoolean();
                if (success) {
                    System.out.println("Файл передан успешно");
                } else {
                    System.out.println("Ошибка в передаче файла");
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        thread.start();
    }
}

