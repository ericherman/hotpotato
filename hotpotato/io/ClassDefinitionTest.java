/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import junit.framework.*;

public class ClassDefinitionTest extends TestCase {

    public void testClassDefinitionEquals() throws Exception {
        ClassDefinition cd1 = new ClassDefinition(String.class);
        ClassDefinition cd2 = new ClassDefinition(String.class);

        assertEquals("java.lang.String", cd1.className());
        assertEquals(cd1, cd2);
        assertEquals(cd1.hashCode(), cd2.hashCode());
    }
}
