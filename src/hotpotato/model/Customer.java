/**
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

public class Customer {
    private RestaurantClient client;

    public Customer(InetAddress address, int orderPort) {
        this(new NetworkRestaurantClient(address, orderPort));
    }

    public Customer(RestaurantClient client) {
        this.client = client;
    }

    public String placeOrder(String prefix, final Order order)
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
        private Order order;
        private String id;
        public PlaceOrderRequest(String id, Order order) {
            this.order = order;
            this.id = id;
        }
        public Serializable exec(Restaurant restaurant) {
            return restaurant.takeOrder(id, order);
        }
    }

    public static class PickUpOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;
        public PickUpOrderRequest(String id) {
            this.id = id;
        }
        public Serializable exec(Restaurant restaurant) {
            return restaurant.pickUpOrder(id);
        }
    }

    public static class CancelOrderRequest implements Request {
        private static final long serialVersionUID = 1L;
        private String id;
        public CancelOrderRequest(String id) {
            this.id = id;
        }
        public Serializable exec(Restaurant restaurant) {
            return new Boolean(restaurant.getTicket(id) != null);
        }
    }
}
