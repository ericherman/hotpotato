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

public class OrderTaker extends ConnectionStation {
    private Queue ticketWheel;
    private int nextOrderNumber;

    public OrderTaker(int port, Queue ticketWheel) throws IOException {
        super(port, "OrderTaker");
        this.ticketWheel = ticketWheel;
        nextOrderNumber = 0;
        Thread.yield();
    }

    protected void acceptConnection(Socket s) throws IOException {
        Order order;
        int thisOrder = nextOrderNumber;
        nextOrderNumber++;
        order = (Order) new ObjectReceiver(s).receive();
        String orderNumberStr = assignOrder(thisOrder, order);
        new ObjectSender(s).send(orderNumberStr);
    }

    private String assignOrder(int thisOrderNumber, Order order)
        throws IOException {

        String orderNumberStr = Integer.toString(thisOrderNumber);
        order.setId(orderNumberStr);
        ticketWheel.add(order);
        return orderNumberStr;
    }
}
