/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.security.*;
import java.util.*;

class HotpotatoClassLoader extends SecureClassLoader {
    private final Map defs;
    private final ClassUtil classes;

    public HotpotatoClassLoader(ClassLoader parent) {
        super(parent);
        defs = new HashMap();
        classes = new ClassUtil();
        SecurityManager manager = System.getSecurityManager();

        if (manager == null) {
            manager = new SecurityManager();
            Policy myPolicy = new HotpotatoPolicy();
            Policy.setPolicy(myPolicy);
            System.setSecurityManager(manager);
        }
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] bytes = getBytes(name);
        if (bytes == null)
            throw new ClassNotFoundException(name);
        return defineClass(name, bytes, 0, bytes.length, new AlienCodeSource());
    }

    private byte[] getBytes(String className) {
        return (byte[]) defs.get(className);
    }

    public void register(ClassDefinition classDef) {
        defs.put(classDef.className(), classDef.classBytes());
    }

    public InputStream getResourceAsStream(String resource) {
        byte[] bytes = getBytes(classes.toClassName(resource));
        return (bytes == null) ? null : new ByteArrayInputStream(bytes);
    }
}
