/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.HotpotatoServer;
import hotpotato.io.DynamicClassLoadTestFixture;
import hotpotato.io.ObjectReceiver;
import hotpotato.model.Hotpotatod;
import hotpotato.net.SocketHotpotatoServer;
import hotpotato.util.Shell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DynamicClassLoadFromCustomerTest extends
        DynamicClassLoadTestFixture {

    private SocketHotpotatoServer server;

    private ByteArrayOutputStream baos1;
    private PrintStream ps1;

    protected void setUp() throws Exception {
        super.setUp();
        baos1 = new ByteArrayOutputStream();
        ps1 = new PrintStream(baos1);
    }

    protected void tearDown() throws Exception {
        try {
            ps1.close();
            ps1 = null;
            baos1 = null;
            if (server != null) {
                server.shutdown();
                server = null;
            }
        } finally {
            super.tearDown();
        }
    }


    public void x_testRoundTripAlienOrder() throws Exception {
        String[] source = {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "public class Alien implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        return \"Alien\";", //
                "    }", //
                "}", //
        };
        roundTrip("Alien", source);
    }

    public void testComplexAlienOrder() throws Exception {
        String[] source1 = { "package aliens;", //
                "public class AlienChild {", //
                "    public int foo = 0;", //
                "    public int getRand() {", //
                "        return (int) (10 * Math.random());", //
                "    }", //
                "}", //
        };

        compileAlienClass("AlienChild", source1);

        String[] source2 = {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "public class ComplexAlien implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        AlienChild child = new AlienChild() {", //
                "            public int getRand() {", //
                "                return super.getRand() + 20;", //
                "            }", //
                "        };", //
                "        if (child.getRand() > child.foo){", //
                "            return \"ComplexAlien\";", //
                "        }", //
                "        return null;", //
                "    }", //
                "}", //
        };
        roundTrip("ComplexAlien", source2);
    }

    private void roundTrip(String shortClassName, String[] source)
            throws Exception {

        compileAlienClass(shortClassName, source);
        String className = "aliens." + shortClassName;

        HotpotatoServer alices = new Hotpotatod();
        server = new SocketHotpotatoServer(0, alices, ps1);
        server.start();

        String maxSeconds = "10";
        String workUnits = "1";

        String[] cookArgs = new String[] { "java", "-cp", CLASSPATH,
                WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(),
                "" + server.getPort(), maxSeconds, workUnits, };

        new Shell(cookArgs, ENVP, "cook", out, err).start();

        String javaProgram = CustomerRunner.class.getName();
        assertEquals("hotpotato.acceptance.CustomerRunner", javaProgram);

        ServerSocket reportingServer = new ServerSocket(0);

        // System.out.println(CLASSPATH);
        // System.out.println(alienClasspath);
        String[] args = new String[] { "java", "-cp", alienClasspath,
                javaProgram, maxSeconds, "" + server.getPort(), className,
                "" + reportingServer.getLocalPort() };

        launched = new Shell(args, ENVP, "send alien", out, err);
        launched.start();

        Socket s = reportingServer.accept();
        Serializable obj = new ObjectReceiver(s).receive();
        s.close();
        assertEquals(shortClassName, obj);
        assertEquals(1, alices.ordersDelivered());
    }
}
