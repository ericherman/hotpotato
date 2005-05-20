/**
 * Copyright (C) 2004 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.util;

import java.io.*;

public class NullOutputStream extends OutputStream {
    public void write(int b) {
        // no op
    }
}