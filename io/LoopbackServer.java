/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.net.*;

public class LoopbackServer extends ConnectionStation {
    public LoopbackServer(int port) throws IOException {
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
        }
    }
}
