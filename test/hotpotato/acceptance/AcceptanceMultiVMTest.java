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
import hotpotato.util.Shell;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringReader;
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
    private ByteArrayOutputStream baos1;
    private PrintStream ps1;
    private ByteArrayOutputStream baos2;
    private PrintStream ps2;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceMultiVMTest.class);
    }

    protected void setUp() throws Exception {
        baos1 = new ByteArrayOutputStream();
        ps1 = new PrintStream(baos1);
        baos2 = new ByteArrayOutputStream();
        ps2 = new PrintStream(baos2);
    }

    protected void tearDown() {
        ps1.close();
        ps1 = null;
        ps2.close();
        ps2 = null;
        baos1 = null;
        baos2 = null;
    }

    public void test1Customer1Cook() throws Exception {
        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "1";
        String sandbox = Boolean.TRUE.toString();

        String[] restaurantArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ServerRunner.class.getName(), "" + 0,
                maxSeconds, workUnits, };

        new Shell(restaurantArgs, envp, "Restaraunt", ps1, ps1).start();
        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);
        port = parseInt(ps1, baos1, 10);

        String[] cookArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, sandbox, };

        new Shell(cookArgs, envp, "cook", ps2, ps2).start();

        Customer bob = new Customer(InetAddress.getLocalHost(), port);

        Thread.sleep(15 * ConnectionServer.SLEEP_DELAY);

        Callable<String> order = new ReturnStringOrder("fries");
        String orderNumber = bob.placeOrder("bob", order);

        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);

        Serializable fries = null;
        for (int i = 0; fries == null; i++) {
            String msg = "";
            if (i >= 20) {
                ps1.flush();
                ps2.flush();
                msg = msg + "\n parsed port = " + port;
                msg = msg + "\n" + baos1.toString();
                msg = msg + "\n" + baos2.toString();
            }
            assertTrue("infinite loop" + msg, i < 20);
            fries = bob.pickupOrder(orderNumber);
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        assertEquals("fries", fries);
    }

    private int parseInt(PrintStream ps, ByteArrayOutputStream baos, int tries)
            throws IOException, InterruptedException {
        String received = null;
        for (int i = 0; i < tries; i++) {
            ps.flush();
            received = baos.toString();
            int p = parseInt(received);
            if (p > 0) {
                return p;
            }
            Thread.sleep(ConnectionServer.SLEEP_DELAY * (i + 1));
        }
        throw new RuntimeException("Could not parse port from:\n" + received);
    }

    private int parseInt(String received) throws IOException {
        StringReader in = new StringReader(received);
        BufferedReader reader = new BufferedReader(in);
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.contains(": ")) {
                int index = line.indexOf(": ");
                return Integer.parseInt(line.substring(index + 2));
            }
        }
        return 0;
    }

    public void testTriangle() throws Exception {
        ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
        PrintStream ps3 = new PrintStream(baos3);

        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "5";
        String workUnits = "0";
        String sandbox = Boolean.FALSE.toString();

        String[] hotpotatodArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ServerRunner.class.getName(), "" + 0,
                maxSeconds, workUnits, };

        new Shell(hotpotatodArgs, envp, "hotpotatod", ps1, ps1).start();
        Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);
        port = parseInt(ps1, baos1, 10);

        String[] resenderArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.ResendRunner.class.getName(), "" + 0,
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, };
        new Shell(resenderArgs, envp, "resenderd", ps3, ps3).start();
        port2 = parseInt(ps3, baos3, 10);

        String[] workerArgs = new String[] { "java", "-cp", classpath,
                hotpotato.acceptance.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, sandbox };
        new Shell(workerArgs, envp, "workerd", ps2, ps2).start();

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
            ps3.flush();
            String captured = baos3.toString();
            String msg = "";
            if (i >= 200) {
                ps1.flush();
                ps2.flush();
                msg = msg + "\n parsed port = " + port;
                msg = msg + "\n parsed port2 = " + port2;
                msg = msg + "\n" + baos1.toString();
                msg = msg + "\n" + baos2.toString();
                msg = msg + "\n" + baos3.toString();
            }

            assertTrue("infinite loop " + msg, i < 200);
            found = 0;
            for (String single : expected) {
                if (captured.contains(single)) {
                    found++;
                }
            }
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        ps3.flush();
        assertEquals(baos3.toString(), expected.size(), found);
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
