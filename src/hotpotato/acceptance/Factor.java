/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class Factor implements Serializable, Callable<List<Long>> {
    private static final long serialVersionUID = 1L;

    long x;

    public Factor(long x) {
        if (x == Long.MIN_VALUE) {
            throw new IllegalArgumentException("Long.MIN_VALUE");
        }
        this.x = x;
    }

    public List<Long> call() {
        if (x == 0) {
            return Collections.singletonList(0l);
        }

        List<Long> factors = new ArrayList<Long>();

        if (x < 0) {
            factors.add(-1l);
            x = -x;
        }

        // perhaps only check primes? (rather than 2 and Odds)
        long limit = (long) (Math.sqrt(x) + 1);
        factorOut(2, factors);
        for (long i = 3; i <= limit && x != 1; i += 2) {
            factorOut(i, factors);
        }

        // PRIME!
        if (factors.isEmpty() || (factors.size() == 1 && factors.contains(-1l))) {
            factors.add(x);
        }

        return factors;
    }

    void factorOut(long i, List<Long> factors) {
        while (x % i == 0) {
            factors.add(i);
            x = x / i;
        }
    }

}
