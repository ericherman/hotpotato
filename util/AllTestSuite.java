/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import junit.framework.*;

public class AllTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato Utility Tests");
        suite.addTestSuite(ClassesTest.class);
        suite.addTestSuite(StreamsTest.class);
        suite.addTestSuite(ConnectionStationTest.class);
        suite.addTestSuite(SynchronizedQueueTest.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }
}
