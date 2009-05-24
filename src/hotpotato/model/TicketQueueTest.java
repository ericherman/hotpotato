/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.model;

import junit.framework.*;

public class TicketQueueTest extends TestCase {

    public void testBasicIO() {
        TicketQueue theQueue = new TicketQueue();

        assertEquals(false, theQueue.hasItems());

        theQueue.add(new Ticket("1", null));
        assertEquals(true, theQueue.hasItems());

        theQueue.add(new Ticket("2", null));
        assertEquals(true, theQueue.hasItems());

        theQueue.add(new Ticket("3", null));
        assertEquals(true, theQueue.hasItems());

        assertEquals("1", theQueue.get().getId());
        assertEquals(true, theQueue.hasItems());

        assertEquals("2", theQueue.get().getId());
        assertEquals(true, theQueue.hasItems());

        assertEquals("3", theQueue.get().getId());
        assertEquals(false, theQueue.hasItems());
    }

    public void testLookUp() {
        TicketQueue theQueue = new TicketQueue();

        theQueue.add(new Ticket("1", null));
        theQueue.add(new Ticket("2", null));
        theQueue.add(new Ticket("three", null));

        assertEquals("2", theQueue.get("2").getId());

    }

    public void testHasItem() {
        TicketQueue theQueue = new TicketQueue();
        theQueue.add(new Ticket("First", null));

        assertTrue(theQueue.hasItem("First"));
        assertFalse(theQueue.hasItem("bogus"));
    }
}
