/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato;

import junit.framework.*;

public class AllTestSuites {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTestSuites.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All Hotpotato Tests");

        suite.addTest(hotpotato.testsupport.AllTestSuite.suite());
        suite.addTest(hotpotato.util.AllTestSuite.suite());
        suite.addTest(hotpotato.io.AllTestSuite.suite());
        suite.addTest(hotpotato.model.AllTestSuite.suite());
        suite.addTest(hotpotato.net.AllTestSuite.suite());
        suite.addTest(hotpotato.acceptance.AllTestSuite.suite());

        return suite;
    }
}
