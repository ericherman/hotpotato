/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ConnectionServer;
import hotpotato.model.Customer;
import hotpotato.model.Worker;
import hotpotato.net.SocketHotpotatoServer;
import hotpotato.testsupport.ReturnStringOrder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class AcceptanceTest extends TestCase {
    private SocketHotpotatoServer server;
    private Worker mel;
    private Worker ophilia;
    private Worker peter;
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
        server = new SocketHotpotatoServer(0);
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
    }

    public void test1Customer1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        Callable<Serializable> order = new ReturnStringOrder("fries");
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
        Callable<Serializable> order = new ReturnStringOrder("fries");
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
        Callable<Serializable> bOrder = new ReturnStringOrder("Bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Customer chris = newCustomer();
        Callable<Serializable> cOrder = new ReturnStringOrder("Chris' Fries");
        String cNum = chris.placeOrder("bob", cOrder);

        Customer david = newCustomer();
        Callable<Serializable> dOrder = new ReturnStringOrder("David's Fries");
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

        Callable<Serializable> bOrder = new ReturnStringOrder("bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Callable<Serializable> cOrder = new ReturnStringOrder("chris' fries");
        String cNum = chris.placeOrder("chris", cOrder);

        Callable<Serializable> dOrder = new ReturnStringOrder("david's fries");
        String dNum = david.placeOrder("david", dOrder);

        Callable<Serializable> eOrder = new ReturnStringOrder("eve's fries");
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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        InetAddress host = InetAddress.getLocalHost();
        int port = server.getPort();
        ReverseRunner runner = new ReverseRunner(host, port);
        runner.setOut(out);
        runner.setMaxWorkTimeSeconds(10);

        runner.reverse("foo");

        String EOL = System.getProperty("line.separator");
        String oof = "oof" + EOL;
        assertEquals(oof, new String(baos.toByteArray()));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceTest.class);
    }
}
