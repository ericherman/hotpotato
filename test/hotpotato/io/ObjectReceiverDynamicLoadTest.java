/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.NullPrintStream;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ObjectReceiverDynamicLoadTest extends DynamicClassLoadTestFixture {
    private File passwd;
    private Socket s;
    private ReverseStringServer ensureClassLoaded;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ObjectReceiverDynamicLoadTest.class);
    }

    protected void tearDown() throws Exception {
        if (passwd != null) {
            passwd.delete();
        }
        if (s != null) {
            s.close();
        }
        if (ensureClassLoaded != null) {
            ensureClassLoaded.shutdown();
        }
        passwd = null;
        s = null;
        ensureClassLoaded = null;
        try {
            super.tearDown();
        } catch (Exception e) { //
        }
    }

    private Serializable receiveSerializable(String shortClassName,
            String[] alien_java_src) throws Exception {
        return receiveSerializable(shortClassName, alien_java_src, true);
    }

    private Serializable receiveSerializable(String shortClassName,
            String[] alien_java_src, boolean sandbox) throws Exception {

        compileAlienClass(shortClassName, alien_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s, sandbox).receive();
        s.close();
        s = null;
        assertNotNull(alienOrderItem);
        return alienOrderItem;
    }

    private Callable<Serializable> receiveOrder(String shortClassName,
            String[] alien_java_src) throws Exception {
        return receiveOrder(shortClassName, alien_java_src, true);
    }

    @SuppressWarnings("unchecked")
    private Callable<Serializable> receiveOrder(String shortClassName,
            String[] alien_java_src, boolean sandbox) throws Exception {

        Serializable obj = receiveSerializable(shortClassName, alien_java_src,
                sandbox);
        String msg = obj.getClass() + " should be an " + Callable.class;
        assertTrue(msg, obj instanceof Callable);

        return (Callable<Serializable>) obj;
    }

    public void testRecieveAlienTransmission() throws Exception {
        String shortName = "Alien0";
        String[] alien_java_src = new String[] { "package aliens;", //
                "import java.io.*;", //
                "import hotpotato.io.*;", //
                "public class " + shortName + " implements Serializable {", //
                "    public String data = \"foo\";", //
                "}", //
        };

        receiveSerializable(shortName, alien_java_src);
    }

    public void testRecieveMultiClassAlienTransmission() throws Exception {
        String shortName = "Alien1";
        String[] alien_java_src = new String[] { "package aliens;", //
                "import java.io.*;", //
                "import hotpotato.io.*;", //
                "public class " + shortName + " implements Serializable {", //
                "    public static class Foo implements Serializable {", //
                "        public String data = \"foo\";", //
                "    }", //
                "    public Foo foo = new Foo();", //
                "}", //
        };

        receiveSerializable(shortName, alien_java_src);
    }

    public void testReceiveAlienClassArray() throws Exception {
        String shortName = "Alien2";
        String[] alien_java_src = new String[] { "package aliens;", //
                "import java.io.*;", //
                "import hotpotato.io.*;", //
                "public class " + shortName + " implements Serializable {", //
                "    public static class Foo implements Serializable {", //
                "        public String data = \"foo\";", //
                "    }", //
                "    public Foo[] foo = new Foo[] {new Foo()};", //
                "}", //
        };

        receiveSerializable(shortName, alien_java_src);
    }

    public void testAlienAnonymousInner() throws Exception {
        String shortName = "Alien3";
        String[] alien_java_src = new String[] { "package aliens;", //
                "import java.io.*;", //
                "import hotpotato.io.*;", //
                "public class " + shortName + " implements Serializable {", //
                "    public Serializable getSer() {", //
                "        return new Serializable(){", //
                "            public String toString() {", //
                "                return \"anonymous inner\";", //
                "            }", //
                "        };", //
                "    }", //
                "}", //
        };

        receiveSerializable(shortName, alien_java_src);
    }

    public void testSecurityViolationFileRead() throws Exception {
        passwd = new File(testDir, "faux.passwd");
        writeFile(passwd, new String[] { "Shh ... it's secret." });
        String path = passwd.getPath();

        String shortName = "Alien4";
        String[] alien_java_src = new String[] {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "import hotpotato.*;", //
                "import hotpotato.util.*;", //
                "public class " + shortName
                        + " implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "      try {", //
                "        File passwd = new File(\"" + path + "\");", //
                "        if (!passwd.exists()) {", //
                "            String msg = \"no file: \" + passwd;", //
                "            throw new RuntimeException(msg);", //
                "        }", //
                "        FileInputStream fis = new FileInputStream(passwd);", //
                "        byte[] bytes = new Streams().readBytes(fis);", //
                "        return new String(bytes);", //
                "      } catch(IOException e) {", //
                "          throw new RuntimeException(e);", //
                "      }", //
                "    }", //
                "}", //
        };

        recieveOrderWithSecurityViolation(shortName, alien_java_src);
    }

    public void testSecurityViolationSocketOpen() throws Exception {
        String shortName = "Alien5";
        String[] alien_java_src = new String[] {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.*;", //
                "import hotpotato.*;", //
                "import hotpotato.io.*;", //
                "import hotpotato.net.*;", //
                "import hotpotato.util.*;", //
                "public class " + shortName
                        + " implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        PrintStream p = new NullPrintStream();", //
                "        ReverseStringServer res = new ReverseStringServer(0, p);", //
                "        try {", //
                "            res.start();", //
                "        } catch (IOException e) {", //
                "            throw new RuntimeException(e);", //
                "        }", //
                "        return res.getInetAddress().toString()", //
                "            + \":\" + res.getPort();", //
                "    }", //
                "}", //
        };

        PrintStream devNull = new NullPrintStream();
        ensureClassLoaded = new ReverseStringServer(0, devNull);
        ensureClassLoaded.start();

        recieveOrderWithSecurityViolation(shortName, alien_java_src);
        devNull.close();
    }

    public void testSecurityViolationPropertyRead() throws Exception {
        String shortClassName = "Alien6";
        String[] alien_java_src = new String[] {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "import hotpotato.*;", //
                "import hotpotato.net.*;", //
                "public class " + shortClassName
                        + " implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        return System.getProperty(\"java.class.path\");", //
                "    }", //
                "}", //
        };

        recieveOrderWithSecurityViolation(shortClassName, alien_java_src);
    }

    private void recieveOrderWithSecurityViolation(String shortClassName,
            String[] alien_java_src) throws Exception {

        Callable<Serializable> command = receiveOrder(shortClassName,
                alien_java_src);
        SecurityException expected = null;
        try {
            command.call();
        } catch (SecurityException e) {
            expected = e;
        }
        assertNotNull("Should have thrown SecurityException", expected);
    }

    public void testNoSanboxPropertyRead() throws Exception {
        String shortClassName = "Alien6";
        String[] alien_java_src = new String[] {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.*;", //
                "import hotpotato.*;", //
                "import hotpotato.net.*;", //
                "public class " + shortClassName
                        + " implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        return System.getProperty(\"java.class.path\");", //
                "    }", //
                "}", //
        };

        boolean sandbox = false;
        Callable<Serializable> command = receiveOrder(shortClassName,
                alien_java_src, sandbox);
        Serializable obj = command.call();
        String expected = System.getProperty("java.class.path");
        assertEquals(expected, obj);
    }

}
