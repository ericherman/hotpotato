/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoServer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class CustomerTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Callable<Serializable> order = null;

            public String takeOrder(String id, Callable<Serializable> in) {
                order = in;
                return id + "7";
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        Callable<Serializable> foo = new ReturnStringOrder("foo");
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
            Map<String, Ticket> map = new HashMap<String, Ticket>();

            public Ticket getTicket(String in) {
                return map.get(in);
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Ticket foo = new Ticket("123", 7, 0, null);
        alices.map.put("123", foo);

        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        assertEquals(Boolean.TRUE, bob.cancelOrder("123"));
        assertEquals(Boolean.FALSE, bob.cancelOrder("not there"));
    }

    public static class MyOrder implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;
        public static AtomicInteger timesRun = new AtomicInteger(0);

        public void run() {
            timesRun.incrementAndGet();
        }
    }

    public void testExecute() {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            String id = null;

            public String takeOrder(String prefix, Callable<Serializable> obj) {
                try {
                    obj.call();
                } catch (Exception e) {
                    //
                }
                return "foo";
            }

            public Serializable pickUpOrder(String in) {
                this.id = in;
                return (id == "bar") ? "bar" : null;
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));
        assertEquals(0, MyOrder.timesRun.get());
        bob.execute(new MyOrder());
        assertEquals(1, MyOrder.timesRun.get());
    }

}
