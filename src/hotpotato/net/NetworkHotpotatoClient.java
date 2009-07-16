/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.HotpotatoClient;
import hotpotato.Request;
import hotpotato.io.ObjectReceiver;
import hotpotato.io.ObjectSender;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkHotpotatoClient implements HotpotatoClient, Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress serverAddress;
    private int port;
    private boolean sandbox;

    public NetworkHotpotatoClient(InetAddress address, int orderPort) {
        this(address, orderPort, true);
    }

    public NetworkHotpotatoClient(InetAddress address, int orderPort,
            boolean sandbox) {
        this.serverAddress = address;
        this.port = orderPort;
        this.sandbox = sandbox;
    }

    public Serializable send(Request request) throws IOException {
        Socket s = new Socket(serverAddress, port);

        new ObjectSender(s).send(request);
        Serializable obj = new ObjectReceiver(s, sandbox).receive();

        s.close();
        return obj;
    }

}
