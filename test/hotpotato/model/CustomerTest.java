/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoServer;
import hotpotato.acceptance.Factor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

public class CustomerTest extends TestCase {

    public void testPlaceOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Callable<? extends Serializable> order = null;

            public String takeOrder(String id,
                    Callable<? extends Serializable> in) {
                order = in;
                return id + "7";
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        Callable<String> foo = new ReturnStringOrder("foo");
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

        assertEquals(true, bob.cancelOrder("123"));
        assertEquals(false, bob.cancelOrder("not there"));
    }

    public void testExecute() {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            String id = null;

            public String takeOrder(String prefix,
                    Callable<? extends Serializable> obj) {
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
        class MyOrder implements Runnable, Serializable {
            private static final long serialVersionUID = 1L;
            public AtomicInteger timesRun = new AtomicInteger(0);

            public void run() {
                timesRun.incrementAndGet();
            }
        }

        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));
        MyOrder order1 = new MyOrder();
        assertEquals(0, order1.timesRun.get());
        bob.execute(order1);
        assertEquals(1, order1.timesRun.get());
    }

    public void testCall() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Callable<? extends Serializable> job = null;
            Serializable result = null;

            public String takeOrder(String prefix,
                    Callable<? extends Serializable> obj) {
                job = obj;
                return prefix + 7;
            }

            public Serializable pickUpOrder(String id) {
                return result;
            }

            public void simulateWorkerComplete() throws Exception {
                result = job.call();
            }

            public Ticket getTicket(String id) {
                return new Ticket(id, 7, 0l, job);
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Customer bob = new Customer(new LocalHotpotatoClient(alices));

        Callable<List<Long>> job = new Factor(2l * 3l * 3l);

        Future<List<Long>> future = bob.submit(job);
        assertEquals(false, future.isCancelled());
        assertEquals(false, future.isDone());

        alices.simulateWorkerComplete();

        List<Long> expected = new ArrayList<Long>();
        expected.add(2l);
        expected.add(3l);
        expected.add(3l);
        List<Long> result = future.get();
        assertEquals(false, future.isCancelled());
        assertEquals(true, future.isDone());
        assertEquals(expected, result);

        alices.job = null;
        alices.result = null;
        future = bob.submit(job);
        assertEquals(false, future.isCancelled());
        assertEquals(false, future.isDone());
        future.cancel(true);
        assertEquals(true, future.isCancelled());
        assertEquals(false, future.isDone());
    }

}
