/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.model;

import hotpotato.*;
import hotpotato.io.*;
import hotpotato.testsupport.LocalRestaurantClient;
import hotpotato.testsupport.ReturnStringOrder;

import java.io.*;

import junit.framework.*;

public class CookTest extends TestCase {

    public void testPickupOrder() throws Exception {
        class FauxRestaurant extends Restaurant.Stub {
            Serializable result = null;
            public void returnResult(String id, Serializable in) {
                this.result = in;
            }
        }
        FauxRestaurant alices = new FauxRestaurant();
        Cook mel = new Cook(new LocalRestaurantClient(alices));

        mel.returnResult("1", "foo");
        assertEquals("foo", alices.result);
    }

    public void testGetNextOrderRequest() throws Exception {
        class FauxRestaurant extends Restaurant.Stub {
            Ticket ticket = null;
            public Ticket getNextTicket() {
                return ticket;
            }
        }

        FauxRestaurant alices = new FauxRestaurant();
        Cook mel = new Cook(new LocalRestaurantClient(alices));
        alices.ticket = new Ticket("1", null);

        Ticket ticket = mel.getNextOrder();
        assertEquals(alices.ticket, ticket);
    }

    public void testShutdown() throws Exception {
        Cook mel = new Cook(new RestaurantClient() {
            public Serializable send(Request request) {
                return null;
            }
        });

        Thread t = new Thread(mel);
        t.start();
        Thread.sleep(ConnectionServer.SLEEP_DELAY);
        assertTrue(t.isAlive());

        mel.shutdown();
        Thread.sleep(ConnectionServer.SLEEP_DELAY);
        assertFalse(t.isAlive());
    }

    public void testRoundTrip() throws Throwable {
        class FauxRestaurant extends Restaurant.Stub {
            Ticket toDo = null;
            Serializable done = null;
            public void returnResult(String id, Serializable in) {
                this.done = in;
            }
            public Ticket getNextTicket() {
                Ticket get = toDo;
                toDo = null;
                return get;
            }
        }

        FauxRestaurant alices = new FauxRestaurant();
        Cook cook = new Cook(new LocalRestaurantClient(alices));

        new Thread(cook).start();
        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Order order = new ReturnStringOrder("foo");
        alices.toDo = new Ticket("123", order);

        for (int i = 0; i < 200 && alices.done == null; i++) {
            Thread.sleep(5);
        }

        assertNotNull(alices.done);

        assertEquals(order.exec(), alices.done);

        cook.shutdown();
    }
}
