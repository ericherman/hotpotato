/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import junit.framework.TestCase;

public class ConnectionServerTest extends TestCase {

    private ConnectionServer server;
    private Socket s1;
    private Socket s2;
    private ByteArrayOutputStream baos;
    private PrintStream ps;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ConnectionServerTest.class);
    }

    protected void setUp() throws Exception {
        setUpInner();
        server = new LoopbackServer(0, ps);
        server.start();
    }

    protected void tearDown() throws Exception {
        if (s1 != null) {
            s1.close();
        }
        if (s2 != null) {
            s2.close();
        }
        server.shutdown();

        ps.close();
        ps = null;
        baos = null;
        s1 = null;
        s2 = null;
        server = null;
    }

    public void testSocketLoopback() throws Exception {
        s1 = new Socket("localhost", server.getPort());

        new ObjectSender(s1).send("Hello, World!");

        assertEquals("Hello, World!", new ObjectReceiver(s1).receive());
    }

    public void testTwoSockets() throws Exception {
        s1 = new Socket("localhost", server.getPort());
        ObjectSender sender1 = new ObjectSender(s1);
        ObjectReceiver receiver1 = new ObjectReceiver(s1);

        s2 = new Socket("localhost", server.getPort());
        ObjectSender sender2 = new ObjectSender(s2);
        ObjectReceiver receiver2 = new ObjectReceiver(s2);

        sender1.send("Nevada Test Site Potatoe");
        sender2.send("Idaho Potato");

        assertEquals("Idaho Potato", receiver2.receive());
        assertEquals("Nevada Test Site Potatoe", receiver1.receive());
    }

    public void testPort() throws Exception {
        tearDown();
        setUpInner();
        server = new LoopbackServer(9973, ps);
        server.start();
        assertEquals("getPort", 9973, server.getPort());
    }

    public void testRandomPort() throws Exception {
        tearDown();
        setUpInner();
        server = new LoopbackServer(0, ps);
        server.start();
        assertTrue("getPort", server.getPort() > 1024);
    }

    private void setUpInner() {
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
    }
}
