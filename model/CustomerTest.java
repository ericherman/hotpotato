/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class CustomerTest extends TestCase {
    private Customer customer;
    private ConnectionStation server;

    protected void tearDown() throws Exception {
        if (server != null)
            server.shutdown();
        Threads.pause();
    }

    public void testRecieve() throws Exception {
        class SimpleServer extends ConnectionStation {
            Serializable thing = null;
            public SimpleServer() throws IOException {
                super(0, "SimpleServer (testRecieve)");
            }
            protected void acceptConnection(Socket s) throws IOException {
                new ObjectReceiver(s).receive();
                new ObjectSender(s).send(thing);
            }
            public void set(Serializable obj) {
                thing = obj;
            }
        }
        server = new SimpleServer();
        server.start();
        customer = new Customer();
        customer.setResturant(InetAddress.getLocalHost(), -1, server.getPort());
        Threads.pause();

        assertNull(customer.pickupOrder());
        Order foo = new NamedOrder("foo");
        ((SimpleServer) server).set(foo);
        assertEquals(foo, customer.pickupOrder());
    }

    public void testSimpleSubmitToWaiter() throws Exception {
        class SimpleServer extends ConnectionStation {
            private Object obj1 = null;
            public SimpleServer() throws IOException {
                super(0, "SimpleServer (testSubmitToWaiter)");
            }
            protected void acceptConnection(Socket s) throws IOException {
                obj1 = new ObjectReceiver(s).receive();
                new ObjectSender(s).send("orderNumber");
            }
            public Object get() {
                return obj1;
            }
        }

        customer = new Customer();
        server = new SimpleServer();
        server.start();
        Threads.pause();

        Order foo = new NamedOrder("foo");
        customer.setResturant(InetAddress.getLocalHost(), server.getPort(), -1);
        customer.placeOrder(foo);

        Threads.pause();
        assertEquals("orderNumber", customer.getOrderNumber());
        assertEquals(foo, ((SimpleServer) server).get());
    }

    public void testPlaceOrder() throws Exception {
        class MyConnStation extends ConnectionStation {
            Exception thrown = null;
            public MyConnStation() throws IOException {
                super(0, "testSendValet");
            }
            protected void acceptConnection(Socket s) {
                try {
                    Object o = new ObjectReceiver(s).receive();
                    new ObjectSender(s).send("1");
                    NamedOrder item = (NamedOrder) o;
                    assertNotNull(item);
                    assertEquals("foo", item.getName());
                } catch (Exception e) {
                    thrown = e;
                }
            }
            public Exception getThrown() {
                return thrown;
            }
        }

        MyConnStation conn = new MyConnStation();
        conn.start();
        try {
            assertTrue(conn.isRunning());
            Customer classSender = new Customer();
            classSender.setResturant(
                InetAddress.getLocalHost(),
                conn.getPort(),
                0);

            classSender.placeOrder(new NamedOrder("foo"));
        } finally {
            conn.shutdown();
        }
        Exception e = conn.getThrown();
        if (e != null)
            throw e;
    }
}
