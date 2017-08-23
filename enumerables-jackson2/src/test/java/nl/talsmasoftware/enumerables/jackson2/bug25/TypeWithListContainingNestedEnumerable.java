/*
 * Copyright 2016-2017 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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