/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.acceptance;

import hotpotato.*;
import hotpotato.model.*;
import hotpotato.net.*;

public class RestaurantRunner {
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int quota = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[2]);

        int MAX = maxWorkTimeSeconds * 4;

        Restaurant alices = new AlicesRestaurant();
        RestaurantServer server = new RestaurantServer(port, alices);
        server.start();

        for (int i = 0; (i < MAX) && (alices.ordersDelivered() < quota); i++) {
            Thread.sleep(250);
        }

        server.shutdown();
    }
}
