package hotpotato.util;

public class Equals {

    public interface Block {
        public boolean equal(Object left, Object right);
    }

    public boolean equal(Object left, Object right, Block block) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        if (classConstraintMisMatch(left, right)) {
            return false;
        }

        return block.equal(left, right);
    }

    /** over-ride for "instanceof" or class.getName() matching */
    protected boolean classConstraintMisMatch(Object left, Object right) {
        return left.getClass() != right.getClass();
    }
}