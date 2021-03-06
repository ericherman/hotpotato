/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoClient;
import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.io.ConnectionServer;

import java.io.Serializable;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class WorkerTest extends TestCase {

    public void testPickupOrder() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Serializable result = null;

            public void returnResult(Ticket ticket, Serializable in) {
                this.result = in;
            }
        }
        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Worker mel = new Worker(new LocalHotpotatoClient(alices));

        mel.returnResult(new Ticket("", 1, 0, null), "foo");
        assertEquals("foo", alices.result);
    }

    public void testGetNextOrderRequest() throws Exception {
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Ticket ticket = null;

            public Ticket getNextTicket() {
                return ticket;
            }
        }

        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Worker mel = new Worker(new LocalHotpotatoClient(alices));
        alices.ticket = new Ticket("", 1, 0, null);

        Ticket ticket = mel.getNextOrder();
        assertEquals(alices.ticket, ticket);
    }

    public void testShutdown() throws Exception {
        Worker mel = new Worker(new HotpotatoClient() {
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
        class FauxHotpotatoServer extends HotpotatoServer.Stub {
            Ticket toDo = null;
            Serializable done = null;

            public void returnResult(Ticket ticket, Serializable in) {
                this.done = in;
            }

            public Ticket getNextTicket() {
                Ticket get = toDo;
                toDo = null;
                return get;
            }
        }

        FauxHotpotatoServer alices = new FauxHotpotatoServer();
        Worker cook = new Worker(new LocalHotpotatoClient(alices));

        new Thread(cook).start();
        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Callable<String> order = new ReturnStringOrder("foo");
        alices.toDo = new Ticket("", 123, 0, order);

        for (int i = 0; i < 200 && alices.done == null; i++) {
            Thread.sleep(5);
        }

        assertNotNull(alices.done);

        assertEquals(order.call(), alices.done);

        cook.shutdown();
    }
}
