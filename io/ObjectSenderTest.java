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

public class ObjectSenderTest extends TestCase {
    private ConnectionStation loopback;
    private InetAddress localhost;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
    }

    protected void tearDown() throws Exception {
        if (loopback != null)
            loopback.shutdown();
    }

    private ObjectInputStream getObjectInputStream(Socket s)
        throws IOException {
        InputStream in = s.getInputStream();
        return new ObjectInputStream(in);
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionStation {
            Object obj;
            public Server() throws IOException {
                super(0, "testWriteObject");
            }
            protected void acceptConnection(Socket s) throws IOException {
                try {
                    obj = getObjectInputStream(s).readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Server server = new Server();
        server.start();
        Threads.pause(3000);
        loopback = server;
        ObjectSender sender =
            new ObjectSender(new Socket(localhost, server.getPort()));
        sender.send("FOO");
        Threads.pause();
        assertEquals("FOO", server.obj);
    }

    public void testWriteAndRead() throws Exception {
        loopback = new LoopbackServer(0);
        loopback.start();
        Object obj1 = "";
        Object obj2 = "";

        Socket s = new Socket(localhost, loopback.getPort());
        ObjectSender sender = new ObjectSender(s);
        sender.send(null);
        sender.send("bar");

        ObjectInputStream ois = getObjectInputStream(s);
        obj1 = ois.readObject();
        obj2 = ois.readObject();
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
                try {
                    item = (Order) getObjectInputStream(s).readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Server server = new Server();
        server.start();
        loopback = server;

        Order item = new NamedOrder("foo");

        ObjectSender sender =
            new ObjectSender(new Socket(localhost, server.getPort()));
        sender.send(item);
        Threads.pause(10000);
        assertEquals(item, server.item);

        sender = new ObjectSender(new Socket(localhost, server.getPort()));
        sender.send(null);
        Threads.pause();
        assertEquals(null, server.classDef);
        assertEquals(null, server.item);
    }
}
