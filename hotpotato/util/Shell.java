/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

public class Shell extends Thread {
    private String[] args;
    private String[] envp;
    PrintStream out;

    public Shell(String[] args, String[] envp, String name, PrintStream out) {
        super(name);
        this.args = args;
        this.envp = envp;
        this.out = out;
    }

    public Shell(String[] args, String[] envp, String name) {
        this(args, envp, name, null);
    }

    public void run() {
        try {
            runtimeExec();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void runtimeExec() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, envp);
        InputStream shellStdErr = p.getErrorStream();
        InputStream shellStdOut = p.getInputStream();
        int i = p.waitFor();
        if (out != null) {
            out.println(getName() + " complete: " + i);
            print(shellStdErr);
            out.println();
            print(shellStdOut);
            out.println("---");
        }
    }

    private void print(InputStream in) throws IOException {
        while (true) {
            int i = in.read();
            if (i == -1) {
                break;
            }
            out.print((char) ((byte) i));
        }
    }
}
