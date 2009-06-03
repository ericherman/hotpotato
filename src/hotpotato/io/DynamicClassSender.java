/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

public class DynamicClassSender {
    public static void main(String[] args) throws Exception {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String className = args[2];
        Class aClass = Class.forName(className);
        Serializable orderItem = (Serializable) aClass.newInstance();
        Socket socket = new Socket(hostName, port);
        ObjectSender sender = new ObjectSender(socket);
        sender.send(orderItem);
        socket.close();
    }
}
