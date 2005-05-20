/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

/**
 * ObjectSender sends objects over a network connection. It also sends all class
 * files that will be needed in order to deserialize the object on the remote
 * side.
 */
public class ObjectSender {
    private ObjectOutputStream oos;
    private ReferencedClassFinder finder;

    public ObjectSender(Socket s) throws IOException {
        OutputStream out = s.getOutputStream();
        oos = new ObjectOutputStream(out);
        finder = new ReferencedClassFinder();
    }

    public void send(Serializable obj) throws IOException {
        Class[] classes = finder.find(obj);
        for (int i = 0; i < classes.length; i++) {
            oos.writeObject(new ClassDefinition(classes[i]));
        }

        oos.writeObject(obj);
        oos.flush();
    }
}
