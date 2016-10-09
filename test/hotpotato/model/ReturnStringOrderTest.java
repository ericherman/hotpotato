/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

public class ReturnStringOrderTest extends TestCase {
    public void testExec() throws Exception {
        Callable<String> order = new ReturnStringOrder("foo");
        Serializable result = order.call();
        assertEquals("foo", result);
    }
}
