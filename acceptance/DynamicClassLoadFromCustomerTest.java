/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.acceptance;

import hotpotato.io.*;
import hotpotato.model.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

public class DynamicClassLoadFromCustomerTest extends DynamicClassLoadFixture {
    // netstat -anp | grep 666
    private static final int PLACE_ORDERS = 6666;
    private static final int PICK_UP = 6667;
    private static final int TICKET_WHEEL = 6668;
    private static final int COUNTER_TOP = 6669;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(DynamicClassLoadFromCustomerTest.class);
    }

    private Restaurant alices;

    protected void tearDown() throws Exception {
        if (alices != null)
            alices.shutdown();

        Threads.pause(10000);

        super.tearDown();
    }

    public void testRoundTripAlienOrder() throws Exception {
        String source =
            "package aliens;"
                + "import hotpotato.model.*;"
                + "public class Alien extends NamedOrder {"
                + "    public Alien() {"
                + "        super(\"Alien\");"
                + "    }"
                + "}";
        roundTrip("Alien", source);
    }

    public void testComplexAlienOrder() throws Exception {
        String source1 =
            "package aliens;"
                + "public class AlienChild {"
                + "    public int foo = 0;"
                + "    public int getRand() {"
                + "        return (int) (10 * Math.random());"
                + "    }"
                + "}";

        compileAlienClass("AlienChild", source1);

        String source2 =
            "package aliens;"
                + "import hotpotato.model.*;"
                + "public class ComplexAlien extends NamedOrder {"
                + "    public ComplexAlien() {"
                + "        super(\"ComplexAlien\");"
                + "    }"
                + "    public void exec() {"
                + "        AlienChild child = new AlienChild() {"
                + "            public int getRand() {"
                + "                return super.getRand() + 20;"
                + "            }"
                + "        };"
                + "        if (child.getRand() > child.foo){"
                + "            super.exec();"
                + "        }"
                + "    }"
                + "}";
        roundTrip("ComplexAlien", source2);
    }

    private void roundTrip(String shortClassName, String source)
        throws Exception, IOException, UnknownHostException {
        compileAlienClass(shortClassName, source);
        String className = "aliens." + shortClassName;

        alices =
            new Restaurant(PLACE_ORDERS, TICKET_WHEEL, COUNTER_TOP, PICK_UP);
        Threads.pause();

        String maxSeconds = "10";
        String workUnits = "1";

        String[] cookArgs =
            new String[] {
                "java",
                "-cp",
                CLASSPATH,
                CookRunner.class.getName(),
                maxSeconds,
                workUnits,
                InetAddress.getLocalHost().getHostName(),
                "" + TICKET_WHEEL,
                "" + COUNTER_TOP,
                };

        Threads.launch("cook", cookArgs, ENVP);

        String javaProgram = CustomerRunner.class.getName();
        assertEquals("hotpotato.acceptance.CustomerRunner", javaProgram);

        ServerSocket server = new ServerSocket(0);

        String[] args =
            new String[] {
                "java",
                "-cp",
                alienClasspath,
                javaProgram,
                maxSeconds,
                "" + PLACE_ORDERS,
                "" + PICK_UP,
                className,
                "" + server.getLocalPort()};

        launched = Threads.launch("send alien", args, ENVP);
        Socket s = server.accept();
        Boolean success = (Boolean) new ObjectReceiver(s).receive();
        s.close();

        assertEquals(Boolean.TRUE, success);
        assertEquals(1, alices.happyCustomers());
    }
}
