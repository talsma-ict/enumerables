/*
 * Copyright 2016-2018 Talsma ICT
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
package nl.talsmasoftware.enumerables.jackson2;

import nl.talsmasoftware.enumerables.Enumerable;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;

/**
 * Object that encapsulates the JSON serialization method. There are two possibilities:
 * as {@link #AS_OBJECT JSON object} or {@link #AS_STRING String primitive}.
 * <p>
 * {@linkplain Enumerable} deserializers are capable of handling both methods.
 *
 * @author Sjoerd Talsma
 */
public final class SerializationMethod {
    /**
     * Constant for default String-serialization for all {@link Enumerable} types.
     * Please see {@link #except(Class)} to add exception types that should be serialized
     * as {@code JSON objects}.
     */
    public static final SerializationMethod AS_STRING =
            new SerializationMethod(false, null);

    /**
     * Constant for default JSON Object-serialization for all {@link Enumerable} types.
     * Please see {@link #except(Class)} to add exception types that should be serialized
     * as primitive {@code JSON strings}.
     */
    public static final SerializationMethod AS_OBJECT =
            new SerializationMethod(true, null);

    private final boolean objectSerializationByDefault;
    private final String[] sortedExceptionTypes;

    private SerializationMethod(boolean objectSerializationByDefault, SortedSet<String> exceptionTypes) {
        this.objectSerializationByDefault = objectSerializationByDefault;
        this.sortedExceptionTypes = exceptionTypes == null ? new String[0]
                : exceptionTypes.toArray(new String[exceptionTypes.size()]);
    }

    /**
     * Whether {@link Enumerable} types must be rendered as JSON object by default (containing 'value' + other fields)
     * or as {@link String} literal otherwise.
     * <p>
     * There can be exceptions to this default value; see {@link #except(Class[])} to specify these exceptions.
     *
     * @return {@code true} indien waardenlijsten standaard als waarde + omschrijving JSON object moeten worden
     * geserialiseerd, {@code false} indien ze standaard als waarde string moeten worden gerepresenteerd.
     * @see #serializeAsObject(Class)
     */
    public boolean isObjectSerializationByDefault() {
        return objectSerializationByDefault;
    }

    /**
     * Adds one or more exceptions to the {@link #isObjectSerializationByDefault()} default serialization method}
     * for certain {@link Enumerable} types.
     *
     * @param exceptionTypes The enumerable types that should be serialized as exception to the default method.
     * @return The serialization method with these added exceptions.
     * @deprecated Due to unchecked generics array creation at the caller location
     */
    public SerializationMethod except(Class<? extends Enumerable>... exceptionTypes) {
        return except(Arrays.asList(exceptionTypes));
    }

    /**
     * Adds one exception to the {@link #isObjectSerializationByDefault()} default serialization method}
     * for a certain {@link Enumerable} subtype.
     *
     * @param exceptionType The enumerable type that should be serialized as exception to the default method.
     * @return The serialization method with this added exception.
     * @see #except(Iterable)
     */
    @SuppressWarnings("unchecked")
    public SerializationMethod except(Class<? extends Enumerable> exceptionType) {
        Set<?> singleton = Collections.singleton(exceptionType);
        return except((Iterable<Class<? extends Enumerable>>) singleton);
    }

    /**
     * Adds zero or more exceptions to the {@link #isObjectSerializationByDefault()} default serialization method}
     * for certain {@link Enumerable} types.
     *
     * @param exceptionTypes The enumerable types that should be serialized as exception to the default method.
     * @return The serialization method with these added exceptions.
     */
    public SerializationMethod except(Iterable<Class<? extends Enumerable>> exceptionTypes) {
        final SortedSet<String> sortedExceptionSet = new TreeSet<String>(asList(this.sortedExceptionTypes));
        for (Class<? extends Enumerable> exceptionType : exceptionTypes) {
            if (exceptionType != null) sortedExceptionSet.add(exceptionType.getName());
        }
        return this.sortedExceptionTypes.length == sortedExceptionSet.size() ? this
                : new SerializationMethod(isObjectSerializationByDefault(), sortedExceptionSet);
    }

    private boolean isException(Class<? extends Enumerable> enumerableType) {
        return enumerableType != null
                && sortedExceptionTypes.length > 0
                && binarySearch(sortedExceptionTypes, enumerableType.getName()) >= 0;
    }

    /**
     * Determine whether the requested {@code enumerableType} should be serialized as a JSON object or not (i.e. as
     * primitive String value).
     *
     * @param enumerableType The Enumerable type to be serialized.
     * @return {@code true} if the enumerable should be serialized as JSON object containing value and other fields, or
     * {@code false} if it should be serialized as primitive String.
     * @see #isObjectSerializationByDefault()
     */
    public boolean serializeAsObject(Class<? extends Enumerable> enumerableType) {
        return isObjectSerializationByDefault() != isException(enumerableType);
    }

    @Override
    public int hashCode() {
        return 31 * (objectSerializationByDefault ? 1231 : 1237) + Arrays.hashCode(sortedExceptionTypes);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof SerializationMethod
                && this.objectSerializationByDefault == ((SerializationMethod) other).objectSerializationByDefault
                && Arrays.equals(this.sortedExceptionTypes, ((SerializationMethod) other).sortedExceptionTypes));
    }

    /**
     * @return String representation {@code "As object"} of {@code "As string"} plus any exceptions.
     */
    public String toString() {
        String result = isObjectSerializationByDefault() ? "As object" : "As string";
        if (sortedExceptionTypes.length > 0) {
            StringBuilder builder = new StringBuilder(result).append(", except [");
            String sep = "";
            for (String exceptionType : sortedExceptionTypes) {
                builder.append(sep).append(exceptionType.substring(exceptionType.lastIndexOf('.') + 1));
                sep = ", ";
            }
            result = builder.append(']').toString();
        }
        return result;
    }

}
