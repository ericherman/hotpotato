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

public class CounterTopTest extends TestCase {
    final static int TOO_MANY = 100000;

    private CounterTop counterTop;

    protected void tearDown() throws Exception {
        if (counterTop != null)
            counterTop.shutdown();
        Thread.yield();
        System.gc();
    }

    public void testOrderTakerConstruction() throws Exception {
        counterTop = new CounterTop(9973, new HashMap());
        assertEquals("getPort", 9973, counterTop.getPort());
    }

    public void testOrderTakerRandomPort() throws Exception {
        counterTop = new CounterTop(0, new HashMap());
        assertTrue("getPort", counterTop.getPort() > 1024);
    }

    public void testAcceptConnection() throws Exception {
        Map map = new HashMap();
        counterTop = new CounterTop(0, map);
        counterTop.start();

        assertEquals(0, map.size());

        Order item = new NamedOrder("foo");
        Socket s = new Socket(InetAddress.getLocalHost(), counterTop.getPort());
        new ObjectSender(s).send(item);
        s.close();

        int i = 0;
        for (i = 0; map.size() == 0; i++) {
            assertTrue("never recieved work", i < TOO_MANY);
            Thread.yield();
        }
    }
}
