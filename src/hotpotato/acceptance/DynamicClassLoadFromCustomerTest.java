/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import hotpotato.*;
import hotpotato.io.*;
import hotpotato.model.*;
import hotpotato.net.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

public class DynamicClassLoadFromCustomerTest extends DynamicClassLoadFixture {
    private SocketHotpotatoServer server;

    protected void tearDown() throws Exception {
        if (server != null)
            server.shutdown();

        super.tearDown();
    }

    public void testRoundTripAlienOrder() throws Exception {
        String[] source = {"package aliens;", //
                "import java.io.*;", //
                "import hotpotato.*;", //
                "public class Alien implements Order {", //
                "    public Serializable exec() {", //
                "        return \"Alien\";", //
                "    }", //
                "}", //
        };
        roundTrip("Alien", source);
    }

    public void testComplexAlienOrder() throws Exception {
        String[] source1 = {"package aliens;", //
                "public class AlienChild {", //
                "    public int foo = 0;", //
                "    public int getRand() {", //
                "        return (int) (10 * Math.random());", //
                "    }", //
                "}", //
        };

        compileAlienClass("AlienChild", source1);

        String[] source2 = {"package aliens;", //
                "import hotpotato.*;", //
                "import java.io.*;", //
                "public class ComplexAlien implements Order {", //
                "    public Serializable exec() {", //
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
        server = new SocketHotpotatoServer(0, alices);
        server.start();

        String maxSeconds = "10";
        String workUnits = "1";

        String[] cookArgs = new String[]{"java", "-cp", CLASSPATH,
                WorkerRunner.class.getName(), maxSeconds, workUnits,
                InetAddress.getLocalHost().getHostName(),
                "" + server.getPort(),};

        new Shell(cookArgs, ENVP, "cook").start();

        String javaProgram = CustomerRunner.class.getName();
        assertEquals("hotpotato.acceptance.CustomerRunner", javaProgram);

        ServerSocket reportingServer = new ServerSocket(0);

        // System.out.println(CLASSPATH);
        // System.out.println(alienClasspath);
        String[] args = new String[]{"java", "-cp", alienClasspath,
                javaProgram, maxSeconds, "" + server.getPort(), className,
                "" + reportingServer.getLocalPort()};

        launched = new Shell(args, ENVP, "send alien");
        launched.start();

        Socket s = reportingServer.accept();
        Serializable obj = new ObjectReceiver(s).receive();
        s.close();
        assertEquals(shortClassName, obj);
        assertEquals(1, alices.ordersDelivered());
    }
}
