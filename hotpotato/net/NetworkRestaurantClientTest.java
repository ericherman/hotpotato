/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.io.*;

import java.io.*;

import junit.framework.*;

public class NetworkRestaurantClientTest extends TestCase {
    private LoopbackServer server;

    protected void tearDown() throws Exception {
        if (server != null)
            server.shutdown();
    }
    static class FooRequest implements Request {
        public Serializable exec(Restaurant restaurant) {
            return null;
        }
        public String toString() {
            return "foo";
        }
    }

    public void testSend() throws Exception {
        server = new LoopbackServer(0);
        server.start();

        NetworkRestaurantClient client = new NetworkRestaurantClient(server
                .getInetAddress(),
                server.getPort());

        Serializable s = client.send(new FooRequest());

        assertEquals("foo", s.toString());
    }
}