/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.Shell;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public abstract class DynamicClassLoadTestFixture extends TestCase {
    private final static String PATH = System.getProperty("java.library.path");
    protected final static String CLASSPATH = System
            .getProperty("java.class.path");
    protected final static String[] ENVP = new String[] { "PATH=" + PATH };

    protected String alienClasspath;
    protected Thread launched;
    protected File testDir;
    protected ByteArrayOutputStream baos;
    protected PrintStream out;
    protected ByteArrayOutputStream baes;
    protected PrintStream err;

    private File tmpDir;
    private File aliensDir;

    protected void setUp() throws Exception {
        tmpDir = new File(System.getProperty("java.io.tmpdir"));
        testDir = new File(tmpDir, "test" + System.currentTimeMillis());
        aliensDir = new File(testDir, "aliens");
        aliensDir.mkdirs();
        baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        baes = new ByteArrayOutputStream();
        err = new PrintStream(baes);

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
        out.close();

        alienClasspath = null;
        launched = null;
        testDir = null;
        out = null;
        err = null;
        tmpDir = null;
        aliensDir = null;
        baos = null;
        baes = null;
    }

    protected void compileAlienClass(String shortClassName, String[] alienSrc)
            throws Exception {

        File alienJava = new File(aliensDir, shortClassName + ".java");
        File alienClass = new File(aliensDir, shortClassName + ".class");
        writeFile(alienJava, alienSrc);
        alienClasspath = CLASSPATH + File.pathSeparatorChar + testDir.getPath();

        String[] args = new String[] { "javac", "-classpath", alienClasspath,
                alienJava.getPath(), };

        Thread t = new Shell(args, ENVP, "javac Alien", out, err);
        t.start();
        t.join();
        out.flush();
        err.flush();
        String msg = "";
        if (!alienJava.exists() || !alienClass.exists()) {
            msg = msg + "\n" + baos.toString();
            msg = msg + "\n" + baes.toString();
        }
        assertTrue("java file exists" + msg, alienJava.exists());
        assertTrue("class file exists" + msg, alienClass.exists());
    }

    protected void launchDynamicClassSender(String className, int port)
            throws UnknownHostException {
        String javaProgram = DynamicClassSender.class.getName();
        // assertEquals("hotpotato.io.DynamicClassSender", javaProgram);
        String[] args = new String[] { "java", "-cp", alienClasspath,
                javaProgram, InetAddress.getLocalHost().getHostName(),
                Integer.toString(port), className };

        launched = new Shell(args, ENVP, "send alien", out, err);
        launched.start();
    }

    protected void writeFile(File file, String[] contents)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
        for (int i = 0; i < contents.length; i++) {
            pw.println(contents[i]);
        }
        pw.close();
    }
}
