/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.io.*;
import hotpotato.model.*;

import java.net.*;

public class CustomerRunner {
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int maxLoops = maxWorkTimeSeconds * 4;
        int orderPort = Integer.parseInt(args[1]);
        int pickupPort = Integer.parseInt(args[2]);
        String className = args[3];
        int reportingPort = Integer.parseInt(args[4]);
        InetAddress host = InetAddress.getLocalHost();

        Customer bob = new Customer();
        bob.setResturant(host, orderPort, pickupPort);
        Class aClass = Class.forName(className);
        Order order = (Order) aClass.newInstance();
        bob.placeOrder(order);
        Order completedWork = null;

        for (int i = 0; i < maxLoops && completedWork == null; i++) {
            Thread.sleep(250);
            completedWork = bob.pickupOrder();
        }

        Boolean report = (completedWork == null) ? Boolean.FALSE : Boolean.TRUE;
        new ObjectSender(new Socket(host, reportingPort)).send(report);
    }
}
