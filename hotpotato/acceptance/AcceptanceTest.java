/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.*;
import hotpotato.io.*;
import hotpotato.model.*;
import hotpotato.net.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class AcceptanceTest extends TestCase {
    private RestaurantServer server;
    private Cook mel;
    private Cook ophilia;
    private Cook peter;
    private static int LOOP_LIMIT = 10;

    private Cook newCook() throws UnknownHostException {
        Cook c = new Cook(server.getInetAddress(), server.getPort());
        new Thread(c).start();
        return c;
    }

    private Customer newCustomer() throws IOException, UnknownHostException {
        Customer bob = new Customer(server.getInetAddress(), server.getPort());
        return bob;
    }

    protected void setUp() throws Exception {
        server = new RestaurantServer(0);
        server.start();
    }

    protected void tearDown() throws Exception {
        if (mel != null)
            mel.shutdown();
        if (ophilia != null)
            ophilia.shutdown();
        if (peter != null)
            peter.shutdown();
        server.shutdown();

        mel = null;
        ophilia = null;
        peter = null;
        server = null;
    }

    public void test1Customer1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        Order order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("loop limit reached", i < LOOP_LIMIT);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals(order.exec(), fries);
        assertEquals(1, mel.ordersFilled());
    }

    public void test1Customer3Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();
        peter = newCook();

        Customer bob = newCustomer();
        Order order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("loop limit reached", i < LOOP_LIMIT);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);

        }

        assertEquals(order.exec(), fries);
    }

    public void test3Customers1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        Order bOrder = new ReturnStringOrder("Bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Customer chris = newCustomer();
        Order cOrder = new ReturnStringOrder("Chris' Fries");
        String cNum = chris.placeOrder("bob", cOrder);

        Customer david = newCustomer();
        Order dOrder = new ReturnStringOrder("David's Fries");
        String dNum = david.placeOrder("bob", dOrder);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable bFries = null;
        Serializable cFries = null;
        Serializable dFries = null;

        for (int i = 0;
            (bFries == null || cFries == null || dFries == null);
            i++) {
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

        assertEquals(bOrder.exec(), bFries);
        assertEquals(cOrder.exec(), cFries);
        assertEquals(dOrder.exec(), dFries);
    }

    public void test4Customers2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        Customer chris = newCustomer();
        Customer david = newCustomer();
        Customer eve = newCustomer();

        Order bOrder = new ReturnStringOrder("bob's fries");
        String bNum = bob.placeOrder("bob", bOrder);

        Order cOrder = new ReturnStringOrder("chris' fries");
        String cNum = chris.placeOrder("chris", cOrder);

        Order dOrder = new ReturnStringOrder("david's fries");
        String dNum = david.placeOrder("david", dOrder);

        Order eOrder = new ReturnStringOrder("eve's fries");
        String eNum = eve.placeOrder("eve", eOrder);

        Thread.sleep(ConnectionServer.SLEEP_DELAY);

        Serializable bFries = null;
        Serializable cFries = null;
        Serializable dFries = null;
        Serializable eFries = null;

        for (int i = 0;
            (bFries == null
                || cFries == null
                || dFries == null
                || eFries == null);
            i++) {

            String status =
                bFries + ", " + cFries + ", " + dFries + ", " + eFries;
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

        assertEquals(bOrder.exec(), bFries);
        assertEquals(cOrder.exec(), cFries);
        assertEquals(dOrder.exec(), dFries);
        assertEquals(eOrder.exec(), eFries);
        assertEquals(4, mel.ordersFilled() + ophilia.ordersFilled());
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AcceptanceTest.class);
    }
}