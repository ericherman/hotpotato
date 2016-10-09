/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoServer;
import hotpotato.util.Clock;
import hotpotato.util.RealClock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Hotpotatod implements HotpotatoServer {
    private AtomicInteger nextOrderNumber;
    private AtomicInteger ordersDelivered;
    TicketQueue ticketWheel;
    Map<String, Serializable> counterTop;
    private Clock clock;

    public Hotpotatod() {
        nextOrderNumber = new AtomicInteger(0);
        ordersDelivered = new AtomicInteger(0);
        ticketWheel = new TicketQueue();
        counterTop = new HashMap<String, Serializable>();
        clock = new RealClock();
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public String takeOrder(String prefix,
            Callable<? extends Serializable> order) {
        int number = nextOrderNumber.incrementAndGet();
        long time = clock.currentTimeMillis();
        Ticket ticket = new Ticket(prefix, number, time, order);
        ticketWheel.add(ticket);
        return prefix + ticket.getNumber();
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

    public void returnResult(Ticket ticket, Serializable orderResult) {
        if (ticket.getId() != null) {
            counterTop.put(ticket.getId(), orderResult);
        }
    }

    public Serializable pickUpOrder(String id) {
        Serializable out = null;
        synchronized (counterTop) {
            if (counterTop.get(id) != null) {
                out = counterTop.get(id);
                counterTop.remove(id);
                ordersDelivered.incrementAndGet();
            }
        }
        return out;
    }

    public int ordersDelivered() {
        return ordersDelivered.intValue();
    }
}
