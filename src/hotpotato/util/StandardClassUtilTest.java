/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.util.*;

import junit.framework.*;

public class StandardClassUtilTest extends TestCase {
    public void testResourceName() {
        String className = getClass().getName();
        assertEquals("hotpotato.util.StandardClassUtilTest", className);

        String expected = "hotpotato/util/StandardClassUtilTest.class";
        assertEquals(expected, new StandardClassUtil().toResourceName(className));
        assertEquals(expected, new StandardClassUtil().toResourceName(getClass()));
    }

    public void testToClassName() {
        String className = new StandardClassUtil().toClassName("aliens/Alien.class");
        assertEquals("aliens.Alien", className);
    }

    public void testLoadResourceBytes() throws Exception {
        byte[] simpleFileBytes =
            new byte[] {
                68,
                111,
                110,
                39,
                116,
                32,
                99,
                104,
                97,
                110,
                103,
                101,
                32,
                109,
                101,
                33,
                10 };

        String fileName = "hotpotato/util/simplefile.txt";
        ClassLoader system = ClassLoader.getSystemClassLoader();
        byte[] result = new StandardClassUtil().loadResourceBytes(fileName, system);
        assertTrue(Arrays.equals(result, simpleFileBytes));
    }
}
