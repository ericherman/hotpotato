/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.NullPrintStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.TestCase;

public class ObjectRecieverTest extends TestCase {
    private ConnectionServer loopback;
    private InetAddress localhost;
    private ByteArrayOutputStream baos;
    private PrintStream ps;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
    }

    protected void tearDown() throws Exception {
        if (loopback != null) {
            loopback.shutdown();
        }
        localhost = null;
        loopback = null;
        ps.close();
        ps = null;
        baos = null;
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionServer {
            Object obj;

            public Server(PrintStream ps) {
                super(0, "testWriteObject", ps);
            }

            public void acceptConnection(Socket s) throws IOException {
                obj = new ObjectReceiver(s).receive();
            }
        }

        Server server = new Server(ps);
        server.start();
        loopback = server;

        Socket s = new Socket(localhost, server.getPort());
        ObjectOutputStream oos = getOutputStream(s);
        oos.writeObject("FOO");

        Thread.sleep(ConnectionServer.SLEEP_DELAY);
        assertEquals("FOO", server.obj);
        s.close();
    }

    private ObjectOutputStream getOutputStream(Socket s) throws IOException {
        return new ObjectOutputStream(s.getOutputStream());
    }

    public void testWriteAndRead() throws Exception {
        loopback = new LoopbackServer(0, ps);
        loopback.start();
        Object obj1 = "";
        Object obj2 = "";

        Socket s = new Socket(localhost, loopback.getPort());
        ObjectOutputStream oos = getOutputStream(s);
        oos.writeObject(null);
        oos.writeObject("bar");

        ObjectReceiver receiver = new ObjectReceiver(s);
        obj1 = receiver.receive();
        obj2 = receiver.receive();
        s.close();

        assertEquals(null, obj1);
        assertEquals("bar", obj2);
    }
}
