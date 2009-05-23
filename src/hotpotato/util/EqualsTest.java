/**
 * Copyright (C) 2005 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package hotpotato.util;

import junit.framework.*;

public class EqualsTest extends TestCase {
    private boolean equalsInnerCalled;

    public void testSameObject() {
        class MyClass {
            public boolean equals(Object obj) {
                return new Equals(this) {
                    protected boolean equalsInner(Object other) {
                        equalsInnerCalled = true;
                        return this == other;
                    }
                }.check(obj);
            }

            public int hashCode() {
                return 0;
            }
        };
        final Object a = new MyClass();
        final Object b = new MyClass();
        equalsInnerCalled = false;
        assertTrue(a.equals(a));
        assertFalse(equalsInnerCalled);

        equalsInnerCalled = false;
        assertFalse(a.equals(b));
        assertTrue(equalsInnerCalled);
    }

    public void testNullOther() {
        Equals eq = new Equals("foo") {
            protected boolean equalsInner(Object obj) {
                return true;
            }
        };
        assertFalse(eq.check(null));
    }

    public void testDifferentClass() {
        final Object owner = "1";
        Object other = new Integer(1);
        Equals eq = new Equals(owner) {
            protected boolean equalsInner(Object obj) {
                return true;
            }
        };
        assertFalse(eq.check(other));
    }

    public void testDifferentHashCodes() {
        class HashCoder {
            private int hc;
            public HashCoder(int hashCode) {
                this.hc = hashCode;
            }

            public boolean equals(Object obj) {
                return new Equals(this) {
                    protected boolean equalsInner(Object other) {
                        return true;
                    }
                }.check(obj);
            }

            public int hashCode() {
                return hc;
            }
        }

        HashCoder hc1a = new HashCoder(1);
        HashCoder hc1b = new HashCoder(1);
        HashCoder hc2 = new HashCoder(2);

        assertTrue(hc1a.equals(hc1b));

        assertFalse(hc1a.equals(hc2));
    }

    public void testEqualsInner() {
        class MyClass {
            private int a;
            public MyClass(int a) {
                this.a = a;
            }

            public boolean equals(Object obj) {
                return new Equals(this) {
                    protected boolean equalsInner(Object other) {
                        equalsInnerCalled = true;
                        return a == ((MyClass) other).a;
                    }
                }.check(obj);
            }

            public int hashCode() {
                return 0;
            }
        }

        MyClass a = new MyClass(1);
        MyClass b = new MyClass(1);
        MyClass c = new MyClass(2);
        equalsInnerCalled = false;
        assertTrue(a.equals(b));
        assertTrue(equalsInnerCalled);

        equalsInnerCalled = false;
        assertFalse(a.equals(c));
        assertTrue(equalsInnerCalled);
    }

    public void testNullOwner() {
        Exception expected = null;
        try {
            new Equals(null) {
                protected boolean equalsInner(Object other) {
                    return false;
                }
            };
        } catch (IllegalArgumentException e) {
            expected = e;
        }
        assertNotNull(expected);
    }
}
