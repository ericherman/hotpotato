/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato;

import java.io.*;

public class LocalRestaurantClient implements RestaurantClient {
    Restaurant restaurant;
    public LocalRestaurantClient(Restaurant alices) {
        this.restaurant = alices;
    }
    public Serializable send(Request request) {
        return request.exec(restaurant);
    }
}
