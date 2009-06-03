/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.util.*;

class HotpotatoClassLoader extends SecureClassLoader {
    private final Map defs;
    private final ClassUtil classes;
        private CodeSource codeSource;

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
        return defineClass(name, bytes, offset, bytes.length, codeSource());
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

    public CodeSource codeSource() {
            return (codeSource != null) ? codeSource : new AlienCodeSource();
    }

    public void useSandBox(boolean sandbox) {
        if (sandbox) {
            codeSource = new AlienCodeSource();
        } else {
            URL url = null;
            java.security.cert.Certificate[] certs = null;
            codeSource = new CodeSource(url, certs);
        }
    }

}
