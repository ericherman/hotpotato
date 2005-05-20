/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import java.io.*;

class ObjectInputStreamUsingLoader extends ObjectInputStream {
    private ClassLoader loader;

    public ObjectInputStreamUsingLoader(ClassLoader loader, InputStream in)
            throws IOException {

        super(in);
        this.loader = loader;
    }

    protected Class resolveClass(ObjectStreamClass desc)
            throws ClassNotFoundException {

        return loader.loadClass(desc.getName());
    }
}
