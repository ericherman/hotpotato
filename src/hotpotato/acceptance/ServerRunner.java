/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.HotpotatoServer;
import hotpotato.model.Hotpotatod;
import hotpotato.net.SocketHotpotatoServer;

public class ServerRunner {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        int maxWorkTimeSeconds = 0;
        if (args.length > 1) {
            maxWorkTimeSeconds = Integer.parseInt(args[1]);
        }
        int quota = 0;
        if (args.length > 2) {
            quota = Integer.parseInt(args[2]);
        }

        HotpotatoServer alices = new Hotpotatod();
        SocketHotpotatoServer server = new SocketHotpotatoServer(port, alices);
        server.start();

        long start = System.currentTimeMillis();
        while (!done(alices, start, maxWorkTimeSeconds, quota)) {
            Thread.sleep(250);
        }

        server.shutdown();
    }

    private static boolean done(HotpotatoServer alices, long start,
            int maxWorkTimeSeconds, int quota) {

        if (maxWorkTimeSeconds > 0) {
            long end = start + (maxWorkTimeSeconds * 1000L);
            if (System.currentTimeMillis() > end) {
                return true;
            }
        }

        if (quota > 0) {
            if (alices.ordersDelivered() >= quota) {
                return true;
            }
        }

        return false;
    }

}
