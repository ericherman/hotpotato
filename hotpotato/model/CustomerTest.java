/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.model;

import hotpotato.*;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class CustomerTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        class FauxRestaurant extends Restaurant.Stub {
            Order order = null;
            public String takeOrder(String id, Order in) {
                order = in;
                return id + "7";
            }
        }
        FauxRestaurant alices = new FauxRestaurant();
        Customer bob = new Customer(new LocalRestaurantClient(alices));

        Order foo = new ReturnStringOrder("foo");
        bob.placeOrder("bob", foo);
        assertEquals(foo, alices.order);
    }

    public void testPickupOrder() throws Exception {
        class FauxRestaurant extends Restaurant.Stub {
            String id = null;
            public Serializable pickUpOrder(String in) {
                this.id = in;
                return (id == "bar") ? "bar" : null;
            }
        }
        FauxRestaurant alices = new FauxRestaurant();
        Customer bob = new Customer(new LocalRestaurantClient(alices));

        assertNull(bob.pickupOrder("foo"));
        assertEquals("foo", alices.id);

        assertEquals("bar", bob.pickupOrder("bar"));
        assertEquals("bar", alices.id);
    }

    public void testCancelOrder() throws Exception {
        class FauxRestaurant extends Restaurant.Stub {
            Map map = new HashMap();
            public Ticket getTicket(String in) {
                return (Ticket) map.get(in);
            }
        }
        FauxRestaurant alices = new FauxRestaurant();
        Ticket foo = new Ticket("123", null);
        alices.map.put("123", foo);

        Customer bob = new Customer(new LocalRestaurantClient(alices));

        assertEquals(Boolean.TRUE, bob.cancelOrder("123"));
        assertEquals(Boolean.FALSE, bob.cancelOrder("not there"));
    }

}
