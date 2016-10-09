/**
 * Copyright (C) 2003 - 2016 by Eric Herman.
 * For licensing information see COPYING
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.io;

import junit.framework.Test;
import junit.framework.TestSuite;

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
        junit.textui.TestRunner.run(AllTestSuite.suite());
    }
}
