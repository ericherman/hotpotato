/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.model.*;

import java.net.*;

public class WorkerRunner {
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int maxLoops = maxWorkTimeSeconds * 4;
        int quota = Integer.parseInt(args[1]);
        InetAddress addr = InetAddress.getByName(args[2]);
        int port = Integer.parseInt(args[3]);

        Worker cook = new Worker(addr, port);
        new Thread(cook).start();

        for (int i = 0; i < maxLoops && cook.ordersFilled() < quota; i++) {
            Thread.sleep(250);
        }

        cook.shutdown();
    }
}
