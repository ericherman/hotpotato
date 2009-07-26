/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.model.Customer;

import java.io.IOException;
import java.net.InetAddress;

public class ResendRunner {

    public static class CustomerBisector implements ResendServer.Sender {
        private InetAddress host;
        private int port;
        private InetAddress resendHost;
        private int resendPort;
        private String lastLine;

        public CustomerBisector(InetAddress host, int port,
                InetAddress resendHost, int resendPort) {
            this.host = host;
            this.port = port;
            this.resendHost = resendHost;
            this.resendPort = resendPort;
        }

        public boolean send(String line) {
            lastLine = null;
            if (line == null || line.length() < 2) {
                return false;
            }
            Customer customer = new Customer(host, port);
            BisectJob runnable = new BisectJob(line, resendHost, resendPort);
            customer.execute(runnable);
            return true;
        }

        public String toString() {
            return CustomerBisector.class.getSimpleName() + " " + lastLine;
        }

        public void setResendPort(int resendPort) {
            this.resendPort = resendPort;
        }
    }

    public static void main(String[] args) throws Exception {
        InetAddress resendHost = InetAddress.getLocalHost();
        int resendPort = parseIntArg(args, 0);
        InetAddress hpHost = parseInetAddressArg(args, 1);
        int hpPort = parseIntArg(args, 2);
        int maxRunTimeSeconds = parseIntArg(args, 3);
        int quota = parseIntArg(args, 4);

        ResendServer.Sender sender = new CustomerBisector(hpHost, hpPort,
                resendHost, resendPort);

        ResendServer server = new ResendServer(resendPort, sender);

        server.start();
        while (resendPort == 0) {
            resendPort = server.getPort();
            if (resendPort != 0) {
                ((CustomerBisector) sender).setResendPort(resendPort);
            }

        }

        long start = System.currentTimeMillis();
        while (!done(server, start, maxRunTimeSeconds, quota)) {
            Thread.sleep(250);
        }

        server.shutdown();
    }

    private static InetAddress parseInetAddressArg(String[] args, int i)
            throws IOException {
        if (args.length <= i) {
            return InetAddress.getLocalHost();
        }
        return InetAddress.getByName(args[i]);
    }

    private static int parseIntArg(String[] args, int i) {
        return (args.length <= i) ? 0 : Integer.parseInt(args[i]);
    }

    private static boolean done(ResendServer server, long start,
            int maxRunTimeSeconds, int quota) {

        if (maxRunTimeSeconds > 0) {
            long end = start + (maxRunTimeSeconds * 1000L);
            if (System.currentTimeMillis() > end) {
                return true;
            }
        }

        if (quota > 0) {
            if (server.stringsRecieved() >= quota) {
                return true;
            }
        }
        return false;
    }

}
