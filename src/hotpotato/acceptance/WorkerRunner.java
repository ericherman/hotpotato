/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.model.Worker;

import java.net.InetAddress;

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
        boolean sandbox = true;
        if (args.length > 4) {
            sandbox = args[4].equalsIgnoreCase(Boolean.TRUE.toString());
        }

        Worker worker = new Worker(addr, port, sandbox);
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
