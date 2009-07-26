/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import hotpotato.HotpotatoServer;
import hotpotato.Request;
import hotpotato.io.LoopbackServer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;

import junit.framework.TestCase;

public class NetworkHotpotatoClientTest extends TestCase {

    private LoopbackServer server;
    private ByteArrayOutputStream baos;
    private PrintStream ps;

    protected void setUp() throws Exception {
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
    }

    protected void tearDown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        server = null;
        ps.close();
        ps = null;
        baos = null;
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
        server = new LoopbackServer(0, ps);
        server.start();

        NetworkHotpotatoClient client = new NetworkHotpotatoClient(server
                .getInetAddress(), server.getPort());

        Serializable s = client.send(new FooRequest());

        assertEquals("foo", s.toString());
    }
}
