/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

/** 
 * Stream operation utility methods.  This class is final simply as a hint 
 * to the compiler, feel free to un-finalize it.
 */
public final class Streams {
    private static final int END_OF_STREAM = -1;

    public Thread connect(final InputStream from, final OutputStream to) {
        Thread t = new Thread("StreamConnector") {
            public void run() {
                try {
                    copy(from, to);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        t.start();
        return t;
    }

    public void copy(InputStream from, OutputStream to) throws IOException {
        while (true) {
            int i = from.read();
            if (i == END_OF_STREAM) {
                break;
            }
            to.write(i);
        }
    }

    public byte[] readBytes(InputStream from) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        copy(from, buf);
        return buf.toByteArray();
    }
}
