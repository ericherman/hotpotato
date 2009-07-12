/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class HotpotatodTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        Hotpotatod alices = new Hotpotatod();
        assertEquals(0, alices.ticketWheel.size());

        Callable<Serializable> foo = new ReturnStringOrder("foo");
        String orderNumber = alices.takeOrder("bob", foo);

        assertEquals("bob0", orderNumber);
        assertEquals(foo, alices.ticketWheel.get().getOrder());
    }

    public void testGetNextOrder() {
        Hotpotatod alices = new Hotpotatod();
        assertNull(alices.getNextTicket());
        Ticket foo = new Ticket("1", null);
        alices.ticketWheel.add(foo);

        assertEquals(foo, alices.getNextTicket());
        assertNull(alices.getNextTicket());
    }

    public void testReturnCompletedOrder() throws Exception {
        Hotpotatod alices = new Hotpotatod();
        alices.returnResult("1", "foo");
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
        Ticket foo = new Ticket("100", null);
        alices.ticketWheel.add(foo);
        Ticket bar = new Ticket("200", null);
        alices.ticketWheel.add(bar);

        assertEquals(bar, alices.getTicket("200"));
        assertEquals(foo, alices.getNextTicket());
        assertNull(alices.getNextTicket());
    }
}
