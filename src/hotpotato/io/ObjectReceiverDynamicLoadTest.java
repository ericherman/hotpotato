/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import hotpotato.*;
import hotpotato.net.*;

import java.io.*;
import java.net.*;

public class ObjectReceiverDynamicLoadTest extends DynamicClassLoadFixture {
    private File passwd;

    protected void tearDown() throws Exception {
        try {
            if (passwd != null)
                passwd.delete();
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
        Socket s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(alienOrderItem);
        return alienOrderItem;
    }

    private Order receiveOrder(String shortClassName, String[] alien_java_src)
            throws Exception, IOException, UnknownHostException {

        Serializable obj = receiveSerializable(shortClassName, alien_java_src);
        assertTrue(obj.getClass() + " should be an " + Order.class,
                obj instanceof Order);

        return (Order) obj;
    }

    public void testRecieveAlienTransmission() throws Exception {
        String shortClassName = "Alien0";
        String[] alien_java_src = new String[]{
                "package aliens;",
                "import java.io.*;",
                "import hotpotato.io.*;",
                "public class " + shortClassName + " implements Serializable {",
                "    public String data = \"foo\";", "}",};

        receiveSerializable(shortClassName, alien_java_src);
    }

    public void testRecieveMultiClassAlienTransmission() throws Exception {
        String shortClassName = "Alien1";
        String[] alien_java_src = new String[]{
                "package aliens;",
                "import java.io.*;",
                "import hotpotato.io.*;",
                "public class " + shortClassName + " implements Serializable {",
                "    public static class Foo implements Serializable {",
                "        public String data = \"foo\";", "    }",
                "    public Foo foo = new Foo();", "}",};

        receiveSerializable(shortClassName, alien_java_src);
    }

    public void testReceiveAlienClassArray() throws Exception {
        String shortClassName = "Alien2";
        String[] alien_java_src = new String[]{
                "package aliens;",
                "import java.io.*;",
                "import hotpotato.io.*;",
                "public class " + shortClassName + " implements Serializable {",
                "    public static class Foo implements Serializable {",
                "        public String data = \"foo\";", "    }",
                "    public Foo[] foo = new Foo[] {new Foo()};", "}",};

        receiveSerializable(shortClassName, alien_java_src);
    }

    public void testAlienAnonymousInner() throws Exception {
        String shortClassName = "Alien3";
        String[] alien_java_src = new String[]{
                "package aliens;",
                "import java.io.*;",
                "import hotpotato.io.*;",
                "public class " + shortClassName + " implements Serializable {",
                "    public Serializable getSer() {",
                "        return new Serializable(){",
                "            public String toString() {",
                "                return \"anonymous inner\";", "            }",
                "        };", "    }", "}",};

        receiveSerializable(shortClassName, alien_java_src);
    }

    public void testSecurityViolationFileRead() throws Exception {
        passwd = new File(testDir, "faux.passwd");
        writeFile(passwd, new String[]{"Shh ... it's secret."});
        String path = passwd.getPath();

        String shortClassName = "Alien4";
        String[] alien_java_src = new String[]{"package aliens;",
                "import java.io.*;", "import hotpotato.*;",
                "import hotpotato.util.*;",
                "public class " + shortClassName + " implements Order {",
                "    public Serializable exec() {", "      try {",
                "        File passwd = new File(\"" + path + "\");",
                "        if (!passwd.exists()) {",
                "            String msg = \"no file: \" + passwd;",
                "            throw new RuntimeException(msg);", "        }",
                "        FileInputStream fis = new FileInputStream(passwd);",
                "        byte[] bytes = new Streams().readBytes(fis);",
                "        return new String(bytes);",
                "      } catch(IOException e) {",
                "          throw new RuntimeException(e);", "      }", "    }",
                "}",};

        Order command = receiveOrder(shortClassName, alien_java_src);
        try {
            command.exec();
            fail("Violation should have thrown SecurityException");
        } catch (SecurityException e) { //
        }
    }

    public void testSecurityViolationSocketOpen() throws Exception {
        String shortClassName = "Alien5";
        String[] alien_java_src = new String[]{
                "package aliens;",
                "import java.io.*;",
                "import hotpotato.*;",
                "import hotpotato.net.*;",
                "public class " + shortClassName + " implements Order {",
                "    public Serializable exec() {",
                "        RestaurantServer sysLoaded = new RestaurantServer(0);",
                "        try {", "            sysLoaded.start();",
                "        } catch (IOException e){",
                "            throw new RuntimeException(e);", "        }",
                "        return sysLoaded.getInetAddress().toString()",
                "            + \":\" + sysLoaded.getPort();", "    }", "}",};

        RestaurantServer ensureClassLoaded = new RestaurantServer(0);
        ensureClassLoaded.start();

        Order command = receiveOrder(shortClassName, alien_java_src);
        try {
            command.exec();
            fail("Violation should have thrown SecurityException");
        } catch (SecurityException e) { // 
        }
    }

    public void testSecurityViolationPropertyRead() throws Exception {
        String shortClassName = "Alien6";
        String[] alien_java_src = new String[]{"package aliens;",
                "import java.io.*;", "import hotpotato.*;",
                "import hotpotato.net.*;",
                "public class " + shortClassName + " implements Order {",
                "    public Serializable exec() {",
                "        return System.getProperty(\"java.class.path\");",
                "    }", "}",};

        Order command = receiveOrder(shortClassName, alien_java_src);
        try {
            command.exec();
            fail("Violation should have thrown SecurityException");
        } catch (SecurityException e) { //
        }
    }
}
