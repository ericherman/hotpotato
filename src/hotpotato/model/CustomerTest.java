/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.*;
import hotpotato.testsupport.*;

import java.io.*;
import java.util.*;

import junit.framework.*;

public class CustomerTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Order order = null;
            public String takeOrder(String id, Order in) {
                order = in;
                return id + "7";
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        Order foo = new ReturnStringOrder("foo");
        bob.placeOrder("bob", foo);
        assertEquals(foo, alices.order);
    }

    public void testPickupOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            String id = null;
            public Serializable pickUpOrder(String in) {
                this.id = in;
                return (id == "bar") ? "bar" : null;
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        assertNull(bob.pickupOrder("foo"));
        assertEquals("foo", alices.id);

        assertEquals("bar", bob.pickupOrder("bar"));
        assertEquals("bar", alices.id);
    }

    public void testCancelOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Map map = new HashMap();
            public Ticket getTicket(String in) {
                return (Ticket) map.get(in);
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Ticket foo = new Ticket("123", null);
        alices.map.put("123", foo);

        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        assertEquals(Boolean.TRUE, bob.cancelOrder("123"));
        assertEquals(Boolean.FALSE, bob.cancelOrder("not there"));
    }

}
