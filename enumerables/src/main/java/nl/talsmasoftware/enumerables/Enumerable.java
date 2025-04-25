/*
 * Copyright 2016-2025 Talsma ICT
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
package nl.talsmasoftware.enumerables;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Integer.signum;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * The <code>Enumerable</code> class is <strong>very</strong> similar to a standard Java {@link Enum} type.
 * However, it has one special feature that makes it more suitable for using in an API that you need to maintain.
 * <p>
 * Ever have an actual {@link Enum} in an exposed API? Then have the customer come up with an additional value for
 * that {@link Enum} that should also be supported?
 * <strong>*Bang!*</strong> there goes your API compatibility. You will either have to tel all your existing customers
 * <em>"sorry, the api is now broken"</em> or create a new version <strong>beside</strong> the existing API and declare
 * the old one deprecated. However, you'll still have to think about how to represent the additional value in the old
 * version or create a special exception for this case.<br>
 * Nasty in any conceivable scenario.
 * <p>
 * That is exactly the reason we've created the <code>Enumerable</code> type. It is similar to {@link Enum} in that it
 * represents a number of <em>known constants</em> but also offers a possibility to represent
 * <em>yet unknown additional values</em> by parsing them. In API terms, you basically make a slightly lighter promise
 * to your customer: I currently know of these possible values which have meaning in the API, but please be prepared to
 * receive any 'other' value as well. Those other values may actually even have meaning to the receiver when they are
 * introduced but allow for a stable API definition. Introducing a new value in a new API release does not break the
 * existing contract since the consumer should have anticipated the new value.
 * <p>
 * The parsing functionality first compares the given value with all known constants of the specified Enumerable type,
 * so normally you will get a constant reference if possible, and a new object instance only for non-constant values.
 * The same goes for serialization and deserialization.
 * <p>
 * TODO: Talk about values(), ordinal(), and other enum concepts.
 * TODO: Document an example of how using it.
 *
 * @author Sjoerd Talsma
 */
public abstract class Enumerable implements Comparable<Enumerable>, Serializable {

    /**
     * The value of this abstract Enumerable type.
     */
    private final String value;

    /**
     * Constructor that should only be used by a private (!) constructor of the subclass.
     *
     * @param value The value of this Enumerable object instance.
     */
    protected Enumerable(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value of type " + getClass().getSimpleName() + " was <null>.");
        }
        this.value = value;
    }

    /**
     * Returns the {@link Enum#ordinal() 'enum ordinal'} of this enumerable value.
     * For a constant value, this method returns the index of this object within the {@link #values(Class)} array.
     * <p>
     * For non-constant values, this method will always return {@link Integer#MAX_VALUE}. This has several reasons,
     * but the most obvious one is that the {@link #compareTo(Enumerable) natural order} is determined by this
     * <code>ordinal</code> value, automatically sorting all constants before any non-constant parsed values.
     *
     * @return The 'ordinal' of this value in the array of constant values,
     * or <code>Integer.MAX_VALUE</code> if this enumerable object does not match any of the defined constants in
     * the enumerable subtype.
     * @see Integer#MAX_VALUE
     */
    public final int ordinal() {
        return _nameAndOrdinal().ordinal;
    }

    /**
     * @return The constant 'name' of this enumerable object in case it matches a defined constant in the enumerable
     * subtype, otherwise <code>null</code>.
     */
    public final String name() {
        return _nameAndOrdinal().name;
    }

    /**
     * @return The value of this Enumerable object instance (non-<code>null</code>).
     */
    public final String getValue() {
        return value;
    }

    /**
     * @return hashcode for this enumerable object.
     */
    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    /**
     * The compareTo implementation for any {@link Enumerable} instance uses the following strategy:
     * <ol>
     * <li>First, the concrete enumerable type is compared, based on class name.</li>
     * <li>Then, the {@link #ordinal() ordinals} of the two enumerable objects are compared, making sure that
     * constants are sorted in the order they were encountered in the class definition.<br>
     * Also, this makes sure that any constant value is sorted before any non-constant value.</li>
     * <li>Lastly, for two non-constant enumerables, their {@link #getValue() values} are compared in two steps:
     * first case-insenstive, with a case-sensitive follow-up comparison for zero outcome.<br>
     * This allows for case-insensitive sorting behaviour, while still defining zero-comparison to be exact equality.</li>
     * </ol>
     *
     * @param other The other enumerable object to compare with (required, non-<code>null</code>).
     * @return The result of the comparison: A negative integer if this enumerable is considered less than the other,
     * zero (<code>0</code>) if it is considered equal to the other,
     * and a positive integer if it is considered greater than the other enumerable.
     * @throws NullPointerException when the <code>other</code> enumerable is <code>null</code>.
     */
    public int compareTo(Enumerable other) {
        if (other == null) throw new NullPointerException("Cannot compare with enumerable <null>.");
        int comparison = getClass().getName().compareTo(other.getClass().getName());
        if (comparison == 0) {
            final int ordinal = ordinal();
            comparison = ordinal - other.ordinal();
            if (comparison == 0 && ordinal == NameAndOrdinal.NONE.ordinal) { // Both are non-constant: compare values!
                // 2-step compare: Preferably sort case-insensitive. However, do not declare case differences as equal:
                comparison = value.compareToIgnoreCase(other.value);
                if (comparison == 0) comparison = value.compareTo(other.value);
            }
        }
        return signum(comparison);
    }

    /**
     * Equality is implemented as either instance equality (which happens a lot with constants)
     * or otherwise a zero {@link #compareTo(Enumerable)} outcome.
     *
     * @param other The other object to compare equality with.
     * @return Whether this enumerable instance is equal to the specified object based on type and value.
     */
    @Override
    public final boolean equals(Object other) {
        return this == other || (other instanceof Enumerable && 0 == compareTo((Enumerable) other));
    }

    /**
     * The implementation of <code>toString()</code> is importantly different from Java's {@link Enum} implementation,
     * which by default returns the {@link #name() name} of the enumeration constant.<br>
     * The problem with this is, is that lazy developers may start depending on that behaviour and comparing with
     * implicitly 'toString'-ed values where they <em>should</em> compare to the constant's {@link Enum#name() name}
     * instead. This increases the likelyhood of bugs.
     * <p>
     * Oh, and of course; we don't have any guarantees that our {@link #name()} will yield any value at all!
     *
     * @return The string representation of this enumerable object instance which will contain the simple class name,
     * the contained <code>value</code> and the constant's <code>name</code> if applicable and different from
     * the <code>value</code>.
     * @see #name()
     * @see #getValue()
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getSimpleName()).append('{');
        String name = name();
        if (name != null && !name.equals(value)) {
            result.append("name=").append(name).append(", ");
        }
        return result.append("value=").append(value).append('}').toString();
    }

    /**
     * @return Re-parse an enumerable object after deserialization to ensure that constants remain constant.
     */
    protected Object readResolve() {
        final Enumerable reParsed = parse(getClass(), this.value);
        return reParsed != null && reParsed.name() != null ? reParsed : this; // name found? Return the parsed constant.
    }

    /**
     * Finds all public constants of the requested enumerable subtype that have been declared in the class of the
     * enumerable itself.
     * <p>
     * These constants must be <strong>public final static</strong> fields.
     *
     * @param <E>            The actual non-abstract enumerable subtype to obtain constants for.
     * @param enumerableType The actual non-abstract enumerable subtype to obtain constants for.
     * @return All declarered public constants of the enumerable subtype.
     */
    public static <E extends Enumerable> E[] values(Class<E> enumerableType) {
        return _rawValues(enumerableType).clone();
    }

    /**
     * This method is comparable with the {@link Enum#valueOf(Class, String)} method.
     * <p>
     * This method returns the constant with the specified <code>name</code>.
     * If there is no enumerable constant of the requested <code>type</code> found by that <code>name</code>,
     * the method will throw an {@link ConstantNotFoundException exception}.
     * <p>
     * Please note that this method looks at the constant {@link #name() name} and <strong>not</strong>
     * the {@link #getValue() value} of this enumerable object. To obtain an enumerable object from a
     * specific {@link #getValue() value}, please use the {@link #parse(Class, CharSequence) parse} method instead.
     * That method will not throw any exceptions for yet-unknown values, but attempt to instantiate a
     * new enumerable instance in that case.
     *
     * @param <E>  The actual subtype of <code>Enumerable</code> to return the named constant value for.
     * @param type The actual subtype of <code>Enumerable</code> to return the named constant value for.
     * @param name The name of the enumerable constant to return (as declared in the code).
     * @return The enumerable constant with the requested name.
     * @throws ConstantNotFoundException in case there is no such constant defined.
     * @see #parse(Class, CharSequence)
     */
    public static <E extends Enumerable> E valueOf(Class<E> type, CharSequence name)
            throws ConstantNotFoundException {
        if (name != null) {
            final String nameString = name.toString();
            for (E enumerable : _rawValues(type)) {
                if (nameString.equals(enumerable.name())) {
                    return enumerable;
                }
            }
        }
        throw new ConstantNotFoundException(type, name);
    }

    /**
     * This method parses the specified value and tries to match it to the found enumerable constants
     * of the specified type. In case a corresponding constant is found, its object reference is returned.
     * In case no constant with a matching value is found, the method attempts to instantiate a new instance of the
     * given enumerable <code>type</code> by searching for a single-{@link String} constructor.
     *
     * @param <E>   The actual subtype of <code>Enumerable</code> to return the specified value of.
     * @param type  The actual subtype of <code>Enumerable</code> to return the specified value of.
     * @param value The enumerable value to be parsed.
     * @return An enumerable object of the requested type containing the specified <code>value</code>,
     * or <code>null</code> if the given <code>value</code> was <code>null</code> itself.
     */
    public static <E extends Enumerable> E parse(Class<E> type, CharSequence value) {
        return parse(type, value, null);
    }

    /**
     * <code>null</code>-safe helper method that prints the value of any enumerable.
     *
     * @param enumerable The enumerable object to return the value of.
     * @return The value of the enumerable or <code>null</code> in case the enumerable itself was <code>null</code>.
     * @see #parse(Class, CharSequence)
     */
    public static String print(Enumerable enumerable) {
        return enumerable == null ? null : enumerable.value;
    }

    /**
     * A factory method for sets of Enumerable values.
     * <p>
     * It is recommended to use this method for constant sets of enumerable values to allow the library to optimize
     * the type of set to be returned.
     * <p>
     * The resulting set will be unmodifiable.
     *
     * @param <E>    The actual non-abstract Enumerable type.
     * @param values The enumerable values to be represented as a Set.
     * @return The unmodifiable set of the specified Enumerable values.
     */
    public static <E extends Enumerable> Set<E> setOf(E... values) {
        if (values == null || values.length == 0) {
            return emptySet();
        } else if (values.length == 1) {
            return singleton(values[0]);
        }
        // For now this 'just works', maybe we can implement a set that is based on the EnumSet idea.
        return unmodifiableSet(new LinkedHashSet<E>(asList(values)));
    }

    /**
     * Helper that can return fixed instances from known constants (comparable to enum parsing).
     *
     * @param <E>     The actual non-abstract Enumerable type.
     * @param type    The enumerable subtype of the value to be parsed (to obtain constants).
     * @param value   The value to be parsed.
     * @param factory A 'factory' implementation to create the new enumerable value instance with if no constant is
     *                found.
     * @return The parsed enumerable value or <code>null</code> if the parameter <code>value</code> was <code>null</code>.
     */
    protected static <E extends Enumerable> E parse(Class<E> type, CharSequence value, Callable<E> factory) {
        E parsed = null;
        if (value != null) {
            String valueStr = value.toString();
            for (E constante : _rawValues(type)) {
                if (((Enumerable) constante).value.equals(valueStr)) {
                    parsed = constante;
                    break;
                }
            }
            if (parsed == null) {
                try {
                    parsed = factory != null ? factory.call() : _callStringConstructor(type, valueStr);
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Could not create new \"%s\" object with value \"%s\".",
                            type.getName(), valueStr), e);
                }
            }
        }
        return parsed;
    }

    // Class constants, all private:
    private static final long serialVersionUID = 1L;
    // Map from concrete subclass type to array of reflected constants.
    private static final ConcurrentMap<String, Object> CONSTANTS = new ConcurrentHashMap<String, Object>();

    /**
     * <ul>
     * <li>If and only if a constant was detected for this value, the constants name and ordinal.</li>
     * <li>Otherwise {@code NONE}.</li>
     * <li>Or {@code null} in case the type has not yet been reflected.</li>
     * </ul>
     */
    @SuppressWarnings("java:S3077") // NameAndOrdinal is immutable, so volatile is valid here.
    private transient volatile NameAndOrdinal _nameAndOrdinal = null;

    /**
     * @return Once-only calculated <code>NameAndOrdinal</code> combination, non-<code>null</code>.
     */
    private NameAndOrdinal _nameAndOrdinal() {
        if (_nameAndOrdinal == null) {
            Enumerable[] values = _rawValues(getClass());
            for (int i = 0; i < values.length; i++) {
                if (value.equals(values[i].value)) {
                    _nameAndOrdinal = new NameAndOrdinal(i, values[i].name());
                    return _nameAndOrdinal;
                }
            }
            _nameAndOrdinal = NameAndOrdinal.NONE;
        }
        return _nameAndOrdinal;
    }

    /**
     * Returns the actual raw (un-cloned) array with constants for the requested type.
     * This method saves a clone, but is therefore only intended for internal use and the resulting array may
     * absolutely <strong>not</strong> be manipulated by callers!
     *
     * @param <E>            The actual enumerable type.
     * @param enumerableType The enumerable type to obtain constant values of.
     * @return All declarered public constants of the requested enumerable subtype.
     */
    @SuppressWarnings("unchecked")
    private static <E extends Enumerable> E[] _rawValues(final Class<E> enumerableType) {
        if (enumerableType == null) throw new IllegalArgumentException("Enumerable type is <null>.");
        final String enumerableTypeName = enumerableType.getName();
        E[] values = (E[]) CONSTANTS.get(enumerableTypeName);
        if (values == null) {
            final List<E> constants = new ArrayList<E>();
            for (Field field : enumerableType.getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                if (isPublic(modifiers) && isStatic(modifiers) && isFinal(modifiers)
                        && enumerableType.isAssignableFrom(field.getType())) {
                    try {
                        final E foundConstant = (E) field.get(null);
                        ((Enumerable) foundConstant)._nameAndOrdinal =
                                new NameAndOrdinal(constants.size(), field.getName());
                        constants.add(foundConstant);
                    } catch (IllegalAccessException iae) {
                        throw new IllegalStateException(String.format("Reading constant \"%s.%s\" was not allowed!",
                                enumerableTypeName, field.getName()), iae);
                    }
                }
            }
            CONSTANTS.putIfAbsent(enumerableTypeName,
                    constants.toArray((E[]) Array.newInstance(enumerableType, constants.size())));
            values = (E[]) CONSTANTS.get(enumerableTypeName);
        }
        return values;
    }

    /**
     * Instantiates a 'new' enumerable object instance with the given constructor argument.
     * <p>
     * Since we guarantee constant instances to be singletons (within the same ClassLoader), this should obviously only
     * be called for values for which it has been established that they do <strong>not</strong> correspond to an
     * existing constant value for this class!
     *
     * @param type  The type of enumerable object to create.
     * @param value The value of the enumerable object.
     * @return The newly-instantiated enumerable (non-constant) object.
     */
    private static <T> T _callStringConstructor(Class<T> type, String value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = type.getDeclaredConstructor(String.class);
        synchronized (constructor) {
            final boolean accessible = constructor.isAccessible();
            try {
                if (!accessible) constructor.setAccessible(true);
                return constructor.newInstance(value);
            } finally {
                if (!accessible) constructor.setAccessible(false);
            }
        }
    }

    /**
     * Internal class to represent name and ordinal to be able to simulate standard Enum behaviour.
     */
    private static final class NameAndOrdinal {
        private static final NameAndOrdinal NONE = new NameAndOrdinal(Integer.MAX_VALUE, null);
        private final int ordinal;
        private final String name;

        private NameAndOrdinal(int ordinal, String name) {
            this.ordinal = ordinal;
            this.name = name;
        }
    }

    /**
     * Exception that is thrown by the {@link Enumerable#valueOf(Class, CharSequence)} method when an enumerable
     * constant is requested that could not be found.
     *
     * @author Sjoerd Talsma
     */
    public static class ConstantNotFoundException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor. Creates a new exception telling the caller that a bad constant name was requested.
         *
         * @param enumerableType The type of Enumerable that was requested.
         * @param constantName   The name of the Enumerable constant that was requested.
         */
        public ConstantNotFoundException(Class<? extends Enumerable> enumerableType, CharSequence constantName) {
            this(String.format("No Enumerable constant \"%s.%s\" found.",
                    enumerableType == null ? null : enumerableType.getSimpleName(), constantName), null);
        }

        /**
         * General exception constructor left in place in case anybody wishes to subclass us.
         *
         * @param message The message for this exception.
         * @param cause   The cause of this exception.
         */
        protected ConstantNotFoundException(String message, Throwable cause) {
            super(message);
            if (cause != null) super.initCause(cause); // Allows for initCause() later on if cause == null.
        }
    }

}
