/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.util.*;

/**
 * Simple <code>Queue</code> based on a linked list.  This is final simply as a 
 * hint to the compiler, feel free to un-finalize it.
 * 
 * When making multiple calls to a single queue, please consider whether you
 * need to perform all operations within a single block synchronized on the
 * queue.  For example, if you call <code>hasItems</code> and then call 
 * <code>get</code> if the <code>hasItems</code> call returns true, you should
 * enclose all of that code in a block synchronized on the queue.
 */
public final class SynchronizedQueue implements Queue {

    private LinkedList queue;

    public SynchronizedQueue() {
        queue = new LinkedList();
    }

    synchronized public boolean hasItems() {
        return !queue.isEmpty();
    }

    synchronized public boolean isEmpty() {
        return queue.isEmpty();
    }

    synchronized public void add(Object theObject) {
        queue.addLast(theObject);
    }

    synchronized public Object get() {
        return queue.removeFirst();
    }

    synchronized public int size() {
        return queue.size();
    }
}
