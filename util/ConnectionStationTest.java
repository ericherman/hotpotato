/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import hotpotato.io.*;

import java.net.*;

import junit.framework.*;

public class ConnectionStationTest extends TestCase {

    private ConnectionStation server;

    protected void setUp() throws Exception {
        Thread.sleep(0);
        server = new LoopbackServer(0);
        server.start();
        Thread.sleep(0);
    }

    protected void tearDown() throws Exception {
        server.shutdown();
        Thread.sleep(0);
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
}
