package org.nsu.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.nsu.CLI;

public class ServerCLI extends CLI {

    public static Args parse(String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int port;
        double speed;
        if (cmd.hasOption("help")) {
            System.out.println(usage());
            System.exit(0);
        }
        if (cmd.hasOption("s")){
            speed = Long.parseLong(cmd.getOptionValue("s"));
            if (speed <= 0){
                throw new RuntimeException("Speed must be greater than zero");
            }
        }else{
            throw new RuntimeException("No required option - s");
        }
        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
            if (port <= 0) {
                throw new RuntimeException("Wrong port");
            }
        }else {
            throw new RuntimeException("No required option - p");
        }
        return new Args(port,speed);
    }
}