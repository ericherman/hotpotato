/**
 * Copyright (C) 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ConnectionServer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ResendServer extends ConnectionServer {
    public interface Sender {
        boolean send(String line);
    }

    private AtomicInteger counter;
    private PrintStream out;
    private Sender sender;

    public ResendServer(int port, Sender sender) {
        this(port, System.out, sender);
    }

    public ResendServer(int port, PrintStream out, Sender sender) {
        super(port, ResendServer.class.getSimpleName(), out);
        this.out = out;
        this.sender = sender;
        this.counter = new AtomicInteger(0);
    }

    public void acceptConnection(Socket s) throws IOException {
        while (isRunning()) {
            InputStream is = new BufferedInputStream(s.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            counter.incrementAndGet();
            out.println("received: '" + line + "'");
            if (sender.send(line)) {
                out.println("sent: '" + line + "'");
            }
        }
    }

    public int stringsRecieved() {
        return counter.intValue();
    }

}
