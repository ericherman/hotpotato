/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;

public class ReverseStringServer extends ConnectionServer {

    public ReverseStringServer(int port) {
        super(port, "ReverseStringServer_" + port);
    }

    public ReverseStringServer(int port, PrintStream out) {
        super(port, "ReverseStringServer_" + port, out);
    }

    public void acceptConnection(Socket s) throws IOException {
        ObjectReceiver receiver = new ObjectReceiver(s);
        Serializable request = receiver.receive();
        StringBuffer buf = new StringBuffer();
        buf.append(request);
        Serializable reply = buf.reverse();
        new ObjectSender(s).send(reply);
    }
}
