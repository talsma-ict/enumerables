package nl.talsmasoftware.enumerables.jackson2.bug25;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kroon.r on 28-06-2017.
 */
public class TypeWithEnumerableList {

    public List<Team> teammembers;
    public String stringValue;

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {teammembers, stringValue});
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof TypeWithEnumerableList
                && TypeWithEnumerable.equals(teammembers, ((TypeWithEnumerableList) other).teammembers)
                && TypeWithEnumerable.equals(stringValue, ((TypeWithEnumerableList) other).stringValue)
        );
    }

}
