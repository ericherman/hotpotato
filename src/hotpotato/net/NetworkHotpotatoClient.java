/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.io.*;

import java.io.*;
import java.net.*;

public class NetworkHotpotatoClient implements HotpotatoClient {
    private InetAddress serverAddress;
    private int port;

    public NetworkHotpotatoClient(InetAddress address, int orderPort) {
        this.serverAddress = address;
        this.port = orderPort;
    }

    public Serializable send(Request request) throws IOException {
        Socket s = new Socket(serverAddress, port);

        new ObjectSender(s).send(request);
        Serializable obj = new ObjectReceiver(s).receive();

        s.close();
        return obj;
    }

}
