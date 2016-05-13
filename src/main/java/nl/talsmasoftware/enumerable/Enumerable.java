package nl.talsmasoftware.enumerable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Character.toUpperCase;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.asList;
import static java.util.Collections.*;

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
 * Nasty in any case.
 * <p>
 * That is exactly the reason we've created the <code>Enumerable</code> type. It is similar to {@link Enum} in that it
 * represents a number of <em>known constants</em> but also offers a possibility to represent
 * <em>yet unknown additional values</em> by parsing them. In fact; parsing a known value results in the single constant
 * reference. Same goes for serialization and deserialization.
 * <p>
 * TODO: Talk about values(), ordinal(), and other enum concepts.
 * TODO: Document an example of how using it.
 * TODO: DescriptionProvider api.
 * TODO: Generify? (instead of String, maybe also support other immutable value object types?) Not sure about this one.
 *
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public abstract class Enumerable implements Comparable<Enumerable>, Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Map from concrete subclass type to array of reflected constants.
     */
    private static final ConcurrentMap<Class<?>, Object> CONSTANTS_CACHE =
            new ConcurrentHashMap<Class<?>, Object>();

    /**
     * The value of this abstract Enumerable type.
     */
    private final String value;

    /**
     * Constructor that may only be used by a private (!) constructor of the subclass.
     *
     * @param value The value of this Enumerable object.
     */
    protected Enumerable(String value) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("Value of type %s was null.", getClass().getSimpleName()));
        }
        this.value = value;
    }

    /**
     * <ul>
     * <li>If and only if a constant was detected for this value, the constants name and ordinal.</li>
     * <li>Otherwise {@code NONE}.</li>
     * <li>Or {@code null} in case the type has not yet been reflected.</li>
     * </ul>
     */
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
     * Provides a short description of this {@link Enumerable} object value that could be used in drop-down boxes etc.
     * <p>
     * The intention is to provide a human-friendly variant of the constant or value represented by this object.
     *
     * @return The human-friendly description of this enumerable object.
     */
    public String getDescription() {
        // TODO: Copy (and document) 'DescriptionProvider' concept.
        String description = name();
        if (description == null) return getValue();
        else if (description.length() > 0)
            description = toUpperCase(description.charAt(0)) + description.substring(1).toLowerCase(Locale.ENGLISH);
        return description.replace('_', ' ');
    }

    /**
     * @return hashcode for this enumerable object.
     */
    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    public int compareTo(Enumerable other) {
        if (other == null) throw new NullPointerException("The enumerable object to compare with was null.");
        final Class<? extends Enumerable> myType = getClass();
        // TODO: Probably a bug: Comparison should be commutative, which it is not due to the isInstance() check:
        int comparison = myType.isInstance(other) ? 0 : myType.getName().compareTo(other.getClass().getName());
        if (comparison == 0) {
            final int ordinal = ordinal();
            comparison = ordinal - other.ordinal();
            if (comparison == 0 && ordinal == NameAndOrdinal.NONE.ordinal) { // Both are non-constant: compare values!
                // Preferably sort case-insensitive, however, do not declare case-only differences as equal:
                comparison = value.compareToIgnoreCase(other.value);
                if (comparison == 0) comparison = value.compareTo(other.value);
            }
        }
        return comparison;
    }

    /**
     * Vergelijkt volgens de regels van {@link #compareTo(Enumerable)}.
     *
     * @param other Het object om mee te vergelijken.
     * @return equals op basis van type en waarde.
     */
    @Override
    public final boolean equals(Object other) {
        return this == other || (other instanceof Enumerable && 0 == compareTo((Enumerable) other));
    }

    /**
     * @return De result of name() if present, otherwise the <code>value</code>.
     */
    private String _nameOrValue() {
        final String name = name();
        return name != null ? name : value;
    }

    /**
     * The implementation of <code>toString()</code> is importantly different from Java's {@link Enum} implementation,
     * which by default returns the {@link #name() name} of the enumeration constant.
     * The problem with this is, is that lazy developers can start depending on that behaviour and comparing with
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
        final String name = name();
        if (name != null && !name.equals(value)) {
            result.append("name=\"").append(name).append("\", ");
        }
        return result.append("value=\"").append(value).append("\"}").toString();
    }

    /**
     * @return Re-parse an enumerable object after deserialisatie to ensure that constants can be compared by ==
     * (which is not recommended by the way).
     */
    protected Object readResolve() {
        return parse(getClass(), value);
    }

    /**
     * Geeft de daadwerkelijke (niet-geclonede) array met constanten voor het gevraagde type terug.
     * Deze methode scheelt een clone, maar is daardoor tevens alleen voor intern gebruik en de array mag absoluut
     * niet gemuteerd worden door aanroepers!
     *
     * @param <E>  Het concrete Enumerable type.
     * @param type Het concrete subtype.
     * @return Haalt alle gedeclareerde public constanten van het concrete subtype op.
     */
    @SuppressWarnings("unchecked")
    private static <E extends Enumerable> E[] _rawValues(final Class<E> type) {
        if (type == null) throw new IllegalArgumentException("Type is null.");
        E[] values = (E[]) CONSTANTS_CACHE.get(type);
        if (values == null) {
            final List<E> constants = new ArrayList<E>();
            for (Field field : type.getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                if (isStatic(modifiers) && isFinal(modifiers)) {
                    if (isPublic(modifiers) && type.equals(field.getType())) {
                        try {
                            final E foundConstant = (E) field.get(null);
                            ((Enumerable) foundConstant)._nameAndOrdinal =
                                    new NameAndOrdinal(constants.size(), field.getName());
                            constants.add(foundConstant);
                        } catch (IllegalAccessException iae) {
                            throw new IllegalStateException(String.format("Mocht constante '%s.%s' niet lezen!",
                                    type.getName(), field.getName()), iae);
                        }
//                    } else {
//                        registerOmschrijvingProvider(type, field);
                    }
                }
            }
            CONSTANTS_CACHE.putIfAbsent(type, constants.toArray((E[]) Array.newInstance(type, constants.size())));
            values = (E[]) CONSTANTS_CACHE.get(type);
        }
        return values;
    }

    /**
     * Zoekt alle publeke constanten van het opgegeven type op die in het betreffende type zelf gedefinieerd staan.
     * <p>
     * <p>
     * Deze constanten moeten <strong>public final static</strong> zijn.
     * </p>
     *
     * @param <E>  Het concrete niet-abstracte Enumerable type.
     * @param type Het concrete subtype.
     * @return Haalt alle gedeclareerde public constanten van het concrete subtype op.
     */
    public static <E extends Enumerable> E[] values(Class<E> type) {
        return _rawValues(type).clone();
    }

    /**
     * This method is comparable with the {@link Enum#valueOf(Class, String)} method.
     * <p>
     * This method returns the constant with the specified <code>name</code>.
     * If there is no enumerable constant of the requested <code>type</code> found by that <code>name</code>,
     * the method will throw an {@link EnumerableConstantNotFoundException exception}.
     * <p>
     * Please note that this method looks at the {@link #name() constant name} and <strong>not</strong>
     * the {@link #getValue() value} of this enumerable object. To obtain an enumerable object from a
     * specific <code>value</code>, please use the {@link #parse(Class, CharSequence) parse} method instead.
     * That method will not throw any exceptions for yet-unknown values, but attempt to instantiate a
     * new enumerable instance in that case.
     *
     * @param <E>  The actual subtype of <code>Enumerable</code> to return the named constant value for.
     * @param type The actual subtype of <code>Enumerable</code> to return the named constant value for.
     * @param name The name of the enumerable constant to return (as declared in the code).
     * @return The enumerable constant with the requested name.
     * @throws EnumerableConstantNotFoundException in case there is no such constant defined.
     * @see #parse(Class, CharSequence)
     */
    public static <E extends Enumerable> E valueOf(Class<E> type, CharSequence name)
            throws EnumerableConstantNotFoundException {
        if (name != null) {
            final String nameString = name.toString();
            for (E enumerable : _rawValues(type)) {
                if (nameString.equals(enumerable.name())) {
                    return enumerable;
                }
            }
        }
        throw new EnumerableConstantNotFoundException(type, name);
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
     * Een eenvoudige manier om een set van Enumerable waardes als constante te kunnen opnemen.
     * <p>
     * Deze operatie dient als factory methode voor een unmodifiable set en kan wellicht later uitgebreid worden voor
     * sets die alleen uit constanten bestaan (vergelijkbaar met EnumSet).
     *
     * @param <E>    Het concrete niet-abstracte Enumerable type.
     * @param values De waardes om als set te representeren.
     * @return De set van Enumerable
     */
    public static <E extends Enumerable> Set<E> setOf(E... values) {
        Set<E> result = null;
        if (values == null || values.length == 0) {
            result = emptySet();
        } else if (values.length == 1) {
            result = singleton(values[0]);
        } else { // For now this 'just works', maybe we can implement a set that is based on the EnumSet idea.
            result = unmodifiableSet(new LinkedHashSet<E>(asList(values)));
        }
        return result;
    }

    /**
     * Helper die uit bekende constanten vaste instanties teruggeeft (vergelijkbaar met enum parsing).
     *
     * @param <E>     Het concrete niet-abstracte Enumerable type.
     * @param type    Het concrete subtype van deze klasse (om constanten uit te halen).
     * @param value   De te parsen constante waarde.
     * @param factory De 'factory' om een nieuw waarde object mee aan te maken als het niet een van de constanten is.
     * @return De herkende waarde of <code>null</code>.
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
        if (constructor == null)
            throw new IllegalStateException(String.format("String constructor for %s is null.", type));
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
     * Internal class to represent name and ordinal to be able to simulate standard Enum behaviour..
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

}
