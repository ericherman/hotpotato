/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato;

import junit.framework.*;

public class AllTestSuite {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Top level Hotpotato Tests");

        suite.addTestSuite(ReturnStringOrderTest.class);

        return suite;
    }
}