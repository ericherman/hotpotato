/**
 * Copyright (C) 2005 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

/** 
 * Bundle of utility methods that do name conversions from classes to
 * resources and back.  
 */
public interface ClassUtil {
    public abstract ClassLoader classLoaderFor(Class aClass);
    public abstract String toClassName(String classResourceName);
    public abstract String toResourceName(Class aClass);
    public abstract String toResourceName(String className);
    public abstract byte[] getResourceBytes(Class aClass) throws IOException;
}