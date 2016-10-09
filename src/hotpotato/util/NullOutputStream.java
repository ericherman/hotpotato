/**
 * Copyright (C) 2004 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.util;

import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
    public void write(int b) {
        // no op
    }
}
