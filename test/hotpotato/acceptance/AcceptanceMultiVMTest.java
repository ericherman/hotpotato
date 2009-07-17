/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.io.ConnectionServer;
import hotpotato.model.Customer;
import hotpotato.model.ReturnStringOrder;
import hotpotato.util.NullPrintStream;
import hotpotato.util.Shell;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class AcceptanceMultiVMTest extends TestCase {

    // netstat -anpt | grep 16
    private int port;
    private int port2;
    private PrintStream out;
    private PrintStream err;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceMultiVMTest.class);
    }

    protected void setUp() throws Exception {
        port = tryForRandomPort();
        port2 = tryForRandomPort();
        out = new NullPrintStream();
        err = out;
    }

    private int tryForRandomPort() {
        return 16000 + (int) (1000 * Math.random());
    }

    protected void tearDown() {
        out = null;
        err = null;
    }

    public void test1Customer1Cook() throws Exception {
        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "1";
        String sandbox = Boolean.TRUE.toString();

        String[] restaurantArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ServerRunner.class.getName(), "" + port,
                maxSeconds, workUnits, };

        String[] cookArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, sandbox, };

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

    public void testTriangle() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream capture = new PrintStream(baos);

        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "0";
        String sandbox = Boolean.FALSE.toString();

        String[] hotpotatodArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ServerRunner.class.getName(), "" + port,
                maxSeconds, workUnits, };

        String[] workerArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, sandbox };

        String[] resenderArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ResendRunner.class.getName(), "" + port2,
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, };

        new Shell(hotpotatodArgs, envp, "hotpotatod", out, err).start();
        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);
        new Shell(workerArgs, envp, "workerd", out, err).start();
        new Shell(resenderArgs, envp, "resenderd", capture, capture).start();

        Socket s = null;
        try {
            s = getSocket(25, port2);
            OutputStream os = s.getOutputStream();
            os.write("Hello, World!".getBytes("UTF-8"));
        } finally {
            if (s != null) {
                s.close();
            }
        }

        List<String> expected = new ArrayList<String>();
        expected.add("'H'");
        expected.add("'e'");
        expected.add("'l'");
        expected.add("'l'");
        expected.add("'o'");
        expected.add("','");
        expected.add("' '");
        expected.add("'W'");
        expected.add("'o'");
        expected.add("'r'");
        expected.add("'l'");
        expected.add("'d'");
        expected.add("'!'");

        int found = 0;
        for (int i = 0; found < expected.size(); i++) {
            capture.flush();
            String captured = baos.toString();
            assertTrue("infinite loop " + captured, i < 200);
            found = 0;
            for (String single : expected) {
                if (captured.contains(single)) {
                    found++;
                }
            }
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        capture.flush();
        assertEquals(baos.toString(), expected.size(), found);
    }

    private Socket getSocket(int tries, int portNum) throws Exception {
        for (int i = 0; i < tries; i++) {
            try {
                return new Socket(InetAddress.getLocalHost(), portNum);
            } catch (ConnectException e) {
                Thread.sleep(100 * ConnectionServer.SLEEP_DELAY);
            }
        }
        return new Socket(InetAddress.getLocalHost(), portNum);
    }

}
