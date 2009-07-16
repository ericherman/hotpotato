/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato;

import hotpotato.model.Ticket;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface HotpotatoServer {
    /**
     * @return Ticket ID
     */
    String takeOrder(String prefix, Callable<Serializable> obj);

    Ticket getNextTicket();

    Ticket getTicket(String id);

    void returnResult(Ticket ticket, Serializable orderResult);

    Serializable pickUpOrder(String id);

    int ordersDelivered();

    public static class Stub implements HotpotatoServer {
        public Ticket getNextTicket() {
            throw new UnsupportedOperationException();
        }

        public Ticket getTicket(String id) {
            throw new UnsupportedOperationException();
        }

        public int ordersDelivered() {
            throw new UnsupportedOperationException();
        }

        public Serializable pickUpOrder(String id) {
            throw new UnsupportedOperationException();
        }

        public void returnResult(Ticket ticket, Serializable orderResult) {
            throw new UnsupportedOperationException();
        }

        public String takeOrder(String prefix, Callable<Serializable> obj) {
            throw new UnsupportedOperationException();
        }
    }
}
