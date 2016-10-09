/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.net;

import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.io.ConnectionServer;
import hotpotato.io.ObjectReceiver;
import hotpotato.io.ObjectSender;
import hotpotato.model.Hotpotatod;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;

public class SocketHotpotatoServer extends ConnectionServer {
    private HotpotatoServer hotpotato;

    public SocketHotpotatoServer(int port) {
        this(port, System.out);
    }

    public SocketHotpotatoServer(int port, PrintStream out) {
        this(port, new Hotpotatod(), out);
    }

    public SocketHotpotatoServer(int port, HotpotatoServer hotpotato,
            PrintStream out) {
        super(port, "hotpotatod", out);
        this.hotpotato = hotpotato;
    }

    public void acceptConnection(Socket s) throws IOException {
        ObjectReceiver receiver = new ObjectReceiver(s);
        Serializable request = receiver.receive();
        Serializable reply;
        try {
            reply = ((Request) request).exec(hotpotato);
        } catch (Exception e) {
            reply = e.toString();
        }
        new ObjectSender(s).send(reply);
    }
}
