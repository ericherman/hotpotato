/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import hotpotato.io.*;
import hotpotato.util.*;

import java.io.*;
import java.net.*;

public class TicketWheel extends ConnectionStation {
    private Queue theWheel;

    public TicketWheel(int port, Queue theWheel) throws IOException {
        super(port, "TicketWheel");
        this.theWheel = theWheel;
    }

    protected void acceptConnection(Socket s) throws IOException {
        Order item = null;
        synchronized (theWheel) {
            if (theWheel.hasItems()) {
                item = (Order) theWheel.get();
            }
        }
        new ObjectSender(s).send(item);
    }
}
