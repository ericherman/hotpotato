/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.model.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class AcceptanceTest extends TestCase {

    /* netstat -anp | grep 666 */
    private static final int COUNTER_TOP = 6669;
    private static final int PICK_UP = 6667;
    private static final int PLACE_ORDERS = 6666;
    private static final int TICKET_WHEEL = 6668;

    private Restaurant alices;
    private Cook mel;
    private Cook ophilia;
    private Cook peter;

    private Cook newCook() throws UnknownHostException {
        Cook c = new Cook();
        c.setKitchen(alices.getInetAddress(), TICKET_WHEEL, COUNTER_TOP);
        return c;
    }

    private Customer newCustomer() throws IOException, UnknownHostException {
        Customer bob = new Customer();
        bob.setResturant(alices.getInetAddress(), PLACE_ORDERS, PICK_UP);
        return bob;
    }

    protected void setUp() throws Exception {
        alices =
            new Restaurant(PLACE_ORDERS, TICKET_WHEEL, COUNTER_TOP, PICK_UP);

        Threads.pause();
        System.gc();
    }

    protected void tearDown() throws Exception {
        if (mel != null)
            mel.reset();
        if (ophilia != null)
            ophilia.reset();
        if (peter != null)
            peter.reset();
        alices.shutdown();

        mel = null;
        ophilia = null;
        peter = null;
        alices = null;

        Threads.pause();
    }

    public void test1Customer1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        NamedOrder order = new NamedOrder("fries");
        bob.placeOrder(order);

        Threads.pause();

        Order fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("infinite loop", i < 10000);
            fries = bob.pickupOrder();
            Thread.yield();
        }

        assertTrue("complete", fries.isComplete());
        assertEquals("equal", order, fries);
        assertEquals(1, mel.ordersFilled());
    }

    public void test1Customer2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        NamedOrder order = new NamedOrder("fries");
        bob.placeOrder(order);

        Threads.pause();

        Order fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("infinite loop", i < 10000);
            fries = bob.pickupOrder();
            Thread.yield();
        }

        assertTrue("complete", fries.isComplete());
        assertEquals("equal", order, fries);
        assertEquals(1, mel.ordersFilled() + ophilia.ordersFilled());
    }

    public void test1Customer3Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();
        peter = newCook();

        Customer bob = newCustomer();
        NamedOrder order = new NamedOrder("fries");
        bob.placeOrder(order);

        Threads.pause();

        Order fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("infinite loop", i < 10000);
            fries = bob.pickupOrder();
            Thread.yield();
        }

        assertTrue("complete", fries.isComplete());
        assertEquals("equal", order, fries);
    }

    public void test2Customers1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("Bob's Fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        for (int i = 0;(bFries == null || cFries == null); i++) {
            String status = bFries + ", " + cFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);
            if (cFries == null)
                cFries = chris.pickupOrder();
            if (bFries == null)
                bFries = bob.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);
        assertEquals(2, mel.ordersFilled());
    }

    public void test2Customers2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        for (int i = 0;(bFries == null || cFries == null); i++) {
            String status = bFries + ", " + cFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);
            if (bFries == null)
                bFries = bob.pickupOrder();
            if (cFries == null)
                cFries = chris.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);
        assertEquals(2, mel.ordersFilled() + ophilia.ordersFilled());
    }

    public void test2Customers3Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();
        peter = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        for (int i = 0;(bFries == null || cFries == null); i++) {
            String status = bFries + ", " + cFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);
            if (bFries == null)
                bFries = bob.pickupOrder();
            if (cFries == null)
                cFries = chris.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);
    }

    public void test3Customers1Cook() throws Exception {
        mel = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("Bob's fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Customer david = newCustomer();
        NamedOrder dOrder = new NamedOrder("David's Fries");
        david.placeOrder(dOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        Order dFries = null;

        for (int i = 0;
            (bFries == null || cFries == null || dFries == null);
            i++) {
            String status = bFries + ", " + cFries + ", " + dFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);
            if (cFries == null)
                cFries = chris.pickupOrder();
            if (dFries == null)
                dFries = david.pickupOrder();
            if (bFries == null)
                bFries = bob.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);

        assertTrue("complete", dFries.isComplete());
        assertEquals("equal", dOrder, dFries);
    }

    public void test3Customers2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("Bob's fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Customer david = newCustomer();
        NamedOrder dOrder = new NamedOrder("David's Fries");
        david.placeOrder(dOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        Order dFries = null;

        for (int i = 0;
            (bFries == null || cFries == null || dFries == null);
            i++) {
            String status = bFries + ", " + cFries + ", " + dFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);

            if (bFries == null)
                bFries = bob.pickupOrder();
            if (cFries == null)
                cFries = chris.pickupOrder();
            if (dFries == null)
                dFries = david.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);

        assertTrue("complete", dFries.isComplete());
        assertEquals("equal", dOrder, dFries);
    }

    public void test3Customers3Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();
        peter = newCook();

        Customer bob = newCustomer();
        NamedOrder bOrder = new NamedOrder("Bob's fries");
        bob.placeOrder(bOrder);

        Customer chris = newCustomer();
        NamedOrder cOrder = new NamedOrder("Chris' Fries");
        chris.placeOrder(cOrder);

        Customer david = newCustomer();
        NamedOrder dOrder = new NamedOrder("David's Fries");
        david.placeOrder(dOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        Order dFries = null;

        for (int i = 0;
            (bFries == null || cFries == null || dFries == null);
            i++) {
            String status = bFries + ", " + cFries + ", " + dFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);

            if (bFries == null)
                bFries = bob.pickupOrder();
            if (cFries == null)
                cFries = chris.pickupOrder();
            if (dFries == null)
                dFries = david.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);

        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);

        assertTrue("complete", dFries.isComplete());
        assertEquals("equal", dOrder, dFries);
    }

    public void test4Customers2Cooks() throws Exception {
        mel = newCook();
        ophilia = newCook();

        Customer bob = newCustomer();
        Customer chris = newCustomer();
        Customer david = newCustomer();
        Customer eve = newCustomer();

        NamedOrder bOrder = new NamedOrder("bob's fries");
        bob.placeOrder(bOrder);

        NamedOrder cOrder = new NamedOrder("chris' fries");
        chris.placeOrder(cOrder);

        NamedOrder dOrder = new NamedOrder("david's fries");
        david.placeOrder(dOrder);

        NamedOrder eOrder = new NamedOrder("eve's fries");
        eve.placeOrder(eOrder);

        Threads.pause();

        Order bFries = null;
        Order cFries = null;
        Order dFries = null;
        Order eFries = null;

        for (int i = 0;
            (bFries == null
                || cFries == null
                || dFries == null
                || eFries == null);
            i++) {

            String status =
                bFries + ", " + cFries + ", " + dFries + ", " + eFries;
            assertTrue("infinite loop (" + status + ")", i < 10000);

            if (bFries == null)
                bFries = bob.pickupOrder();
            if (cFries == null)
                cFries = chris.pickupOrder();
            if (dFries == null)
                dFries = david.pickupOrder();
            if (eFries == null)
                eFries = eve.pickupOrder();

            Thread.yield();
        }

        assertTrue("complete", bFries.isComplete());
        assertEquals("equal", bOrder, bFries);
        assertTrue("complete", cFries.isComplete());
        assertEquals("equal", cOrder, cFries);
        assertTrue("complete", dFries.isComplete());
        assertEquals("equal", dOrder, dFries);
        assertTrue("complete", eFries.isComplete());
        assertEquals("equal", eOrder, eFries);
        assertEquals(4, mel.ordersFilled() + ophilia.ordersFilled());
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AcceptanceTest.class);
    }
}
