/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import hotpotato.model.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class ObjectRecieverTest extends TestCase {
    private ConnectionStation loopback;
    private InetAddress localhost;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
    }

    protected void tearDown() throws Exception {
        if (loopback != null)
            loopback.shutdown();
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionStation {
            Object obj;
            public Server() throws IOException {
                super(0, "testWriteObject");
            }
            protected void acceptConnection(Socket s) throws IOException {
                obj = new ObjectReceiver(s).receive();
            }
        }

        Server server = new Server();
        server.start();
        Threads.pause(3000);
        loopback = server;
        ObjectOutputStream oos =
            getOutputStream(new Socket(localhost, server.getPort()));
        oos.writeObject("FOO");
        Threads.pause();
        assertEquals("FOO", server.obj);
    }

    private ObjectOutputStream getOutputStream(Socket s) throws IOException {
        return new ObjectOutputStream(s.getOutputStream());
    }

    public void testWriteAndRead() throws Exception {
        loopback = new LoopbackServer(0);
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

    public void testWriteTicket() throws Exception {
        class Server extends ConnectionStation {
            ClassDefinition classDef;
            Order item;
            public Server() throws IOException {
                super(0, "testWriteTicket");
            }
            protected void acceptConnection(Socket s) throws IOException {
                item = (Order) new ObjectReceiver(s).receive();
            }
        }

        Server server = new Server();
        server.start();
        loopback = server;

        Order item = new NamedOrder("foo");

        ObjectOutputStream oos =
            getOutputStream(new Socket(localhost, server.getPort()));
        oos.writeObject(item);
        Threads.pause(10000);
        assertEquals(item, server.item);

        oos = getOutputStream(new Socket(localhost, server.getPort()));
        oos.writeObject(null);
        Threads.pause();
        assertEquals(null, server.classDef);
        assertEquals(null, server.item);
    }
}
