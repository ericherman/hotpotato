/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public abstract class DynamicClassLoadFixture extends TestCase {
    private final static String PATH = System.getProperty("java.library.path");
    protected final static String CLASSPATH = System
            .getProperty("java.class.path");
    protected final static String[] ENVP = new String[]{"PATH=" + PATH};

    protected String alienClasspath;
    protected Thread launched;
    protected File testDir;
    protected PrintStream out;
    protected PrintStream err;

    private File tmpDir;
    private File aliensDir;

    protected void setUp() throws Exception {
        tmpDir = new File(System.getProperty("java.io.tmpdir"));
        testDir = new File(tmpDir, "test" + System.currentTimeMillis());
        aliensDir = new File(testDir, "aliens");
        aliensDir.mkdirs();
        out = new NullPrintStream();
        err = out;
    }

    protected void tearDown() throws Exception {
        if (launched != null) {
            Thread.sleep(3 * ConnectionServer.SLEEP_DELAY);
        }

        File[] files = aliensDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
        aliensDir.delete();

        files = testDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
        testDir.delete();
        out = null;
        err = null;
    }

    protected void compileAlienClass(String shortClassName, String[] alienSrc)
            throws Exception {

        File alienJava = new File(aliensDir, shortClassName + ".java");
        File alienClass = new File(aliensDir, shortClassName + ".class");
        writeFile(alienJava, alienSrc);
        alienClasspath = CLASSPATH + File.pathSeparatorChar + testDir.getPath();

        String[] args = new String[]{"javac", "-classpath", alienClasspath,
                alienJava.getPath(),};

        Thread t = new Shell(args, ENVP, "javac Alien", out, err);
        t.start();
        t.join();
        assertTrue("java file exists", alienJava.exists());
        assertTrue("class file exists", alienClass.exists());
    }

    protected void launchDynamicClassSender(String className, int port)
            throws UnknownHostException {
        String javaProgram = DynamicClassSender.class.getName();
        assertEquals("hotpotato.io.DynamicClassSender", javaProgram);
        String[] args = new String[]{"java", "-cp", alienClasspath,
                javaProgram, InetAddress.getLocalHost().getHostName(),
                Integer.toString(port), className};

        launched = new Shell(args, ENVP, "send alien", out, err);
        launched.start();
    }

    protected void writeFile(File file, String[] contents)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(fos);
        for (int i = 0; i < contents.length; i++)
            out.println(contents[i]);

        out.close();
    }
}
