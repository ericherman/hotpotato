/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import java.net.Socket;

public class LoopbackServer extends ConnectionServer {
    public LoopbackServer(int port) {
        super(port, "LoopBack");
    }

    protected void acceptConnection(Socket s) {
        try {
            ObjectSender sender = new ObjectSender(s);
            ObjectReceiver reciever = new ObjectReceiver(s);
            while (true) {
                sender.send(reciever.receive());
            }
        } catch (Exception quit) {
        	// just die on exception
        	// we're probably done
        }
    }
}
