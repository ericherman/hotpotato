/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.io.*;
import hotpotato.model.*;

import java.io.*;
import java.net.*;

public class RestaurantServer extends ConnectionServer {
    private Restaurant restaurant;

    public RestaurantServer(int port) {
        this(port, new AlicesRestaurant());
    }

    public RestaurantServer(int port, Restaurant restaurant) {
        super(port, "Restaurant");
        this.restaurant = restaurant;
    }

    protected void acceptConnection(Socket s) throws IOException {
        ObjectReceiver receiver = new ObjectReceiver(s);
        Serializable request = receiver.receive();
        Serializable reply;
        try {
            reply = ((Request) request).exec(restaurant);
        } catch (Exception e) {
            reply = e.toString();
        }
        new ObjectSender(s).send(reply);
    }
}