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
import java.util.*;

public class OrderPresenter extends ConnectionStation {
    private Map counterTop;
    private int ordersDelivered;

    public OrderPresenter(int port, Map counterTop) throws IOException {
        super(port, "OrderPresenter");
        this.counterTop = counterTop;
        ordersDelivered = 0;
        for (int i = 0; i < 1000; i++)
            Thread.yield();
    }

    /**
     * first reads an "id" then writes an object for that "id"
     */
    protected void acceptConnection(Socket s) throws IOException {
        String orderNum = null;
        orderNum = "" + new ObjectReceiver(s).receive(); // null-safe toString()
        Order order = getOrderItemForOrderNumber(orderNum);
        new ObjectSender(s).send(order);
    }

    private Order getOrderItemForOrderNumber(String id) {
        Serializable out = null;
        synchronized (counterTop) {
            if (counterTop.get(id) != null) {
                out = (Serializable) counterTop.get(id);
                counterTop.remove(id);
                ordersDelivered++;
            }
        }
        return (Order) out;
    }

    public int ordersDelivered() {
        return ordersDelivered;
    }
}
