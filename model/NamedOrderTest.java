/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import junit.framework.*;

public class NamedOrderTest extends TestCase {
    public void testExec() throws Exception {
        NamedOrder order = new NamedOrder("foo");
        assertEquals("foo", order.getName());
        assertFalse("before", order.isComplete());
        order.exec();
        assertTrue("after", order.isComplete());
    }

    public void testEquals() {
        NamedOrder order1 = new NamedOrder("foo");
        NamedOrder order2 = new NamedOrder("foo");
        NamedOrder order3 = new NamedOrder("bar");

        order2.exec();

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());

        assertFalse(order1.equals(order3));
    }
}
