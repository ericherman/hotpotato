/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.model;

import hotpotato.*;
import hotpotato.testsupport.ReturnStringOrder;
import junit.framework.*;

public class AlicesRestaurantTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        AlicesRestaurant alices = new AlicesRestaurant();
        assertEquals(0, alices.ticketWheel.size());

        Order foo = new ReturnStringOrder("foo");
        String orderNumber = alices.takeOrder("bob", foo);

        assertEquals("bob0", orderNumber);
        assertEquals(foo, alices.ticketWheel.get().getOrder());
    }

    public void testGetNextOrder() {
        AlicesRestaurant alices = new AlicesRestaurant();
        assertNull(alices.getNextTicket());
        Ticket foo = new Ticket("1", null);
        alices.ticketWheel.add(foo);

        assertEquals(foo, alices.getNextTicket());
        assertNull(alices.getNextTicket());
    }

    public void testReturnCompletedOrder() {
        AlicesRestaurant alices = new AlicesRestaurant();
        Order foo = new ReturnStringOrder("foo");
        alices.returnResult("1", foo);
        assertEquals(1, alices.counterTop.size());
    }

    public void testPickUpOrder() {
        AlicesRestaurant alices = new AlicesRestaurant();
        alices.counterTop.put("1", "foo");

        assertEquals(null, alices.pickUpOrder("bogus"));
        assertEquals("foo", alices.pickUpOrder("1"));
        assertEquals(null, alices.pickUpOrder("1"));
    }

    public void testGetTicket() {
        AlicesRestaurant alices = new AlicesRestaurant();
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
