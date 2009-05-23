/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.security.*;

class AlienCodeSource extends CodeSource {
    private static final long serialVersionUID = 1L;

    public AlienCodeSource() {
        super(null, (java.security.cert.Certificate[]) null);
    }
}
