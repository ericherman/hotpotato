/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

import java.io.*;

public class Equals implements Serializable {
    private static final long serialVersionUID = 1L;
    private Equals.Inner owner;
    
    public Equals(Equals.Inner owner){
        this.owner = owner;
    }

    public boolean check(Object other) {
        if (owner == other) {
            return true;
        }
        if (owner == null || other == null) {
            return false;
        }

        if (!classConstraintMatch(other)) {
            return false;
        }

        return owner.equalsInner(other);
    }

    /**
     * @deprecated over-ride with extreme caution: using "instanceof" or
     *             class.getName() matching allows for breaking of the Equals
     *             contract.
     */
    protected boolean classConstraintMatch(Object other) {
        return owner.getClass().equals(other.getClass());
    }

    public static interface Inner {
        boolean equalsInner(Object other);
    }
}