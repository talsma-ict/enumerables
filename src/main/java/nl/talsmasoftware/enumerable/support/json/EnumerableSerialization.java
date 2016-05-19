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
package nl.talsmasoftware.enumerable.support.json;

import nl.talsmasoftware.enumerable.Enumerable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static nl.talsmasoftware.enumerable.support.json.EnumerableSerialization.SerializationMethod.OBJECT;
import static nl.talsmasoftware.enumerable.support.json.EnumerableSerialization.SerializationMethod.STRING;

/**
 * Object that abstracts the JSON serialization method: as plain string or json object
 * (containing <code>value</code> and <code>description</code> properties).
 * <p>
 * The provided deserializers know how to handle both known {@link SerializationMethod serialization methods}.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableSerialization {

    /**
     * The serialization method to use.
     * <p>
     * The library provides just <code>"string"</code> and <code>"object"</code>, but theoretically you <em>could</em>
     * parse your own and provide a different serialization algorithm for it yourself.
     */
    public static final class SerializationMethod extends Enumerable {
        /**
         * Constant that indicates Plain String serialization for {@link Enumerable} object instances.
         */
        public static final SerializationMethod STRING = new SerializationMethod("String");
        /**
         * Constant that indicates JSON Object serialization for {@link Enumerable} object instances.
         */
        public static final SerializationMethod OBJECT = new SerializationMethod("Object");

        private SerializationMethod(String value) {
            super(value);
        }
    }

    /**
     * Constant for plain string serialization of all enumerable objects.
     */
    public static final EnumerableSerializationWithExceptions PLAIN_STRING = new EnumerableSerializationWithExceptions(STRING) {
        public EnumerableSerialization except(Class<? extends Enumerable>... enumerableTypes) {
            return withSpecificMethod(OBJECT, enumerableTypes);
        }
    };

    /**
     * Constant for JSON Object serialization of all enumerable objects.
     */
    public static final EnumerableSerializationWithExceptions JSON_OBJECT = new EnumerableSerializationWithExceptions(OBJECT) {
        public EnumerableSerialization except(Class<? extends Enumerable>... enumerableTypes) {
            return withSpecificMethod(STRING, enumerableTypes);
        }
    };

    private final SerializationMethod defaultSerializationMethod;
    private final Map<String, SerializationMethod> explicitSerializationMethods;

    /**
     * Private constructor to prevent outside-instances or unwanted subclasses..
     *
     * @param defaultSerializationMethod The default serialization method to be used (required, non-<code>null</code>).
     */
    private EnumerableSerialization(SerializationMethod defaultSerializationMethod) {
        this(defaultSerializationMethod, Collections.<String, SerializationMethod>emptyMap());
    }

    /**
     * Private constructor to prevent outside-instances or unwanted subclasses..
     *
     * @param defaultSerializationMethod   The default serialization method to be used (required, non-<code>null</code>).
     * @param explicitSerializationMethods The enumerable type names that deviate from the default serialization method.
     */
    private EnumerableSerialization(SerializationMethod defaultSerializationMethod,
                                    Map<String, SerializationMethod> explicitSerializationMethods) {
        if (defaultSerializationMethod == null) {
            throw new IllegalArgumentException("No default serialization method specified!");
        } else if (explicitSerializationMethods == null) {
            throw new IllegalArgumentException("Explicit serialization methods were null!");
        }
        this.defaultSerializationMethod = defaultSerializationMethod;
        this.explicitSerializationMethods = explicitSerializationMethods;
    }

    /**
     * Returns an {@link EnumerableSerialization} instance with one or more exceptions to the default object
     * {@link SerializationMethod serialization method} for particular {@link Enumerable enumerable types}.
     * <p>
     * This method should <strong>not</strong> change the current object but return new object instances instead.
     *
     * @param explicitSerializationMethod The explicitly chosen serialization method.
     * @param explicitEnumerableTypes     De Enumerable types that should be serialized using the non-default
     *                                    explicit serialization method.
     * @return A new instance of this serialization method with certain enumerable types that should be serialized
     * using the non-default method.
     */
    public EnumerableSerialization withSpecificMethod(
            SerializationMethod explicitSerializationMethod, Class<? extends Enumerable>... explicitEnumerableTypes) {
        if (explicitSerializationMethod == null) {
            throw new IllegalArgumentException("Deviating serialization method is null.");
        } else if (explicitEnumerableTypes == null || explicitEnumerableTypes.length == 0) {
            return this;
        }

        Map<String, SerializationMethod> copyMap = new LinkedHashMap<String, SerializationMethod>(explicitSerializationMethods);
        for (Class<?> enumerableType : explicitEnumerableTypes) {
            if (enumerableType != null) {
                if (defaultSerializationMethod.equals(explicitSerializationMethod)) {
                    copyMap.remove(enumerableType.getName());
                } else {
                    copyMap.put(enumerableType.getName(), explicitSerializationMethod);
                }
            }
        }
        if (copyMap.isEmpty()) {
            return STRING.equals(explicitSerializationMethod) ? PLAIN_STRING
                    : OBJECT.equals(explicitSerializationMethod) ? JSON_OBJECT
                    : new EnumerableSerialization(explicitSerializationMethod);
        }
        return copyMap.equals(explicitSerializationMethods) ? this :
                new EnumerableSerialization(explicitSerializationMethod, copyMap);
    }

    /**
     * Determines the serialization method for the requested {@link Enumerable enumerable type}.
     *
     * @param enumerableType The enumerable type te determine the serialization method for (required, non-<code>null</code>).
     * @return The applicable serialization method (non-<code>null</code>).
     */
    public SerializationMethod serializationMethodFor(Class<? extends Enumerable> enumerableType) {
        if (enumerableType == null) {
            throw new IllegalArgumentException("Enumerable type was null.");
        }
        final SerializationMethod explicit = explicitSerializationMethods.get(enumerableType.getName());
        return explicit == null ? defaultSerializationMethod : explicit;
    }

    @Override
    public int hashCode() {
        return 31 * defaultSerializationMethod.hashCode() + explicitSerializationMethods.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof EnumerableSerialization
                && this.defaultSerializationMethod.equals(((EnumerableSerialization) other).defaultSerializationMethod)
                && this.explicitSerializationMethods.equals(((EnumerableSerialization) other).explicitSerializationMethods));
    }

    /**
     * @return String representation of the enumerable serialization.
     */
    public String toString() {
        String result = "EnumerableSerialization as " + defaultSerializationMethod.getDescription();
        if (!explicitSerializationMethods.isEmpty()) {
            result += ", except " + explicitSerializationMethods;
        }
        return result;
    }

    /**
     * Class that allows callers to specify exceptions that need to be serialized 'the other way', where 'the other way'
     * has some known meaning.
     */
    public static abstract class EnumerableSerializationWithExceptions extends EnumerableSerialization {
        private EnumerableSerializationWithExceptions(SerializationMethod defaultSerializationMethod) {
            super(defaultSerializationMethod);
        }

        /**
         * Returns the serialization instance with the specified enumerable types not serialized in the default way.
         *
         * @param enumerableTypes The enumerable types that will be serialized as JSON Objects instead of plain Strings.
         * @return The serialization with the specified exceptions.
         */
        public abstract EnumerableSerialization except(Class<? extends Enumerable>... enumerableTypes);
    }
}
