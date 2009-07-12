/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import hotpotato.HotpotatoClient;
import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.net.NetworkHotpotatoClient;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class Worker implements Runnable {
    private int ordersFilled;
    private boolean running;
    private HotpotatoClient client;

    public Worker(InetAddress restaurantAddress, int port) {
        this(restaurantAddress, port, true);
    }

    public Worker(InetAddress restaurantAddress, int port, boolean sandbox) {
        this(new NetworkHotpotatoClient(restaurantAddress, port, sandbox));
    }

    public Worker(HotpotatoClient client) {
        this.client = client;
        ordersFilled = 0;
        running = false;
    }

    public void run() {
        Ticket work = null;
        running = true;
        while (running) {
            Thread.yield();
            try {
                work = getNextOrder();
            } catch (IOException e) { //
                // should we do anything here?
            }
            if (work == null) {
                continue;
            }
            Callable<Serializable> order = work.getOrder();
            if (order == null) {
                continue;
            }
            Serializable result;
            try {
                result = order.call();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            try {
                returnResult(work.getId(), result);
                ordersFilled++;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        public Serializable exec(HotpotatoServer restaurant) {
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

        public Serializable exec(HotpotatoServer restaurant) {
            restaurant.returnResult(id, result);
            return null;
        }
    }
}
