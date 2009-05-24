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

import junit.framework.*;

public class SocketHotpotatoServerTest extends TestCase {

    private SocketHotpotatoServer server;

    protected void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        server = null;
    }

    static class Foo implements Request {
        private static final long serialVersionUID = 1L;
        public Serializable exec(HotpotatoServer restaurant) {
            return "foo";
        }
    }

    public void testAcceptConnection() throws Exception {
        server = new SocketHotpotatoServer(0);
        server.start();

        Socket s = new Socket(InetAddress.getLocalHost(), server.getPort());
        ObjectSender sender = new ObjectSender(s);

        sender.send(new Foo());

        String reply = "" + new ObjectReceiver(s).receive();
        s.close();

        assertEquals("foo", reply);
    }
}
