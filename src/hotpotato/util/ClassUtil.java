/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

/** 
 * Bundle of utility methods that do name conversions from classes to
 * resources and back.  This is final simply as a hint to the compiler, 
 * it can be un-finalized safely.
 */
public final class ClassUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    public ClassLoader classLoaderFor(Class aClass) {
        ClassLoader cl = aClass.getClassLoader();
        return (cl != null) ? cl : ClassLoader.getSystemClassLoader();
    }

    public byte[] loadResourceBytes(
        String resourceName,
        ClassLoader classLoader)
        throws IOException {

        InputStream in = classLoader.getResourceAsStream(resourceName);
        return new Streams().readBytes(in);
    }

    public String toClassName(String classResourceName) {
        if (classResourceName.endsWith(".class")) {
            int end = classResourceName.length() - ".class".length();
            classResourceName = classResourceName.substring(0, end);
        }
        return classResourceName.replace('/', '.');
    }

    public String toResourceName(Class aClass) {
        return toResourceName(aClass.getName());
    }

    public String toResourceName(String className) {
        return className.replace('.', '/') + ".class";
    }
}
