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
import java.util.*;

public class CounterTop extends ConnectionStation {
    private Map counterTop;

    public CounterTop(int port, Map counterTop) throws IOException {
        super(port, "CounterTop");
        this.counterTop = counterTop;
    }

    protected void acceptConnection(Socket s) throws IOException {
        Order in = (Order) new ObjectReceiver(s).receive();
        if (in != null) {
            synchronized (counterTop) {
                counterTop.put(in.getId(), in);
            }
        }
    }
}
