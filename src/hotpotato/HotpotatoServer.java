/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato;

import hotpotato.model.*;

import java.io.*;

public interface HotpotatoServer {
    String takeOrder(String prefix, Order obj); // return value is Ticket ID
    Ticket getNextTicket();
    Ticket getTicket(String id);
    void returnResult(String ticketId, Serializable orderResult);
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

        public void returnResult(String ticketId, Serializable orderResult) {
            throw new UnsupportedOperationException();
        }

        public String takeOrder(String prefix, Order obj) {
            throw new UnsupportedOperationException();
        }
    }
}
