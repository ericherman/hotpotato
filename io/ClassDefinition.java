/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.util.*;

class ClassDefinition implements Serializable {
    private final String className;
    private final byte[] classBytes;

    public ClassDefinition(Class aClass) throws IOException {
        this.className = aClass.getName();
        String resourceName = new Classes().toResourceName(className);
        ClassLoader classLoader = aClass.getClassLoader();
        this.classBytes = loadResourceBytes(resourceName, classLoader);
    }

    public String className() {
        return className;
    }

    public byte[] classBytes() {
        return classBytes;
    }

    static byte[] loadResourceBytes(
        String resourceName,
        ClassLoader classLoader)
        throws IOException {

        InputStream in;
        if (classLoader != null) {
            in = classLoader.getResourceAsStream(resourceName);
        } else {
            in = ClassLoader.getSystemResourceAsStream(resourceName);
        }
        return new Streams().readBytes(in);
    }

    public String toString() {
        return className() + ": " + classBytes().length;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(getClass().equals(obj.getClass())))
            return false;
        ClassDefinition other = (ClassDefinition) obj;

        if (!className().equals(other.className()))
            return false;
        return Arrays.equals(classBytes(), other.classBytes());
    }

    public int hashCode() {
        return classBytes().length;
    }
}
