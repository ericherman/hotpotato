/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
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
