/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;

import java.net.*;
import java.util.*;

import junit.framework.*;

public class OrderPresenterTest extends TestCase {
    final static int TOO_MANY = 100000;
    private OrderPresenter presenter;

    protected void shutDown() throws Exception {
        if (presenter != null)
            presenter.shutdown();
        Thread.yield();
        System.gc();
    }

    public void testAcceptConnection() throws Exception {
        Map map = new HashMap();

        presenter = new OrderPresenter(0, map);
        presenter.start();

        Socket s = new Socket(InetAddress.getLocalHost(), presenter.getPort());
        new ObjectSender(s).send("foo");
        Order returned = (Order) new ObjectReceiver(s).receive();
        s.close();
        assertNull(returned);

        assertEquals(0, presenter.ordersDelivered());

        Order foo = new NamedOrder("foo");
        map.put("foo", foo);

        s = new Socket(InetAddress.getLocalHost(), presenter.getPort());
        new ObjectSender(s).send("foo");
        returned = (Order) new ObjectReceiver(s).receive();
        s.close();
        assertEquals(foo, returned);

        assertEquals(1, presenter.ordersDelivered());
        assertEquals(0, map.size());
    }
}
