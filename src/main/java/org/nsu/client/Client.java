package org.nsu.client;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.extern.log4j.Log4j2;
import org.nsu.DataUnits;

@Log4j2
public class Client {
    private final InetAddress serverIP;
    private final int serverPort;
    private final Path filePath;
    private final int FILE_NAME_SIZE = 4096;
    private final double speed;

    public static void main(String[] args) {
        Client client = new Client(ClientCLI.parse(args));
        client.start();
    }

    public Client(Args args){
        serverIP = args.serverAddress();
        serverPort = args.port();
        filePath = args.path();
        speed = args.speed();
    }


    public void start(){
        if (filePath.getFileName().toString().getBytes(StandardCharsets.UTF_8).length > FILE_NAME_SIZE){
            System.out.println("The length of the file name is more than 4068 bytes");
            System.exit(1);
        }
        if ((filePath.toFile().length() / (DataUnits.TB.getValue())) > 1){
            System.out.println("The file size is more than а TB");
            System.exit(1);
        }

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
                byte[] buffer = new byte[10000];
                int bytesRead;

                long startTime = System.currentTimeMillis();
                long elapsedTime;
                long bytesTotal = 0;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    bytesTotal += bytesRead;
                    if (DataUnits.convertToMbSec(bytesTotal,elapsedTime) > speed){
                        Thread.sleep(1);
                    }
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                socket.shutdownOutput();
                log.info("Connection end");
                boolean success = dis.readBoolean();
                if (success) {
                    System.out.println("Success");
                } else {
                    System.out.println("Fail");
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
}

