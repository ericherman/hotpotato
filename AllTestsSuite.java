/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato;

import junit.framework.*;

public class AllTestsSuite {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestsSuite.class);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All Hotpotato Tests");
        suite.addTest(hotpotato.util.AllTestSuite.suite());
        suite.addTest(hotpotato.io.AllTestSuite.suite());
        suite.addTest(hotpotato.model.AllTestSuite.suite());
        suite.addTest(hotpotato.acceptance.AllTestSuite.suite());
        return suite;
    }
}
