/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.util.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Restaurant {
    private Queue toDoQueue;
    private Map completedOrders;
    private OrderTaker orderTaker;
    private OrderPresenter orderPresenter;
    private TicketWheel toDo;
    private CounterTop done;

    public Restaurant(
        int newOrdersPort,
        int ticketWheelPort,
        int completedOrdersPort,
        int orderPickUpPort)
        throws IOException {

        toDoQueue = new SynchronizedQueue();
        completedOrders = new HashMap();

        toDo = new TicketWheel(ticketWheelPort, toDoQueue);
        done = new CounterTop(completedOrdersPort, completedOrders);
        orderTaker = new OrderTaker(newOrdersPort, toDoQueue);
        orderPresenter = new OrderPresenter(orderPickUpPort, completedOrders);

        toDo.start();
        done.start();
        orderTaker.start();
        orderPresenter.start();
    }

    public void shutdown() throws IOException {
        orderTaker.shutdown();
        orderPresenter.shutdown();
        toDo.shutdown();
        done.shutdown();
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public int happyCustomers() {
        return orderPresenter.ordersDelivered();
    }
}
