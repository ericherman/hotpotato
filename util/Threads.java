/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

/** 
 * Thread operation utility methods.  This is final simply as a hint to the 
 * compiler; feel free to un-finalize it.
 */ 
public final class Threads {

    public static void pause(int yields) {
        for (int i = 0; i < yields; i++)
            Thread.yield();
    }

    public static void pause() {
        pause(3000);
    }

    public static Thread launch(
        String threadName,
        String[] args,
        String[] envp) {

        return launch(threadName, args, envp, false);
    }

    public static Thread launchVerbose(
        String threadName,
        String[] args,
        String[] envp) {

        return launch(threadName, args, envp, true);
    }

    private static Thread launch(
        final String name,
        final String[] args,
        final String[] envp,
        final boolean verbose) {
        Thread t = new Thread(name) {
            public void run() {
                try {
                    Process p = Runtime.getRuntime().exec(args, envp);
                    InputStream err = p.getErrorStream();
                    InputStream out = p.getInputStream();
                    int i = p.waitFor();
                    if (verbose) {
                        System.out.println(name + " complete: " + i);
                        print(err);
                        System.out.println();
                        print(out);
                        System.out.println("---");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            private void print(InputStream err) throws IOException {
                int i;
                i = 0;
                while (i != -1) {
                    i = err.read();
                    if (i != -1)
                        System.out.print((char) ((byte) i));
                }
            }
        };
        t.start();
        return t;
    }
}
