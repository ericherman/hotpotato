/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.testsupport.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class ObjectRecieverTest extends TestCase {
    private ConnectionServer loopback;
    private InetAddress localhost;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
    }

    protected void tearDown() throws Exception {
        if (loopback != null)
            loopback.shutdown();
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionServer {
            Object obj;
            public Server() {
                super(0, "testWriteObject");
            }
            protected void acceptConnection(Socket s) throws IOException {
                obj = new ObjectReceiver(s).receive();
            }
        }

        Server server = new Server();
        server.start();

        loopback = server;
        ObjectOutputStream oos = getOutputStream(new Socket(localhost, server
                .getPort()));
        oos.writeObject("FOO");

        Thread.sleep(ConnectionServer.SLEEP_DELAY);
        assertEquals("FOO", server.obj);
    }

    private ObjectOutputStream getOutputStream(Socket s) throws IOException {
        return new ObjectOutputStream(s.getOutputStream());
    }

    public void testWriteAndRead() throws Exception {
        loopback = new LoopbackServer();
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
