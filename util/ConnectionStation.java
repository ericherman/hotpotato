/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;
import java.net.*;

public abstract class ConnectionStation {
    private ServerSocket server;
    private Thread listener;
    private volatile boolean isRunning;
    private final String name;
    private int count = 0;

    public ConnectionStation(int port, String name) throws IOException {
        isRunning = true;
        server = new ServerSocket(port);
        this.name = name;
        listener = listenerThread();
    }

    public void start() {
        listener.start();
    }

    abstract protected void acceptConnection(Socket s) throws IOException;

    public int getPort() {
        return server.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return server.getInetAddress();
    }

    public void shutdown() throws IOException {
        isRunning = false;
        listener.interrupt();
        server.close();
        Threads.pause();
    }

    public boolean isRunning() {
        return isRunning;
    }

    private Thread listenerThread() {
        return new ListenerThread();
    }

    private class ListenerThread extends Thread {
        public ListenerThread() {
            super(name);
        }
        public void run() {
            try {
                while (true) {
                    Socket socket = server.accept();
                    new ConnectionThread(socket).start();
                }
            } catch (IOException e) {
                if (!socketClosed(e)) {
                    e.printStackTrace();
                }
            }
        }

        private boolean socketClosed(IOException e) {
            String msg = e.getMessage();
            return "Socket closed".equals(msg)
                || "Socket is closed".equals(msg);
        }
    }

    private class ConnectionThread extends Thread {
        private Socket socket;

        public ConnectionThread(Socket socket) {
            super(name + "[" + count++ +"]");
            this.socket = socket;
        }

        public void run() {
            try {
                acceptConnection(socket);
                socket.close();
            } catch (IOException e) {
                /*
                 * TODO Hmm, is this good enough? Maybe the 
                 * exception should be found and hurled on the
                 * main thread instead.
                 */
                throw new RuntimeException(e);
            }
        }
    }
}
