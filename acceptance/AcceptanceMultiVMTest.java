/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.model.*;
import hotpotato.util.*;

import java.net.*;

import junit.framework.*;

public class AcceptanceMultiVMTest extends TestCase {

    // netstat -anpt | grep 666
    private static final int PLACE_ORDERS = 6676;
    private static final int PICK_UP = 6677;
    private static final int TICKET_WHEEL = 6678;
    private static final int COUNTER_TOP = 6679;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AcceptanceMultiVMTest.class);
    }

    protected void setUp() throws Exception {
        Thread.sleep(1100);
    }

    protected void tearDown() throws Exception {
        Thread.sleep(1100); // let launched processes complete
    }

    public void test1Customer1Cook() throws Exception {
        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "1";

        String[] restaurantArgs =
            new String[] {
                "java",
                "-cp",
                classpath,
                "hotpotato.acceptance.RestaurantRunner",
                maxSeconds,
                workUnits,
                "" + PLACE_ORDERS,
                "" + TICKET_WHEEL,
                "" + COUNTER_TOP,
                "" + PICK_UP,
                };

        String[] cookArgs =
            new String[] {
                "java",
                "-cp",
                classpath,
                "hotpotato.acceptance.CookRunner",
                maxSeconds,
                workUnits,
                InetAddress.getLocalHost().getHostName(),
                "" + TICKET_WHEEL,
                "" + COUNTER_TOP,
                };

        Threads.launch("Restaraunt", restaurantArgs, envp);
        Threads.pause(1000);
        Threads.launch("cook", cookArgs, envp);

        Customer bob = new Customer();
        bob.setResturant(InetAddress.getLocalHost(), PLACE_ORDERS, PICK_UP);

        Threads.pause(3000);

        NamedOrder order = new NamedOrder("fries");
        bob.placeOrder(order);

        Threads.pause(1000);

        Order fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("infinite loop", i < 10000);
            fries = bob.pickupOrder();
            Thread.yield();
        }

        assertTrue("complete", fries.isComplete());
        assertEquals("equal", order, fries);
    }
}
