/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class CookTest extends TestCase {
    private static final int TOO_MANY = 5000000;

    private FakeTicketWheel toDo;
    private FakeCounterTop done;

    protected void tearDown() throws Exception {
        if (toDo != null)
            toDo.shutdown();
        if (done != null)
            done.shutdown();
    }

    private void assertStatus(String msg, Cook.Status expected, Cook cook) {
        if (cook.getStatus() == Cook.Status.PROBLEM) {
            cook.getLastProblem().printStackTrace();
        }
        assertEquals(msg, expected, cook.getStatus());
    }

    class FakeCounterTop extends ConnectionStation {
        private Queue completedWork;
        public FakeCounterTop() throws IOException {
            super(0, "foo");
            completedWork = new SynchronizedQueue();
        }
        protected void acceptConnection(Socket s) throws IOException {
            completedWork.add(new ObjectReceiver(s).receive());
        }
        public Order get() {
            int i = 0;
            while (completedWork.isEmpty()) {
                Thread.yield();
                assertTrue(i++ < TOO_MANY);
            }
            return (Order) completedWork.get();
        }
    }

    class FakeTicketWheel extends ConnectionStation {
        private Order work = null;
        public FakeTicketWheel() throws IOException {
            super(0, "foo");
        }
        protected void acceptConnection(Socket s) throws IOException {
            new ObjectSender(s).send(work);
        }
        public void set(Order item) {
            work = item;
        }
    }

    public void testCook() throws Exception {
        toDo = new FakeTicketWheel();
        done = new FakeCounterTop();
        toDo.start();
        done.start();
        Cook cook = new Cook();
        assertStatus("NO_SERVER", Cook.Status.NO_SERVER, cook);
        InetAddress localHost = InetAddress.getLocalHost();

        cook.setKitchen(localHost, toDo.getPort(), done.getPort());
        assertStatus("polling", Cook.Status.POLLING, cook);
        cook.reset();
        assertStatus("quit", Cook.Status.NO_SERVER, cook);
        cook.setKitchen(localHost, toDo.getPort(), done.getPort());
        assertStatus("2", Cook.Status.POLLING, cook);

        Order message = new WaitingOrder("foo");
        toDo.set(message);

        Order completedWork = done.get();
        assertEquals("complete", true, completedWork.isComplete());

        for (int i = 0; cook.getStatus() != Cook.Status.POLLING; i++) {
            assertTrue("infinite loop 3 " + cook.getStatus(), i < TOO_MANY);
        }

        cook.reset();
        assertEquals("finished", Cook.Status.NO_SERVER, cook.getStatus());
    }

    static class WaitingOrder extends NamedOrder {
        public WaitingOrder(String s) {
            super(s);
        }
        public void exec() {
            for (int i = 0; i < 20000; i++) {
                Thread.yield();
            }
            super.exec();
        }
    }
}
