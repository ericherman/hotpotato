/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.model.*;

import java.net.*;

public class CookRunner {
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int maxLoops = maxWorkTimeSeconds * 4;
        int quota = Integer.parseInt(args[1]);
        InetAddress addr = InetAddress.getByName(args[2]);
        int ticketWheelPort = Integer.parseInt(args[3]);
        int counterTopPort = Integer.parseInt(args[4]);

        Cook cook = new Cook();
        cook.setKitchen(addr, ticketWheelPort, counterTopPort);

        for (int i = 0; i < maxLoops && cook.ordersFilled() < quota; i++) {
            Thread.sleep(250);
        }

        cook.reset();
    }
}
