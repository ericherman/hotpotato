/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.testsupport;

import hotpotato.*;

import java.io.*;

import junit.framework.*;

public class ReturnStringOrderTest extends TestCase {
    public void testExec() throws Exception {
        Order order = new ReturnStringOrder("foo");
        Serializable result = order.exec();
        assertEquals("foo", result);
    }
}
