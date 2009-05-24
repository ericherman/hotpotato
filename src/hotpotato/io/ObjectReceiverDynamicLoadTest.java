/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.*;
import hotpotato.net.*;

import java.io.*;
import java.net.*;

public class ObjectReceiverDynamicLoadTest extends DynamicClassLoadFixture {
    private File passwd;
    private Socket s;
    private SocketHotpotatoServer ensureClassLoaded;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ObjectReceiverDynamicLoadTest.class);
    }

    protected void tearDown() throws Exception {
        if (passwd != null) {
            passwd.delete();
        }
        passwd = null;
        if (s != null) {
            s.close();
        }
        s = null;
        if (ensureClassLoaded != null) {
            ensureClassLoaded.shutdown();
        }
        try {
            super.tearDown();
        } catch (Exception e) { //
        }
    }

    private Serializable receiveSerializable(String shortClassName,
            String[] alien_java_src) throws Exception {

        compileAlienClass(shortClassName, alien_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s).receive();
        s.close();
        s = null;
        assertNotNull(alienOrderItem);
        return alienOrderItem;
    }

    private Order receiveOrder(String shortClassName, String[] alien_java_src)
            throws Exception {

        Serializable obj = receiveSerializable(shortClassName, alien_java_src);
        String msg = obj.getClass() + " should be an " + Order.class;
        assertTrue(msg, obj instanceof Order);

        return (Order) obj;
    }

    public void testRecieveAlienTransmission() throws Exception {
        String shortName = "Alien0";
        String[] alien_java_src = new String[]{"package aliens;", //
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
        String[] alien_java_src = new String[]{"package aliens;", //
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
        String[] alien_java_src = new String[]{"package aliens;", //
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
        String[] alien_java_src = new String[]{"package aliens;", //
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
        writeFile(passwd, new String[]{"Shh ... it's secret."});
        String path = passwd.getPath();

        String shortName = "Alien4";
        String[] alien_java_src = new String[]{"package aliens;", //
                "import java.io.*;", //
                "import hotpotato.*;", //
                "import hotpotato.util.*;", //
                "public class " + shortName + " implements Order {", //
                "    public Serializable exec() {", //
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
        String[] alien_java_src = new String[]{"package aliens;", //
                "import java.io.*;", //
                "import hotpotato.*;", //
                "import hotpotato.net.*;", //
                "public class " + shortName + " implements Order {", //
                "    public Serializable exec() {", //
                "        SocketHotpotatoServer res = new SocketHotpotatoServer(0);", //
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

        ensureClassLoaded = new SocketHotpotatoServer(0);
        ensureClassLoaded.start();

        recieveOrderWithSecurityViolation(shortName, alien_java_src);
    }

    public void testSecurityViolationPropertyRead() throws Exception {
        String shortClassName = "Alien6";
        String[] alien_java_src = new String[]{"package aliens;", //
                "import java.io.*;", //
                "import hotpotato.*;", //
                "import hotpotato.net.*;", //
                "public class " + shortClassName + " implements Order {", //
                "    public Serializable exec() {", //
                "        return System.getProperty(\"java.class.path\");", //
                "    }", //
                "}", //
        };

        recieveOrderWithSecurityViolation(shortClassName, alien_java_src);
    }

    private void recieveOrderWithSecurityViolation(String shortClassName,
            String[] alien_java_src) throws Exception {

        Order command = receiveOrder(shortClassName, alien_java_src);
        SecurityException expected = null;
        try {
            command.exec();
        } catch (SecurityException e) {
            expected = e;
        }
        assertNotNull("Should have thrown SecurityException", expected);
    }
}
