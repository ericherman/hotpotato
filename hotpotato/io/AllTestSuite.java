/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import junit.framework.*;

public class AllTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato I/O Tests");
        suite.addTestSuite(ClassDefinitionTest.class);
        suite.addTestSuite(ConnectionServerTest.class);
        suite.addTestSuite(ObjectRecieverTest.class);
        suite.addTestSuite(ObjectSenderTest.class);
        suite.addTestSuite(ReferencedClassFinderTest.class);
        suite.addTestSuite(ObjectReceiverDynamicLoadTest.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }
}
