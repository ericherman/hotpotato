/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.testsupport;

import hotpotato.Order;

import java.io.*;

import junit.framework.*;

public class ReturnStringOrderTest extends TestCase {
    public void testExec() throws Exception {
        Order order = new ReturnStringOrder("foo");
        Serializable result = order.exec();
        assertEquals("foo", result);
    }
}
