/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import java.util.*;

import junit.framework.*;

public class ClassDefinitionTest extends TestCase {

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
        byte[] result = ClassDefinition.loadResourceBytes(fileName, null);
        assertTrue(Arrays.equals(result, simpleFileBytes));
    }

    public void testClassDefinitionEquals() throws Exception {
        ClassDefinition cd1 = new ClassDefinition(String.class);
        ClassDefinition cd2 = new ClassDefinition(String.class);

        assertEquals("java.lang.String", cd1.className());
        assertEquals(cd1, cd2);
        assertEquals(cd1.hashCode(), cd2.hashCode());
    }
}
