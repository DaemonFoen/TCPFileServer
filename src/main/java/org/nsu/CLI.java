package org.nsu;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public abstract class CLI {
    protected static final Options options = new Options();

    static {
        options.addOption("p", "port", true, "server port");
        options.addOption("h", "help", false, "...");
        options.addOption("s", "speed", true, "connection speed in MB/sec");

    }

    public static String usage() {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter stringWriter = new StringWriter();
        formatter.printHelp(new PrintWriter(stringWriter), 250, "Server", null,
                options, formatter.getLeftPadding(), formatter.getDescPadding(), null, true);
        return stringWriter.toString();
    }
}
