/**
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
    private PrintStream out;
    private PrintStream err;
    private Streams streams;
    private int returnCode;

    public Shell(String[] args, String[] envp, String name, PrintStream out,
            PrintStream err) {
        super(name);
        this.args = args;
        this.envp = envp;
        this.out = out;
        this.err = err;
        this.streams = new Streams();
        this.returnCode = Integer.MIN_VALUE;
    }

    public Shell(String[] args, String[] envp, String name) {
        this(args, envp, name, null, null);
    }

    public void run() {
        try {
            returnCode = runtimeExec();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    private int runtimeExec() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, envp);

        if (out != null) {
            InputStream shellStdOut = p.getInputStream();
            new Streams().connect(shellStdOut, out);
        }
        if (err != null) {
            InputStream shellStdErr = p.getErrorStream();
            new Streams().connect(shellStdErr, err);
        }

        return p.waitFor();
    }

    private void print(InputStream in) throws IOException {
        out.print(streams.readString(in));
    }

    public int getReturnCode() {
        return returnCode;
    }
}