/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ObjectSender;
import hotpotato.model.Customer;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class CustomerRunner {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int orderPort = Integer.parseInt(args[1]);
        String className = args[2];
        int reportingPort = Integer.parseInt(args[3]);

        InetAddress host = InetAddress.getLocalHost();
        int maxLoops = maxWorkTimeSeconds * 4;

        Customer bob = new Customer(host, orderPort);
        Class<?> aClass = Class.forName(className);
        Callable<Serializable> order;
        order = (Callable<Serializable>) aClass.newInstance();
        String orderNumber = bob.placeOrder("foo", order);
        Serializable completedWork = null;

        for (int i = 0; i < maxLoops && completedWork == null; i++) {
            Thread.sleep(250);
            completedWork = bob.pickupOrder(orderNumber);
        }

        new ObjectSender(new Socket(host, reportingPort)).send(completedWork);
    }
}
