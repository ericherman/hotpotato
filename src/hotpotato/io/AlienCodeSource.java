/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import java.security.*;

class AlienCodeSource extends CodeSource {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3256725074055606838L;

	public AlienCodeSource() {
        super(null, (java.security.cert.Certificate[]) null);
    }
}
