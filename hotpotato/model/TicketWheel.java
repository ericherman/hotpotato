/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import java.util.*;

/**
 * TicketWheel is marked final as a hint to the compiler. 
 * It can safely be un-finalized.
 * 
 * When making multiple calls to a single TicketWheel, please consider whether 
 * we need to perform all operations within a single block synchronized on the
 * TicketWheel.  For example, if our code calls <code>hasItems</code> and then 
 * calls <code>get</code> if the <code>hasItems</code> call returns true, we 
 * should enclose all of that code in a block synchronized on the TicketWheel.
 */
public final class TicketWheel {

    private Map map;
    private LinkedList queue;

    public TicketWheel() {
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
