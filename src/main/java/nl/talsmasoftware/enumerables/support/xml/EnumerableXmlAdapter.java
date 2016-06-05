/*
 * Copyright (C) 2016 Talsma ICT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */

package nl.talsmasoftware.enumerables.support.xml;

import nl.talsmasoftware.enumerables.Enumerable;
import nl.talsmasoftware.enumerables.support.UnknownEnumerable;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:info@talsma-software.nl">Sjoerd Talsma</a>
 */
public class EnumerableXmlAdapter<E extends Enumerable> extends XmlAdapter<String, E> {
    /**
     * The actual enumerable type that is mapped.
     */
    private final Class<E> enumerableType;

    /**
     * Default constructor that will attempt to determine the actual enumerable type by the reflected generic type of
     * this instance.
     */
    public EnumerableXmlAdapter() {
        this(null);
    }

    /**
     * Constructor for the adapter, unmarshalling into the concrete {@link Enumerable} subtype that was specified.
     *
     * @param enumerableType The enumerable type to be used for unmarshalling plain string values.
     */
    @SuppressWarnings("unchecked")
    public EnumerableXmlAdapter(Class<E> enumerableType) {
        if (enumerableType == null) try { // TODO: Refactor into a cleaner solution.
            enumerableType = (Class<E>) findParameterizedRawType(
                    getClass(), EnumerableXmlAdapter.class).getActualTypeArguments()[0];
        } catch (RuntimeException rte) {
            Logger.getLogger(getClass().getName())
                    .log(Level.FINEST, "Unable to determine subtype of Enumerable objects to unmarshal.", rte);
        }
        this.enumerableType = enumerableType != null ? enumerableType : (Class<E>) UnknownEnumerable.class;
    }

    private static ParameterizedType findParameterizedRawType(Class<?> unknownSubtype, Class<?> rawType) {
        Type genericSuperclass = unknownSubtype.getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType
                && rawType.equals(((ParameterizedType) genericSuperclass).getRawType()))) {
            unknownSubtype = unknownSubtype.getSuperclass();
            genericSuperclass = unknownSubtype.getGenericSuperclass();
        }
        return (ParameterizedType) genericSuperclass;
    }

    /**
     * Translates the String value to a  parsed {@link Enumerable} object instance.
     *
     * @param value The enumerable value that should be parsed.
     * @return The parsed Enumerable object instance or <code>null</code> if the value itself was also <code>null</code>.
     * @see Enumerable#parse(Class, CharSequence)
     */
    public E unmarshal(String value) {
        return Enumerable.parse(enumerableType, value);
    }

    /**
     * Translates the {@link Enumerable} object instance into its plain String value.
     *
     * @param value The enumerable object instance to be marshalled.
     * @return The plain String representation of the specified value or <code>null</code> if the enumerable value itself
     * was also <code>null</code>.
     * @see Enumerable#print(Enumerable)
     */
    public String marshal(Enumerable value) {
        return Enumerable.print(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{enumerableType=" + enumerableType + '}';
    }

}
