/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.util;

import junit.framework.*;

public class AllTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato Utility Tests");
        suite.addTestSuite(EqualsTest.class);
        suite.addTestSuite(StandardClassUtilTest.class);
        suite.addTestSuite(StreamsTest.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTestSuite.suite());
    }
}
