/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.io;

import java.util.*;

import junit.framework.*;

import org.apache.bcel.classfile.*;

public class ReferencedClassFinderTest extends TestCase {
    interface Baz {
        String interfaceMethod();
    }
    class Foo {
        public int i;
    }

    public void testNull() throws Exception {
        Class[] done = new ReferencedClassFinder().find(null);
        assertEquals(0, done.length);
    }

    public void testNoReferences() throws Exception {
        Set done = new ReferencedClassFinder().findReferences(false, new Foo());
        assertTrue(done.contains(Foo.class));
    }

    public void testOneReference() throws Exception {
        class Bar {
            public Foo getFoo() {
                return new Foo();
            }
        }

        Set done = new ReferencedClassFinder().findReferences(false, new Bar());
        assertTrue(done.contains(Bar.class));
        assertTrue(done.contains(Foo.class));
    }

    public void testAnonymousAndInterfaceReferences() throws Exception {
        class Bar implements Baz {
            public Foo getFoo() {
                return new Foo() {
                    public String toString() {
                        return "anon";
                    }
                };
            }
            public String interfaceMethod() {
                return getFoo().toString();
            }
        }

        Bar bar = new Bar();
        Set done = new ReferencedClassFinder().findReferences(false, bar);
        assertTrue(done.contains(Bar.class));
        assertTrue(done.contains(Foo.class));
        assertTrue(done.contains(Baz.class));
        assertTrue(done.contains(bar.getFoo().getClass()));
    }

    public void testNoHotPotatoClasses() throws Exception {
        Object o = new Node() {
            public void accept(Visitor obj) {
                System.out.println(obj);
            }
        };
        Set done = new ReferencedClassFinder().findReferences(true, o);
        assertEquals(0, done.size());
    }
}
