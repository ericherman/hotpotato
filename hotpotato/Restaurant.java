/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato;

import hotpotato.model.*;

import java.io.*;

public interface Restaurant {
    String takeOrder(String prefix, Order obj); // return value is Ticket ID
    Ticket getNextTicket();
    Ticket getTicket(String id);
    void returnResult(String ticketId, Serializable orderResult);
    Serializable pickUpOrder(String id);
    int ordersDelivered();

    public static class Stub implements Restaurant {
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