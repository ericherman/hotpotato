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

public class TicketWheelTest extends TestCase {
    private TicketWheel ticketWheel;

    protected void tearDown() throws Exception {
        if (ticketWheel != null) {
            ticketWheel.shutdown();
        }
    }

    public void testAcceptConnection() throws Exception {
        Queue queue = new SynchronizedQueue();
        ticketWheel = new TicketWheel(0, queue);
        ticketWheel.start();

        Order foo = new NamedOrder("foo");
        queue.add(foo);
        assertEquals(1, queue.size());

        Order pulled = (Order) new ObjectReceiver(ticketSocket()).receive();

        assertTrue(queue.isEmpty());

        assertEquals(foo, pulled);
    }

    private Socket ticketSocket() throws IOException, UnknownHostException {
        return new Socket(InetAddress.getLocalHost(), ticketWheel.getPort());
    }
}
