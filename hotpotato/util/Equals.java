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

    /** over-ride for "instanceof" or class.getName() matching */
    protected boolean classConstraintMatch(Object left, Object right) {
        return left.getClass().equals(right.getClass());
    }

    protected abstract boolean classCheck(Object left, Object right);
}