/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class ObjectSenderTest extends TestCase {
    private ConnectionServer loopback;
    private InetAddress localhost;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
    }

    protected void tearDown() throws Exception {
        if (loopback != null)
            loopback.shutdown();
    }

    private ObjectInputStream getObjectInputStream(Socket s) throws IOException {
        InputStream in = s.getInputStream();
        return new ObjectInputStream(in);
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionServer {
            Object obj;
            public Server() {
                super(0, "testWriteObject");
            }
            protected void acceptConnection(Socket s) throws IOException {
                try {
                    ObjectInputStream ois = getObjectInputStream(s);
                    do {
                        obj = ois.readObject();
                    } while (obj instanceof ClassDefinition);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Server server = new Server();
        server.start();

        loopback = server;
        ObjectSender sender = new ObjectSender(new Socket(localhost, server
                .getPort()));
        sender.send("FOO");
        Thread.sleep(ConnectionServer.SLEEP_DELAY);
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
        do {
            obj1 = ois.readObject();
        } while (obj1 instanceof ClassDefinition);
        do {
            obj2 = ois.readObject();
        } while (obj2 instanceof ClassDefinition);
        s.close();

        assertEquals(null, obj1);
        assertEquals("bar", obj2);
    }

    public void testAssumptions() throws Exception {
        loopback = new LoopbackServer(0);
        loopback.start();

        Object obj1 = "Foo";
        Object[] arr = new Object[]{obj1, obj1};

        assertSame(arr[0], arr[1]);

        Socket s = new Socket(localhost, loopback.getPort());
        new ObjectSender(s).send(arr);
        arr = (Object[]) new ObjectReceiver(s).receive();

        assertSame(arr[0], arr[1]);
        assertNotSame(obj1, arr[0]);
    }
}
