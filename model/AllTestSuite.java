/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

import junit.framework.*;

public class AllTestSuite {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AllTestSuite.class);
    }
    public static Test suite() {
        TestSuite suite = new TestSuite("Hotpotato Model Tests");
        suite.addTestSuite(CookTest.class);
        suite.addTestSuite(CounterTopTest.class);
        suite.addTestSuite(CustomerTest.class);
        suite.addTestSuite(NamedOrderTest.class);
        suite.addTestSuite(OrderPresenterTest.class);
        suite.addTestSuite(OrderTakerTest.class);
        suite.addTestSuite(TicketWheelTest.class);
        return suite;
    }
}
