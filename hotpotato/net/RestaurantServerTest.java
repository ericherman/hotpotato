/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.net;

import hotpotato.*;
import hotpotato.io.*;

import java.io.*;
import java.net.*;

import junit.framework.*;

public class RestaurantServerTest extends TestCase {

    private RestaurantServer server;

    protected void tearDown() throws Exception {
        if (server != null)
            server.shutdown();
    }

    static class Foo implements Request {
        private static final long serialVersionUID = 1L;
        public Serializable exec(Restaurant restaurant) {
            return "foo";
        }
    }

    public void testAcceptConnection() throws Exception {
        server = new RestaurantServer(0);
        server.start();
        
        Socket s = new Socket(InetAddress.getLocalHost(), server.getPort());
        ObjectSender sender = new ObjectSender(s);

        sender.send(new Foo());

        String reply = "" + new ObjectReceiver(s).receive();
        s.close();

        assertEquals("foo", reply);
    }
}
