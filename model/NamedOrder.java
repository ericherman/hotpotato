/*
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.model;

/**
 * A <code>SimpleOrder</code> with a meaningful <code>equals()</code>
 * One NamedOrder is equal to another if their names are equal.
 */
public class NamedOrder extends SimpleOrder {
    private String name;

    public NamedOrder(String name) {
        if (name == null)
            throw new IllegalArgumentException("null name String");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!getClass().equals(o.getClass()))
            return false;

        NamedOrder other = (NamedOrder) o;
        return getName().equals(other.getName());
    }

    public final int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return getName();
    }
}
