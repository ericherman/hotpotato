/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.*;

import java.io.*;
import java.util.*;

public class Hotpotatod implements HotpotatoServer {
    private int nextOrderNumber;
    private int ordersDelivered;
    TicketQueue ticketWheel;
    Map counterTop;

    public Hotpotatod() {
        nextOrderNumber = 0;
        ordersDelivered = 0;
        ticketWheel = new TicketQueue();
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
