/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ConnectionServer;
import hotpotato.model.Customer;
import hotpotato.model.ReturnStringOrder;
import hotpotato.model.Worker;
import hotpotato.net.SocketHotpotatoServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

public class AcceptanceTest extends TestCase {
    private SocketHotpotatoServer server;
    private Worker mel;
    private Worker ophilia;
    private Worker peter;
    private ByteArrayOutputStream baos;
    private PrintStream ps;
    private static int LOOP_LIMIT = 10;

    private Worker newCook() {
        Worker c = new Worker(server.getInetAddress(), server.getPort());
        new Thread(c).start();
        return c;
    }

    private Customer newCustomer() {
        Customer bob = new Customer(server.getInetAddress(), server.getPort());
        return bob;
    }

    protected void setUp() throws Exception {
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
        server = new SocketHotpotatoServer(0, ps);
        server.start();
    }

    protected void tearDown() throws Exception {
        if (mel != null) {
            mel.shutdown();
        }
        if (ophilia != null) {
            ophilia.shutdown();
        }
        if (peter != null) {
            peter.shutdown();
        }
        server.shutdown();

        mel = null;
        ophilia = null;
        peter = null;
        server = null;
        ps.close();
        ps = null;
        baos = null;
    }

    public void test1Customer1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        Callable<String> order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("loop limit reached", i < LOOP_LIMIT);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals(order.call(), fries);
        assertEquals(1, mel.ordersFilled());
    }

    public void test1Customer3Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();
        peter = newCook();

        Customer bob = newCustomer();
        Callable<String> order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("loop limit reached", i < LOOP_LIMIT);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);

        }

        assertEquals(order.call(), fries);
    }

    public void test3Customers1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        Callable<String> bOrder = new ReturnStringOrder("Bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Customer chris = newCustomer();
        Callable<String> cOrder = new ReturnStringOrder("Chris' Fries");
        String cNum = chris.placeOrder("bob", cOrder);

        Customer david = newCustomer();
        Callable<String> dOrder = new ReturnStringOrder("David's Fries");
        String dNum = david.placeOrder("bob", dOrder);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable bFries = null;
        Serializable cFries = null;
        Serializable dFries = null;

        for (int i = 0; anyNull(bFries, cFries, dFries); i++) {
            String status = bFries + ", " + cFries + ", " + dFries;
            assertTrue("loop limit reached (" + status + ")", i < LOOP_LIMIT);
            if (cFries == null)
                cFries = chris.pickupOrder(cNum);
            if (dFries == null)
                dFries = david.pickupOrder(dNum);
            if (bFries == null)
                bFries = bob.pickupOrder(bNum);

            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals(bOrder.call(), bFries);
        assertEquals(cOrder.call(), cFries);
        assertEquals(dOrder.call(), dFries);
    }

    private boolean anyNull(Object a, Object b, Object c) {
        return a == null || b == null || c == null;
    }

    private boolean anyNull(Object a, Object b, Object c, Object d) {
        return anyNull(a, b, c) || d == null;
    }

    public void test4Customers2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        Customer chris = newCustomer();
        Customer david = newCustomer();
        Customer eve = newCustomer();

        Callable<String> bOrder = new ReturnStringOrder("bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Callable<String> cOrder = new ReturnStringOrder("chris' fries");
        String cNum = chris.placeOrder("chris", cOrder);

        Callable<String> dOrder = new ReturnStringOrder("david's fries");
        String dNum = david.placeOrder("david", dOrder);

        Callable<String> eOrder = new ReturnStringOrder("eve's fries");
        String eNum = eve.placeOrder("eve", eOrder);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable bFries = null;
        Serializable cFries = null;
        Serializable dFries = null;
        Serializable eFries = null;

        for (int i = 0; anyNull(bFries, cFries, dFries, eFries); i++) {

            String status = bFries + ", " + cFries + ", " + dFries + ", "
                    + eFries;
            assertTrue("loop limit reached (" + status + ")", i < LOOP_LIMIT);

            if (bFries == null)
                bFries = bob.pickupOrder(bNum);
            if (cFries == null)
                cFries = chris.pickupOrder(cNum);
            if (dFries == null)
                dFries = david.pickupOrder(dNum);
            if (eFries == null)
                eFries = eve.pickupOrder(eNum);

            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals(bOrder.call(), bFries);
        assertEquals(cOrder.call(), cFries);
        assertEquals(dOrder.call(), dFries);
        assertEquals(eOrder.call(), eFries);
        assertEquals(4, mel.ordersFilled() + ophilia.ordersFilled());
    }

    public void testReverseExample() throws Exception {
        mel = newCook();

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos1);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        InetAddress host = InetAddress.getLocalHost();
        int port = server.getPort();
        ReverseRunner runner = new ReverseRunner(host, port);
        runner.setOut(out);
        runner.setMaxWorkTimeSeconds(10);

        runner.reverse("foo");

        String EOL = System.getProperty("line.separator");
        String oof = "oof" + EOL;
        assertEquals(oof, new String(baos1.toByteArray()));
    }

    public void test1Customer2CooksLotsOfJobs() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();

        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 100; i++) {
            Callable<String> job = new ReturnStringOrder("foo" + i);
            futures.add(bob.submit(job));
        }

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        int numDone = 0;
        for (int i = 0; numDone < futures.size(); i++) {
            assertTrue("loop limit reached", i < LOOP_LIMIT);
            numDone = 0;
            for (Future<?> future : futures) {
                if (future.isDone()) {
                    numDone++;
                } else {
                    getNoThrow(future, ConnectionServer.SLEEP_DELAY);
                }
            }

            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }
        assertEquals(futures.size(), numDone);
    }

    private void getNoThrow(Future<?> future, long sleepDelay)
            throws InterruptedException, ExecutionException {
        try {
            future.get(sleepDelay, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ignore) {
            // that's okay
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceTest.class);
    }
}
