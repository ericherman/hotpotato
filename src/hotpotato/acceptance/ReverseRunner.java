/**
 * Copyright (C) 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.model.Customer;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class ReverseRunner {

    private InetAddress host;
    private int port;
    private int maxWorkTimeSeconds;
    private PrintStream out;

    public ReverseRunner(InetAddress host, int port) {
        this.host = host;
        this.port = port;
        out = System.out;
        maxWorkTimeSeconds = Integer.MAX_VALUE;
    }

    public void setMaxWorkTimeSeconds(int maxWorkTimeSeconds) {
        this.maxWorkTimeSeconds = maxWorkTimeSeconds;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void reverse(String message) throws Exception {
        Customer us = new Customer(host, port);
        Callable<Serializable> order = new ReverseOrder(message);
        String orderNumber = us.placeOrder("foo", order);
        Serializable completedWork = null;

        long start = System.currentTimeMillis();
        long end = start + (maxWorkTimeSeconds * 1000L);
        while (completedWork == null && System.currentTimeMillis() < end) {
            Thread.sleep(250);
            completedWork = us.pickupOrder(orderNumber);
        }

        out.println(completedWork);
        out.flush();
    }

    public static void main(String[] args) throws Exception {
        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        ReverseRunner runner = new ReverseRunner(host, port);
        runner.setOut(System.out);

        if (args.length > 3) {
            runner.setMaxWorkTimeSeconds(Integer.parseInt(args[3]));
        }

        String message = args[2];
        runner.reverse(message);
    }

}
