/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

public class ObjectReceiverDynamicLoadTest extends DynamicClassLoadFixture {
    public interface Command {
        void exec() throws Exception;
    }

    private File passwd;

    protected void tearDown() throws Exception {
        try {
            if (passwd != null)
                passwd.delete();
            super.tearDown();
        } catch (Exception e) {
        }
    }

    public void testRecieveAlienTransmission() throws Exception {
        String shortClassName = "Alien0";
        String alien0_java_src = buildAlienSource0(shortClassName);

        compileAlienClass(shortClassName, alien0_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        Socket s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(alienOrderItem);
    }

    private String buildAlienSource0(String shortClassName) {
        return "package aliens;"
            + "import java.io.*;"
            + "import hotpotato.io.*;"
            + "public class "
            + shortClassName
            + " implements Serializable {"
            + "    public String data = \"foo\";"
            + "}";
    }

    public void testRecieveMultiClassAlienTransmission() throws Exception {
        String shortClassName = "Alien1";
        String alien1_java_src = buildAlienSource1(shortClassName);

        compileAlienClass(shortClassName, alien1_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        Socket s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(alienOrderItem);
    }

    private String buildAlienSource1(String shortClassName) {
        return "package aliens;"
            + "import java.io.*;"
            + "import hotpotato.io.*;"
            + "public class "
            + shortClassName
            + " implements Serializable {"
            + "    public static class Foo implements Serializable {"
            + "        public String data = \"foo\";"
            + "    }"
            + "    public Foo foo = new Foo();"
            + "}";
    }

    public void testReceiveAlienClassArray() throws Exception {
        String shortClassName = "Alien2";
        String alien1_java_src = buildAlien2Source(shortClassName);

        compileAlienClass(shortClassName, alien1_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        Socket s = server.accept();

        Serializable alienOrderItem = new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(alienOrderItem);
    }

    private String buildAlien2Source(String shortClassName) {
        return "package aliens;"
            + "import java.io.*;"
            + "import hotpotato.io.*;"
            + "public class "
            + shortClassName
            + " implements Serializable {"
            + "    public static class Foo implements Serializable {"
            + "        public String data = \"foo\";"
            + "    }"
            + "    public Foo[] foo = new Foo[] {new Foo()};"
            + "}";
    }

    public void testClassWithSecurityViolation() throws Exception {
        passwd = new File(testDir, "faux.passwd");
        writeFile(passwd, "Shh ... it's secret.");
        String path = passwd.getPath();

        String shortClassName = "Alien3";
        String alien_java_src = buildAlien3Source(path, shortClassName);

        compileAlienClass(shortClassName, alien_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        Socket s = server.accept();

        Command command = (Command) new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(command);
        try {
            command.exec();
            fail("Violation should have thrown SecurityException");
        } catch (SecurityException e) {
        }
    }

    private String buildAlien3Source(String path, String shortClassName) {
        return "package aliens;"
            + "import java.io.*;"
            + "import hotpotato.io.*;"
            + "import hotpotato.util.*;"
            + "public class "
            + shortClassName
            + " implements ObjectReceiverDynamicLoadTest.Command, Serializable {"
            + "    public void exec() throws Exception{"
            + "        File passwd = new File(\""
            + path
            + "\");"
            + "        if (!passwd.exists()) {"
            + "            throw new RuntimeException(\"no file: \" + passwd);"
            + "        }"
            + "        FileInputStream fis = new FileInputStream(passwd);"
            + "        byte[] bytes = new Streams().readBytes(fis);"
            + "        System.out.println(new String(bytes));"
            + "    }"
            + "}";
    }

    public void testAlienAnonymousInner() throws Exception {
        String shortClassName = "Alien4";
        String alien4_java_src = buildAlien4Source(shortClassName);

        compileAlienClass(shortClassName, alien4_java_src);
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        launchDynamicClassSender("aliens." + shortClassName, port);
        Socket s = server.accept();

        Serializable obj = new ObjectReceiver(s).receive();
        s.close();
        assertNotNull(obj);
    }

    private String buildAlien4Source(String shortClassName) {
        return "package aliens;"
            + "import java.io.*;"
            + "import hotpotato.io.*;"
            + "public class "
            + shortClassName
            + " implements Serializable {"
            + "    public Serializable getSer() {"
            + "        return new Serializable(){"
            + "            public String toString() {"
            + "                return \"anonymous inner\";"
            + "            }"
            + "        };"
            + "    }"
            + "}";
    }
}
