/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;

import java.io.*;
import java.net.*;

public class Customer {
    private String orderNumber;

    private InetAddress restaurantAddress;
    private int orderPort;
    private int pickupPort;

    public Customer() throws IOException {
        this.orderPort = -1;
        this.pickupPort = -1;
    }

    public void setResturant(
        InetAddress address,
        int orderPort,
        int pickupPort) {

        this.orderPort = orderPort;
        this.pickupPort = pickupPort;
        restaurantAddress = address;
    }

    public void placeOrder(Order order) throws IOException {
        Socket s = new Socket(restaurantAddress, orderPort);
        new ObjectSender(s).send(order);
        orderNumber = "" + new ObjectReceiver(s).receive();
        s.close();
    }

    public Order pickupOrder() throws IOException {
        try {
            Socket s = new Socket(restaurantAddress, pickupPort);
            new ObjectSender(s).send(orderNumber);
            Order item = (Order) new ObjectReceiver(s).receive();
            s.close();
            return item;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
