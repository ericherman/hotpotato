/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.model;

import junit.framework.TestCase;

public class TicketQueueTest extends TestCase {

    public void testBasicIO() {
        TicketQueue theQueue = new TicketQueue();

        assertEquals(false, theQueue.hasItems());

        theQueue.add(new Ticket("", 1, 0, null));
        assertEquals(true, theQueue.hasItems());

        theQueue.add(new Ticket("", 2, 0, null));
        assertEquals(true, theQueue.hasItems());

        theQueue.add(new Ticket("", 3, 0, null));
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

        theQueue.add(new Ticket("", 1, 0, null));
        theQueue.add(new Ticket("", 2, 0, null));
        theQueue.add(new Ticket("three", 3, 0, null));

        assertEquals("2", theQueue.get("2").getId());

    }

    public void testHasItem() {
        TicketQueue theQueue = new TicketQueue();
        theQueue.add(new Ticket("First", 1, 0, null));

        assertTrue(theQueue.hasItem("First1"));
        assertFalse(theQueue.hasItem("bogus2"));
    }
}
