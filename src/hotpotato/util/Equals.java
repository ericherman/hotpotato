/**
 * Copyright (C) 2003 by Eric Herman.
 * For licensing information see GnuGeneralPublicLicenseVersion2.txt
 *  or http://www.fsf.org/licenses/gpl.txt 
 *  or for alternative licensing, email Eric Herman: eric AT rnd DOT cx 
 */
package hotpotato.util;

public abstract class Equals {

    public boolean check(Object left, Object right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        if (!classConstraintMatch(left, right)) {
            return false;
        }

        return classCheck(left, right);
    }

    /**
     * @deprecated over-ride with extreme caution: using "instanceof" or
     *             class.getName() matching allows for breaking of the Equals
     *             contract.
     */
    protected boolean classConstraintMatch(Object left, Object right) {
        return left.getClass().equals(right.getClass());
    }

    protected abstract boolean classCheck(Object left, Object right);
}