/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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

    private Map<String, Ticket> map;
    private LinkedList<Ticket> queue;

    public TicketQueue() {
        queue = new LinkedList<Ticket>();
        map = new HashMap<String, Ticket>();
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
        Ticket ticket = queue.removeFirst();
        map.remove(ticket.getId());
        return ticket;
    }

    synchronized public Ticket get(String id) {
        Ticket ticket = map.remove(id);
        queue.remove(ticket);
        return ticket;
    }
}
