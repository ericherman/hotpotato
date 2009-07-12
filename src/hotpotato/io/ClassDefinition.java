/**
 * Copyright (C) 2003, 2005 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import hotpotato.util.ClassUtil;
import hotpotato.util.Equals;
import hotpotato.util.StandardClassUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

class ClassDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String className;

    private final byte[] classBytes;

    private Equals equals;

    public ClassDefinition(Class<?> aClass) throws IOException {
        this(new StandardClassUtil(), aClass);
    }

    ClassDefinition(ClassUtil classUtil, Class<?> aClass) throws IOException {
        this.className = aClass.getName();
        this.classBytes = classUtil.getResourceBytes(aClass);

        this.equals = new Equals(this) {
            private static final long serialVersionUID = 1L;

            protected boolean equalsInner(Object obj) {
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
        return className() + ", size: " + classBytes().length;
    }

    public boolean equals(Object obj) {
        return equals.check(obj);
    }

    public int hashCode() {
        return classBytes().length;
    }
}
