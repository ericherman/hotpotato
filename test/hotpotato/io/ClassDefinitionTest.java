/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import junit.framework.TestCase;

public class ClassDefinitionTest extends TestCase {

    public void testClassDefinitionEquals() throws Exception {
        ClassDefinition cd1 = new ClassDefinition(String.class);
        ClassDefinition cd2 = new ClassDefinition(String.class);

        assertEquals("java.lang.String", cd1.className());
        assertEquals(cd1, cd2);
        assertEquals(cd1.hashCode(), cd2.hashCode());
    }
}
