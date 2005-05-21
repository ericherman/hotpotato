/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import hotpotato.testsupport.*;

import java.net.*;

import junit.framework.*;

public class ConnectionServerTest extends TestCase {

    private ConnectionServer server;

    protected void setUp() throws Exception {
        server = new LoopbackServer(0);
        server.start();
    }

    protected void tearDown() throws Exception {
        server.shutdown();
    }

    public void testSocketLoopback() throws Exception {
        Socket s = new Socket("localhost", server.getPort());

        new ObjectSender(s).send("Hello, World!");

        assertEquals("Hello, World!", new ObjectReceiver(s).receive());
    }

    public void testTwoSockets() throws Exception {
        Socket socket = new Socket("localhost", server.getPort());
        ObjectSender sender1 = new ObjectSender(socket);
        ObjectReceiver receiver1 = new ObjectReceiver(socket);

        Socket socket2 = new Socket("localhost", server.getPort());
        ObjectSender sender2 = new ObjectSender(socket2);
        ObjectReceiver receiver2 = new ObjectReceiver(socket2);

        sender1.send("Nevada Test Site Potatoe");
        sender2.send("Idaho Potato");

        assertEquals("Idaho Potato", receiver2.receive());
        assertEquals("Nevada Test Site Potatoe", receiver1.receive());
    }

    public void testPort() throws Exception {
        tearDown();
        server = new LoopbackServer(9973);
        server.start();
        assertEquals("getPort", 9973, server.getPort());
    }

    public void testRandomPort() throws Exception {
        tearDown();
        server = new LoopbackServer(0);
        server.start();
        assertTrue("getPort", server.getPort() > 1024);
    }
}
