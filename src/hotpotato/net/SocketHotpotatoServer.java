/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.io.*;
import hotpotato.model.*;

import java.io.*;
import java.net.*;

public class SocketHotpotatoServer extends ConnectionServer {
    private HotpotatoServer restaurant;

    public SocketHotpotatoServer(int port) {
        this(port, new Hotpotatod());
    }

    public SocketHotpotatoServer(int port, HotpotatoServer restaurant) {
        super(port, "Restaurant");
        this.restaurant = restaurant;
    }

    protected void acceptConnection(Socket s) throws IOException {
        ObjectReceiver receiver = new ObjectReceiver(s);
        Serializable request = receiver.receive();
        Serializable reply;
        try {
            reply = ((Request) request).exec(restaurant);
        } catch (Exception e) {
            reply = e.toString();
        }
        new ObjectSender(s).send(reply);
    }
}