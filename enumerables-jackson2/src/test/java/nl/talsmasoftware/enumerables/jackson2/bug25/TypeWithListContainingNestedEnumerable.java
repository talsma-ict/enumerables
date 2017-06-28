package nl.talsmasoftware.enumerables.jackson2.bug25;


import java.util.Arrays;
import java.util.List;

/**
 * Created by kroon.r on 28-06-2017.
 */
public class TypeWithListContainingNestedEnumerable {

    public List<TypeWithEnumerable> teammembers;
    public String stringValue;

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{teammembers, stringValue});
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TypeWithListContainingNestedEnumerable
                && TypeWithEnumerable.equals(teammembers, ((TypeWithListContainingNestedEnumerable) other).teammembers)
                && TypeWithEnumerable.equals(stringValue, ((TypeWithListContainingNestedEnumerable) other).stringValue)
        );
    }

}
