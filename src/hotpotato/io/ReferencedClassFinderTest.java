/**
 * Copyright (C) 2003 by Eric Herman. 
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt 
 *  or http://www.fsf.org/licenses/gpl.txt
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx
 */
package hotpotato.io;

import hotpotato.util.*;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.bcel.classfile.*;

public class ReferencedClassFinderTest extends TestCase {
    interface Baz extends Serializable {
        String interfaceMethod();
    }

    class Foo {
        public int i;
    }

    class Wiz implements Baz {
		private static final long serialVersionUID = 1L;

		public String interfaceMethod() {
            return "wiz";
        }
    }

    void assertContains(Collection c, Object o) {
        assertTrue("should contain: " + o.toString(), c.contains(o));
    }

    public void testClassesReferencedByInterface() throws Exception {
        class Foo {
            public final Baz baz;
            public Foo(Baz baz) {
                this.baz = baz;
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false, new StandardClassUtil());
        Wiz wiz = new Wiz();
        Foo foo = new Foo(wiz);

        List list = finder.discoverObjectsForInspection(foo);
        assertContains(list, (wiz));

        List found = Arrays.asList(finder.find(foo));
        assertContains(found, Wiz.class);
    }

    public void testPrivate() throws Exception {
        class Foo {
            private final Baz baz;
            public Foo(Baz baz) {
                this.baz = baz;
            }
            public String toString() {
                return baz.toString();
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false, new StandardClassUtil());
        Wiz wiz = new Wiz();
        Foo foo = new Foo(wiz);

        List list = finder.discoverObjectsForInspection(foo);
        assertContains(list, wiz);

        List found = Arrays.asList(finder.find(foo));
        assertContains(found, Wiz.class);
    }

    public void testNullSafe() throws Exception {
        Class[] done = new ReferencedClassFinder().find(null);
        assertEquals(0, done.length);
    }

    public void testNoReferences() throws Exception {
        Set done = new HashSet();
        new ReferencedClassFinder(false, new StandardClassUtil()).findReferences(done, new Foo());
        assertContains(done, Foo.class);
    }

    public void testOneReference() throws Exception {
        class Bar {
            public Foo getFoo() {
                return new Foo();
            }
        }

        Set done = new HashSet();
        new ReferencedClassFinder(false, new StandardClassUtil()).findReferences(done, new Bar());
        assertContains(done, Bar.class);
        assertContains(done, Foo.class);
    }

    public void testAnonymousAndInterfaceReferences() throws Exception {
        class Bar implements Baz {
    		private static final long serialVersionUID = 1L;
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
        Set done = new HashSet();
        new ReferencedClassFinder(false , new StandardClassUtil()).findReferences(done, bar);
        assertContains(done, Bar.class);
        assertContains(done, Foo.class);
        assertContains(done, Baz.class);
        assertContains(done, bar.getFoo().getClass());
    }

    public void testInList() throws Exception {
        class Foo {
            private final List serializables;
            public Foo(Serializable wiz) {
                this.serializables = new ArrayList();
                serializables.add(wiz);
            }
            public String toString() {
                return serializables.get(0).toString();
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false, new StandardClassUtil());
        Foo foo = new Foo(new Wiz());

        List found = Arrays.asList(finder.find(foo));
        assertContains(found, Wiz.class);
    }

    public void testNoHotPotatoClasses() throws Exception {
        Object o = new Node() {
            public void accept(Visitor obj) {
                System.out.println(obj);
            }
        };
        Set done = new HashSet();
        new ReferencedClassFinder().findReferences(done, o);
        assertEquals(0, done.size());
    }
}
