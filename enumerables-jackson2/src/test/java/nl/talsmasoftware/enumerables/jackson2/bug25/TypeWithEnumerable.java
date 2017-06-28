package nl.talsmasoftware.enumerables.jackson2.bug25;

import java.util.Arrays;

/**
 * Created by kroon.r on 28-06-2017.
 */
public class TypeWithEnumerable {

    public Team teammember;
    public String otherValue;

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{otherValue, teammember});
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof TypeWithEnumerable
                && equals(otherValue, ((TypeWithEnumerable) other).otherValue)
                && equals(teammember, ((TypeWithEnumerable) other).teammember)
        );
    }

    static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

}
