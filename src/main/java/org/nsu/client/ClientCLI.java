package org.nsu.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.nsu.CLI;

public class ClientCLI extends CLI {

    static {
        options.addOption("i", "ip", true, "server IP");
        options.addOption("f", "file", true, "absolute path");
    }

    public static Args parse(String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Path path;
        InetAddress ip;
        int port;
        double speed;
        if (cmd.hasOption("help")) {
            System.out.println(usage());
            System.exit(0);
        }
        if (cmd.hasOption("f")) {
            path = Path.of(cmd.getOptionValue("f"));
        } else {
            throw new RuntimeException("No required option - f");
        }
        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
            if (port <= 0) {
                throw new RuntimeException("Wrong port");
            }
        } else {
            throw new RuntimeException("No required option - p");
        }
        if (cmd.hasOption("i")) {
            try {
                ip = InetAddress.getByName(cmd.getOptionValue("i"));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("No required option - i");
        }
        if (cmd.hasOption("s")){
            speed = Long.parseLong(cmd.getOptionValue("s"));
            if (speed <= 0){
                throw new RuntimeException("Speed must be greater than zero");
            }
        }else{
            throw new RuntimeException("No required option - s");
        }
        return new Args(path, ip, port,speed);
    }

}