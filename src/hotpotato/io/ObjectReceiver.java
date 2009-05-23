/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import java.io.*;
import java.net.*;

/**
 * ObjectReceiver receives objects over a network connection. It loads all of
 * the classes using a custom class loader, into a security domain that has no
 * privileges.
 */
public class ObjectReceiver {
    private HotpotatoClassLoader loader;
    private ObjectInputStream ois;

    public ObjectReceiver(Socket s) throws IOException {
        loader = new HotpotatoClassLoader(getClass().getClassLoader());
        InputStream in = s.getInputStream();
        ois = new ObjectInputStreamUsingLoader(loader, in);
    }

    public Serializable receive() throws IOException {
        try {
            Object obj = ois.readObject();
            while (obj instanceof ClassDefinition) {
                loader.register((ClassDefinition) obj);
                obj = ois.readObject();
            }
            return (Serializable) obj;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
