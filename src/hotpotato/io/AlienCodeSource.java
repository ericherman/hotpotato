/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.security.CodeSource;

class AlienCodeSource extends CodeSource {
    private static final long serialVersionUID = 1L;

    public AlienCodeSource() {
        super(null, (java.security.cert.Certificate[]) null);
    }
}
