/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.model;

import hotpotato.*;

import java.io.*;
import java.util.*;

public class AlicesRestaurant implements Restaurant {
    private int nextOrderNumber;
    private int ordersDelivered;
    TicketWheel ticketWheel;
    Map counterTop;

    public AlicesRestaurant() {
        nextOrderNumber = 0;
        ordersDelivered = 0;
        ticketWheel = new TicketWheel();
        counterTop = new HashMap();
    }

    public String takeOrder(String prefix, Order order) {
        String orderNumberStr = Integer.toString(nextOrderNumber++);
        Ticket ticket = new Ticket(prefix + orderNumberStr, order);
        ticketWheel.add(ticket);
        return ticket.getId();
    }

    public Ticket getNextTicket() {
        Ticket item = null;
        synchronized (ticketWheel) {
            if (ticketWheel.hasItems()) {
                item = ticketWheel.get();
            }
        }
        return item;
    }

    public Ticket getTicket(String id) {
        Ticket item = null;
        synchronized (ticketWheel) {
            if (ticketWheel.hasItem(id)) {
                item = ticketWheel.get(id);
            }
        }
        return item;
    }

    public void returnResult(String ticketId, Serializable orderResult) {
        counterTop.put(ticketId, orderResult);
    }

    public Serializable pickUpOrder(String id) {
        Serializable out = null;
        synchronized (counterTop) {
            if (counterTop.get(id) != null) {
                out = (Serializable) counterTop.get(id);
                counterTop.remove(id);
                ordersDelivered++;
            }
        }
        return out;
    }

    public int ordersDelivered() {
        return ordersDelivered;
    }
}