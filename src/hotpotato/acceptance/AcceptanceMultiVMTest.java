/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ConnectionServer;
import hotpotato.model.Customer;
import hotpotato.testsupport.ReturnStringOrder;
import hotpotato.util.NullPrintStream;
import hotpotato.util.Shell;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class AcceptanceMultiVMTest extends TestCase {

    // netstat -anpt | grep 16
    private int port;
    private PrintStream out;
    private PrintStream err;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceMultiVMTest.class);
    }

    protected void setUp() throws Exception {
        port = 16000 + (int) (1000 * Math.random());
        out = new NullPrintStream();
        err = out;
    }

    protected void tearDown() {
        out.close();
        out = null;
        err = null;
    }

    public void test1Customer1Cook() throws Exception {
        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "1";

        String[] restaurantArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ServerRunner.class.getName(), "" + port,
                maxSeconds, workUnits, };

        String[] cookArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, };

        new Shell(restaurantArgs, envp, "Restaraunt", out, err).start();
        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);
        new Shell(cookArgs, envp, "cook", out, err).start();

        Customer bob = new Customer(InetAddress.getLocalHost(), port);

        Thread.sleep(15 * ConnectionServer.SLEEP_DELAY);

        Callable<Serializable> order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            assertTrue("infinite loop", i < 20);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals("fries", fries);
    }
}
