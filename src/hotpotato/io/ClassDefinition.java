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
	private static final long serialVersionUID = 1L;

	private final String className;

    private final byte[] classBytes;

    private final ClassUtil classUtil;
    
    private final Equals equals;

    public ClassDefinition(Class aClass) throws IOException {
        this.classUtil = new ClassUtil();
        this.className = aClass.getName();
        
        String resourceName = classUtil.toResourceName(className);
        ClassLoader classLoader = classUtil.classLoaderFor(aClass);
        this.classBytes = classUtil
                .loadResourceBytes(resourceName, classLoader);
        
        this.equals = new Equals(this) {
            private static final long serialVersionUID = 1L;
            public boolean classCheck(Object obj) {
                ClassDefinition other = (ClassDefinition) obj;

                if (!className().equals(other.className()))
                    return false;
                return Arrays.equals(classBytes(), other.classBytes());
            }
        };
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
        return equals.check(obj);
    }

    public int hashCode() {
        return classBytes().length;
    }
}