/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionServer implements SocketAcceptor {
    public static final int SLEEP_DELAY = 25;
    private ServerSocket server;
    private Thread listener;
    private volatile boolean isRunning;
    private final String name;
    private int count = 0;
    private int port;
    private SocketAcceptor acceptor = this;

    public ConnectionServer(int port, String name) {
        isRunning = true;
        this.name = name;
        this.port = port;
        listener = new Thread(new SocketListener(), name);
    }

    public void start() throws IOException {
        server = new ServerSocket(port);
        listener.start();
        pause();
    }

    public void acceptConnection(Socket s) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setSocketAcceptor(SocketAcceptor acceptor) {
        if (acceptor == null) {
            throw new IllegalArgumentException();
        }
        this.acceptor = acceptor;
    }

    public int getPort() {
        if (server == null) {
            return port;
        }
        return server.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return server.getInetAddress();
    }

    public void shutdown() throws IOException {
        isRunning = false;
        listener.interrupt();
        if (server != null) {
            server.close();
        }
        pause();
    }

    private void pause() {
        try {
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private class SocketListener implements Runnable {
        public IOException caught = null;

        public void run() {
            try {
                runIO();
            } catch (IOException e) {
                caught = e;
                if (toRuntime(e)) {
                    throw new RuntimeException(e);
                }
            }
        }

        protected void runIO() throws IOException {
            while (isRunning()) {
                String nextName = name + "[" + count++ + "]";
                Socket socket;
                socket = server.accept();
                ConnectionAcceptor acceptor = new ConnectionAcceptor(socket);
                execute(acceptor, nextName);
            }
        }

        /* override with a threadpool if needed */
        protected void execute(Runnable target, String nextName) {
            new Thread(target, nextName).start();
        }

        protected boolean toRuntime(IOException e) {
            String msg = e.getMessage();
            return !("Socket closed".equals(msg) //
            || "Socket is closed".equals(msg));
        }
    }

    private class ConnectionAcceptor implements Runnable {
        private Socket socket;

        public ConnectionAcceptor(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                acceptor.acceptConnection(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e1) { //
                }
            }
        }
    }
}

interface SocketAcceptor {
    void acceptConnection(Socket s) throws IOException;
}
