/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.util.*;

/**
 * TicketQueue is marked final as a hint to the compiler. It can safely be
 * un-finalized.
 * 
 * When making multiple calls to a TicketQueue, please consider whether we need
 * to perform all operations within a single block synchronized on the
 * TicketWheel.
 * 
 * <code>
 * if (ticketQueue.hasItems())  // possible threading problem
 *     a = ticketQueue.get();   // what if ticketWheel is now empty?
 * </code>
 * 
 * <code>
 * synchronized (ticketQueue) {
 *     if (ticketQueue.hasItems())
 *         a = ticketQueue.get();
 * }
 * </code>
 */
public final class TicketQueue {

    private Map map;
    private LinkedList queue;

    public TicketQueue() {
        queue = new LinkedList();
        map = new HashMap();
    }

    synchronized public int size() {
        return queue.size();
    }

    synchronized public boolean hasItems() {
        return !queue.isEmpty();
    }

    synchronized public boolean isEmpty() {
        return queue.isEmpty();
    }

    synchronized public boolean hasItem(String id) {
        return map.containsKey(id);
    }

    synchronized public void add(Ticket ticket) {
        if (ticket == null)
            throw new IllegalArgumentException("null");
        map.put(ticket.getId(), ticket);
        queue.addLast(ticket);
    }

    synchronized public Ticket get() {
        Ticket ticket = (Ticket) queue.removeFirst();
        map.remove(ticket.getId());
        return ticket;
    }

    synchronized public Ticket get(String id) {
        Ticket ticket = (Ticket) map.get(id);
        queue.remove(ticket);
        return ticket;
    }
}