/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;
import java.net.*;

/** 
 * Stream operation utility methods.  This class is final simply as a hint 
 * to the compiler, feel free to un-finalize it.
 */ 
public final class Streams {
    private static final int END_OF_STREAM = -1;

    public Thread connect(Socket loopback) throws IOException {
        return connect(loopback.getInputStream(), loopback.getOutputStream());
    }

    public Thread connect(final InputStream from, final OutputStream to) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    copy(from, to);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Thread t = new Thread(r, "StreamConnector");
        t.start();
        return t;
    }

    public void copy(InputStream from, OutputStream to) throws IOException {
        int next = from.read();
        while (next != END_OF_STREAM) {
            to.write(next);
            next = from.read();
        }
    }

    public byte[] readBytes(InputStream from) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        copy(from, buf);
        return buf.toByteArray();
    }
}
