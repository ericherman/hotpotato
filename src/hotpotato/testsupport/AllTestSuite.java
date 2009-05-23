/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.testsupport;

import junit.framework.*;

public class AllTestSuite {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato 'Misc' Tests");

        suite.addTestSuite(ReturnStringOrderTest.class);

        return suite;
    }
}