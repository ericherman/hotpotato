/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

public abstract class Equals {
    private Object owner;
    
    public Equals(Object owner){
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

        return classCheck(other);
    }
    
    protected final Object owner() {
        return owner;
    }

    /**
     * @deprecated over-ride with extreme caution: using "instanceof" or
     *             class.getName() matching allows for breaking of the Equals
     *             contract.
     */
    protected boolean classConstraintMatch(Object other) {
        return owner.getClass().equals(other.getClass());
    }

    protected abstract boolean classCheck(Object other);
}