/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.acceptance;

import junit.framework.*;

public class AllTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato Acceptance Tests");
        suite.addTestSuite(AcceptanceTest.class);
        suite.addTestSuite(AcceptanceMultiVMTest.class);
        suite.addTestSuite(DynamicClassLoadFromCustomerTest.class);
        return suite;
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }
}