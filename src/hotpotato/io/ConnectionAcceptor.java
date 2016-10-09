/**
 * Copyright (C) 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionAcceptor {
    void acceptConnection(Socket s) throws IOException;

    void close(Socket socket, Exception thrown);
}
