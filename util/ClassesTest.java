/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import junit.framework.*;

public class ClassesTest extends TestCase {
    public void testResourceName() {
        String className = getClass().getName();
        assertEquals("hotpotato.util.ClassesTest", className);
        String resourceName = new Classes().toResourceName(className);
        assertEquals("hotpotato/util/ClassesTest.class", resourceName);
    }

    public void testToClassName() {
        String className = new Classes().toClassName("aliens/Alien.class");
        assertEquals("aliens.Alien", className);
    }
}
