/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.*;
import hotpotato.net.*;

import java.io.*;
import java.net.*;

public class Cook implements Runnable {
    private int ordersFilled;
    private boolean running;
    private RestaurantClient client;

    public Cook(InetAddress restaurantAddress, int port) {
        this(new NetworkRestaurantClient(restaurantAddress, port));
    }

    public Cook(RestaurantClient client) {
        this.client = client;
        ordersFilled = 0;
        running = false;
    }

    public void run() {
        Ticket work = null;
        running = true;
        while (running) {
            try {
                work = getNextOrder();
            } catch (IOException e) {
            }
            if (work != null) {
                Order order = work.getOrder();
                if (order != null) {
                    Serializable result = order.exec();
                    try {
                        returnResult(work.getId(), result);
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    }
                    ordersFilled++;
                }
            }
            Thread.yield();
        }
    }

    Ticket getNextOrder() throws IOException {
        return (Ticket) client.send(new GetNextOrderRequest());
    }

    void returnResult(String id, Serializable result) throws IOException {
        client.send(new ReturnOrderRequest(id, result));
    }

    public void shutdown() {
        running = false;
    }

    public int ordersFilled() {
        return ordersFilled;
    }

    private static class GetNextOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        public Serializable exec(Restaurant restaurant) {
            return restaurant.getNextTicket();
        }
    }

    private static class ReturnOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        String id;
        Serializable result;
        public ReturnOrderRequest(String id, Serializable result) {
            this.id = id;
            this.result = result;
        }
        public Serializable exec(Restaurant restaurant) {
            restaurant.returnResult(id, result);
            return null;
        }
    }
}
