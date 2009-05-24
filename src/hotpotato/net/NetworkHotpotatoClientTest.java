/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.testsupport.*;

import java.io.*;

import junit.framework.*;

public class NetworkHotpotatoClientTest extends TestCase {
    
    private LoopbackServer server;

    protected void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        server = null;
    }

    static class FooRequest implements Request {
        private static final long serialVersionUID = 1L;
        public Serializable exec(HotpotatoServer restaurant) {
            return null;
        }
        public String toString() {
            return "foo";
        }
    }

    public void testSend() throws Exception {
        server = new LoopbackServer();
        server.start();

        NetworkHotpotatoClient client = new NetworkHotpotatoClient(server
                .getInetAddress(), server.getPort());

        Serializable s = client.send(new FooRequest());

        assertEquals("foo", s.toString());
    }
}
