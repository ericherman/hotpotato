/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;
import hotpotato.util.*;

import java.net.*;

import junit.framework.*;

public class OrderTakerTest extends TestCase {
    final static int TOO_MANY = 100000;
    private OrderTaker orderTaker;

    protected void tearDown() throws Exception {
        if (orderTaker != null)
            orderTaker.shutdown();
        Thread.yield();
    }

    public void testAcceptConnection() throws Exception {
        SynchronizedQueue queue = new SynchronizedQueue();
        orderTaker = new OrderTaker(0, queue);
        orderTaker.start();

        assertEquals(0, queue.size());

        Order order = new NamedOrder("foo");
        Socket s = new Socket(InetAddress.getLocalHost(), orderTaker.getPort());
        new ObjectSender(s).send(order);
        new ObjectReceiver(s).receive();
        s.close();

        int i = 0;
        for (i = 0; queue.size() == 0; i++) {
            assertTrue("never recieved", i < TOO_MANY);
            Thread.yield();
        }

        assertEquals(order, queue.get());
    }
}
