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
        this.returnCode = Integer.MIN_VALUE;
        this.streams = new Streams();
        this.out = (out != null) ? out : new NullPrintStream();
        this.err = (err != null) ? err : new NullPrintStream();

    }

    public Shell(String[] args, String[] envp, String name) {
        this(args, envp, name, null, null);
    }

    public void run() {
        try {
            returnCode = runtimeExec();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int runtimeExec() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, envp);
        streams.connect(p.getInputStream(), out);
        streams.connect(p.getErrorStream(), err);
        return p.waitFor();
    }

    public int getReturnCode() {
        return returnCode;
    }
}
