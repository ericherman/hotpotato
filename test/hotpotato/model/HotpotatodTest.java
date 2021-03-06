/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class HotpotatodTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        Hotpotatod alices = new Hotpotatod();
        assertEquals(0, alices.ticketWheel.size());

        Callable<String> foo = new ReturnStringOrder("foo");
        String orderNumber = alices.takeOrder("bob", foo);

        assertEquals("bob1", orderNumber);
        assertEquals(foo, alices.ticketWheel.get().getOrder());
    }

    public void testGetNextOrder() {
        Hotpotatod alices = new Hotpotatod();
        assertNull(alices.getNextTicket());
        Ticket foo = new Ticket("", 1, 0, null);
        alices.ticketWheel.add(foo);

        assertEquals(foo, alices.getNextTicket());
        assertNull(alices.getNextTicket());
    }

    public void testReturnCompletedOrder() throws Exception {
        Hotpotatod alices = new Hotpotatod();
        Ticket foo = new Ticket("", 1, 0, null);
        alices.returnResult(foo, "foo");
        assertEquals(1, alices.counterTop.size());
    }

    public void testPickUpOrder() {
        Hotpotatod alices = new Hotpotatod();
        alices.counterTop.put("1", "foo");

        assertEquals(null, alices.pickUpOrder("bogus"));
        assertEquals("foo", alices.pickUpOrder("1"));
        assertEquals(null, alices.pickUpOrder("1"));
    }

    public void testGetTicket() {
        Hotpotatod alices = new Hotpotatod();
        assertNull(alices.getNextTicket());
        Ticket foo = new Ticket("x", 100, 0, null);
        alices.ticketWheel.add(foo);
        Ticket bar = new Ticket("x", 200, 0, null);
        alices.ticketWheel.add(bar);

        assertEquals(bar, alices.getTicket("x200"));
        assertEquals(foo, alices.getNextTicket());
        assertNull(alices.getNextTicket());
    }
}
