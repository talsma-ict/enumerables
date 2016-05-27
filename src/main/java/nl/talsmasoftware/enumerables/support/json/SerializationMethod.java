/*
 * Copyright (C) 2016 Talsma ICT
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
package nl.talsmasoftware.enumerables.support.json;

import nl.talsmasoftware.enumerables.Enumerable;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;

/**
 * Object to represent the Enumerable serialization method: A plain String or a JSON object with various properties,
 * including <code>value</code> and <code>description</code>.
 * <p>
 * Enumerable deserializers should be capable of handling both serialization methods.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public final class SerializationMethod {
    /**
     * Constant for standard String serialization of all enumerable objects, without exceptions.
     */
    public static final SerializationMethod PLAIN_STRING = new SerializationMethod(false);
    /**
     * Constant for standard JSON Object serialization of all enumerable objects, withtout exceptions.
     */
    public static final SerializationMethod JSON_OBJECT = new SerializationMethod(true);

    private final boolean jsonObjectSerialization;
    private final String[] sortedExceptionTypeNames;

    private SerializationMethod(boolean standardObjectSerialization, String... sortedTypeNames) {
        this.jsonObjectSerialization = standardObjectSerialization;
        this.sortedExceptionTypeNames = sortedTypeNames;
    }

    /**
     * This method returns another object representing the enumerable serialization method with specified types
     * as exceptions to the 'rule'. So <code>PLAIN_STRING.except(MyType.class)</code> will serialize
     * <code>OtherType</code> as plain String and <code>MyType</code> as JSON objects.
     * Similarly, <code>JSON_OBJECT.except(MyType.class)</code> will serialize <code>MyType</code> as plain String and
     * <code>OtherType</code> as JSON object.
     * <p>
     * This method will not modify the current object, but return a new value instead.
     *
     * @param exceptionTypes The enumerable exception types that will be serialized as exception to the default 'rule'.
     * @return An enumerable serialization method that will serialize the specified types different from the general rule.
     */
    public SerializationMethod except(Class<? extends Enumerable>... exceptionTypes) {
        SortedSet<String> types = new TreeSet<String>(asList(this.sortedExceptionTypeNames));
        boolean changed = false;
        if (exceptionTypes != null) {
            for (Class<?> exceptionType : exceptionTypes) {
                if (exceptionType != null && types.add(exceptionType.getName())) {
                    changed = true;
                }
            }
        }
        return changed
                ? new SerializationMethod(jsonObjectSerialization, types.toArray(new String[types.size()]))
                : this;
    }

    private boolean isException(String typeName) {
        return sortedExceptionTypeNames.length > 0 && binarySearch(sortedExceptionTypeNames, typeName) >= 0;
    }

    /**
     * Evaluates whether the requested {@link Enumerable} type should be serialized as a JSON object (<code>true</code>)
     * or as a plain String (<code>false</code>).
     *
     * @param enumerableType The type of enumerable object to determine the serialization method for.
     * @return <code>true</code> if enumerable objects of the requested type should be serialized as JSON objects
     * (with <code>value</code> and <code>description</code> fields), or <code>false</code> if they should be serialized
     * as plain String values.
     */
    public boolean serializeAsJsonObject(Class<? extends Enumerable> enumerableType) {
        if (enumerableType == null) throw new IllegalArgumentException("Enumerable type is required.");
        return jsonObjectSerialization != isException(enumerableType.getName());
    }

    @Override
    public int hashCode() {
        return 31 * Boolean.valueOf(jsonObjectSerialization).hashCode() + Arrays.hashCode(sortedExceptionTypeNames);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof SerializationMethod
                && this.jsonObjectSerialization == ((SerializationMethod) other).jsonObjectSerialization
                && Arrays.equals(this.sortedExceptionTypeNames, ((SerializationMethod) other).sortedExceptionTypeNames));
    }

    /**
     * @return String representation {@code "JSON Object"} or {@code "Plain String"} plus any exception types if specified.
     */
    public String toString() {
        String result = jsonObjectSerialization ? "JSON Object" : "Plain String";
        if (sortedExceptionTypeNames.length > 0) {
            StringBuilder builder = new StringBuilder(result).append(", except {");
            String sep = "";
            for (String uitzondering : sortedExceptionTypeNames) {
                builder.append(sep).append(uitzondering.substring(uitzondering.lastIndexOf('.') + 1));
                sep = ", ";
            }
            result = builder.append('}').toString();
        }
        return result;
    }

}
