/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.io.ObjectReceiver;
import hotpotato.io.ObjectSender;
import hotpotato.util.NullPrintStream;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.TestCase;

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
        PrintStream devNull = new NullPrintStream();
        server = new SocketHotpotatoServer(0, devNull);
        server.start();

        Socket s = new Socket(InetAddress.getLocalHost(), server.getPort());
        ObjectSender sender = new ObjectSender(s);

        sender.send(new Foo());

        String reply = "" + new ObjectReceiver(s).receive();
        s.close();
        devNull.close();

        assertEquals("foo", reply);
    }
}
