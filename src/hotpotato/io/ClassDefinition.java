/**
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

    private final ClassUtil classUtil;

    public ClassDefinition(Class aClass) throws IOException {
        this.classUtil = new ClassUtil();
        this.className = aClass.getName();
        String resourceName = classUtil.toResourceName(className);
        ClassLoader classLoader = classUtil.classLoaderFor(aClass);
        this.classBytes = classUtil
                .loadResourceBytes(resourceName, classLoader);
    }

    public String className() {
        return className;
    }

    public byte[] classBytes() {
        return classBytes;
    }

    public String toString() {
        return className() + ": " + classBytes().length;
    }

    public boolean equals(Object obj) {
        return new Equals() {
            public boolean classCheck(Object left, Object right) {
                ClassDefinition us = (ClassDefinition) left;
                ClassDefinition other = (ClassDefinition) right;

                if (!us.className().equals(other.className()))
                    return false;
                return Arrays.equals(us.classBytes(), other.classBytes());
            }
        }.check(this, obj);
    }

    public int hashCode() {
        return classBytes().length;
    }
}