/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.model;

import junit.framework.*;

public class AllTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTestSuite.suite());
    }
    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato Model Tests");
        suite.addTestSuite(TicketQueueTest.class);
        suite.addTestSuite(HotpotatodTest.class);
        suite.addTestSuite(WorkerTest.class);
        suite.addTestSuite(CustomerTest.class);

        return suite;
    }
}
