/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.model.*;

public class RestaurantRunner {
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int maxLoops = maxWorkTimeSeconds * 4;
        int quota = Integer.parseInt(args[1]);
        int placeOrdersPort = Integer.parseInt(args[2]);
        int ticketWheelPort = Integer.parseInt(args[3]);
        int counterTopPort = Integer.parseInt(args[4]);
        int pickUpOrdersPort = Integer.parseInt(args[5]);

        Restaurant alices =
            new Restaurant(
                placeOrdersPort,
                ticketWheelPort,
                counterTopPort,
                pickUpOrdersPort);

        for (int i = 0; i < maxLoops && alices.happyCustomers() < quota; i++) {
            Thread.sleep(250);
        }

        alices.shutdown();
    }
}
