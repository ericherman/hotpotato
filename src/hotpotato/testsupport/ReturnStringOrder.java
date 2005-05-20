/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.testsupport;

import hotpotato.*;

import java.io.*;

public class ReturnStringOrder implements Order {
    private static final long serialVersionUID = 1L;

    private String val;

    public ReturnStringOrder(String val) {
        this.val = val;
    }

    public Serializable exec() {
        return val;
    }
}
