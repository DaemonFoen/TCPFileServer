package org.nsu.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ClientCLI {

    private static final Options options = new Options();

    static {
        options.addOption("i", "ip", true, "server IP");
        options.addOption("p", "port", true, "server port");
        options.addOption("f", "file", true, "absolute path");
        options.addOption("h", "help", false, "...");
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
        return new Args(path, ip, port);
    }

    public static String usage() {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter stringWriter = new StringWriter();
        formatter.printHelp(new PrintWriter(stringWriter), 250, "Client", null,
                options, formatter.getLeftPadding(), formatter.getDescPadding(), null, true);
        return stringWriter.toString();
    }
}