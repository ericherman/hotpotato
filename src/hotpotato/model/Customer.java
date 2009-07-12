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

public class Customer {
    private HotpotatoClient client;

    public Customer(InetAddress address, int orderPort) {
        this(new NetworkHotpotatoClient(address, orderPort));
    }

    public Customer(HotpotatoClient client) {
        this.client = client;
    }

    public String placeOrder(String prefix, final Callable<Serializable> order)
            throws IOException {
        return "" + client.send(new PlaceOrderRequest(prefix, order));
    }

    public Serializable pickupOrder(String id) throws IOException {
        return client.send(new PickUpOrderRequest(id));
    }

    public Boolean cancelOrder(final String id) throws IOException {
        return (Boolean) client.send(new CancelOrderRequest(id));
    }

    public static class PlaceOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private Callable<Serializable> order;
        private String id;

        public PlaceOrderRequest(String id, Callable<Serializable> order) {
            this.order = order;
            this.id = id;
        }

        public Serializable exec(HotpotatoServer restaurant) {
            return restaurant.takeOrder(id, order);
        }
    }

    public static class PickUpOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;

        public PickUpOrderRequest(String id) {
            this.id = id;
        }

        public Serializable exec(HotpotatoServer restaurant) {
            return restaurant.pickUpOrder(id);
        }
    }

    public static class CancelOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;

        public CancelOrderRequest(String id) {
            this.id = id;
        }

        public Serializable exec(HotpotatoServer restaurant) {
            return new Boolean(restaurant.getTicket(id) != null);
        }
    }
}
