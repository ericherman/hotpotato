/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.net;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato 'net' Tests");
        suite.addTestSuite(SocketHotpotatoServerTest.class);
        suite.addTestSuite(NetworkHotpotatoClientTest.class);

        return suite;
    }
}
