/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import junit.framework.*;

public class SynchronizedQueueTest extends TestCase {

    public void testBasicIO() {
        SynchronizedQueue theQueue = new SynchronizedQueue();

        Object obj1 = "First";
        Object obj2 = "Second";
        Object obj3 = "Third";

        assertEquals(false, theQueue.hasItems());        

        theQueue.add(obj1);
        assertEquals(true, theQueue.hasItems());        

        theQueue.add(obj2);
        assertEquals(true, theQueue.hasItems());        

        theQueue.add(obj3);
        assertEquals(true, theQueue.hasItems());        

        assertEquals(obj1, theQueue.get());
        assertEquals(true, theQueue.hasItems());        
 
        assertEquals(obj2, theQueue.get());
        assertEquals(true, theQueue.hasItems());        
 
        assertEquals(obj3, theQueue.get());
        assertEquals(false, theQueue.hasItems());        
     }
}
