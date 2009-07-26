/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package hotpotato.acceptance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class FactorTest extends TestCase {

    public void testExampleUsage() {
        long numberToFactor = 40;

        Factor f = new Factor(numberToFactor);
        List<Long> factors = f.call();

        List<Long> expected = Arrays.asList(new Long[] { 2l, 2l, 2l, 5l });
        assertEquals(expected, factors);
    }

    public void testFactors() throws Exception {
        long[] factors;

        factors = new long[] { 2, 2 };
        checkFactoring(4, factors);

        factors = new long[] { 2, 2, 5 };
        checkFactoring(20, factors);

        factors = new long[] { 2, 3, 5, 7, 13 };
        checkFactoring(factors);

        factors = new long[] { 2, 2, 2, 7, 13 };
        checkFactoring(factors);

        factors = new long[] { 3, 3, 3, 3, 3, 3, 3, 5, 7, 13 };
        checkFactoring(factors);

        factors = new long[] { 3, 3, 3, 3, 3, 3, 3, 5, 7, 13, 4211 };
        checkFactoring(factors);

        factors = new long[] { 649657, 649657 };
        checkFactoring(649657l * 649657l, factors);
    }

    public void testPrimes() {
        long[] factors;

        factors = new long[] { 2 };
        checkFactoring(2, factors);

        factors = new long[] { 7 };
        checkFactoring(7, factors);

        factors = new long[] { 4211 };
        checkFactoring(4211, factors);

        factors = new long[] { 649657 };
        checkFactoring(649657, factors);
    }

    public void testNegativeNumbers() {
        checkFactoring(-216, new long[] { -1, 2, 2, 2, 3, 3, 3 });
        checkFactoring(-331, new long[] { -1, 331 });
    }

    public void testEdgeCases() throws Exception {
        long[] factors;

        factors = new long[] { 1 };
        checkFactoring(factors);

        factors = new long[] { 0 };
        checkFactoring(0, factors);

        factors = new long[] { 7, 7, 73, 127, 337, 92737, 649657 };
        checkFactoring(Long.MAX_VALUE, factors);

        factors = new long[] { -1, 7, 7, 73, 127, 337, 92737, 649657 };
        checkFactoring(Long.MIN_VALUE + 1, factors);

        // Long.MIN_VALUE is out of range
        // (because the absolute value of Long.MIN_VALUE is not defined)
        // abs(Long.MIN_VALUE + 1) == Long.MAX_VALUE
        try {
            new Factor(Long.MIN_VALUE).call();
        } catch (Exception ingore) {
            // we don't care if it throws or not: this case is undefined
        }
        // we only care that we don't end up in a loop.
    }

    /* this takes 30 seconds to run, but is useful for measuring efficiency */
    public void x_testReallyBigPrime() {
        long pandigitalPalindromicPrime = 1023456987896543201l;
        long[] factors = new long[] { pandigitalPalindromicPrime };
        checkFactoring(pandigitalPalindromicPrime, factors);
    }

    private void assertValueEqualsFactors(long value, List<Long> factors) {
        long result = 1;
        for (long factor : factors) {
            result = result * factor;
        }
        assertEquals(value, result);
    }

    private void checkFactoring(long[] factors) {
        long number = 1;
        for (long factor : factors) {
            number = number * factor;
        }
        checkFactoring(number, factors);
    }

    private void checkFactoring(long value, long[] factors) {
        List<Long> expectedFactors = new ArrayList<Long>();
        if (factors != null) {
            expectedFactors = new ArrayList<Long>();
            for (long factor : factors) {
                expectedFactors.add(factor);
            }
        }

        if (!expectedFactors.isEmpty()) {
            assertValueEqualsFactors(value, expectedFactors);
        }

        List<Long> actualFactors = new Factor(value).call();
        assertValueEqualsFactors(value, actualFactors);

        if (!expectedFactors.isEmpty()) {
            assertEquals(expectedFactors, actualFactors);
        }
    }

}
