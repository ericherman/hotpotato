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
        InetAddress addr = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        int maxWorkTimeSeconds = 0;
        if (args.length > 2) {
        	maxWorkTimeSeconds = Integer.parseInt(args[2]);
        }
        int quota = 0;
        if (args.length > 3) {
        	quota = Integer.parseInt(args[3]);
        }

        Worker worker = new Worker(addr, port);
        new Thread(worker).start();

        long start = System.currentTimeMillis();
        while (!done(worker, start, maxWorkTimeSeconds, quota)) {
            Thread.sleep(250);
        }

        worker.shutdown();
    }

	private static boolean done(Worker worker, long start,
			int maxWorkTimeSeconds, int quota) {

		if (maxWorkTimeSeconds > 0) {
			long end = start + (maxWorkTimeSeconds * 1000L);
			if (System.currentTimeMillis() > end) {
				return true;
			}
		}

		if (quota > 0) {
			if (worker.ordersFilled() >= quota) {
				return true;
			}
		}

		return false;
	}

}
