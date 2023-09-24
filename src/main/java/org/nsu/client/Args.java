package org.nsu.client;

import java.net.InetAddress;
import java.nio.file.Path;

public record Args(Path path, InetAddress serverAddress, int port, double speed) { }
