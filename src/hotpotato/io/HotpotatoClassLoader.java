/**
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
        this(new StandardClassUtil(), parent);
    }

    HotpotatoClassLoader(ClassUtil classes, ClassLoader parent) {
        super(parent);
        this.defs = new HashMap();
        this.classes = classes;
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
        int offset = 0;
        CodeSource codeSource = new AlienCodeSource();
        return defineClass(name, bytes, offset, bytes.length, codeSource);
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
