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
package nl.talsmasoftware.enumerables.jaxrs;

import nl.talsmasoftware.enumerables.Enumerable;

import javax.ws.rs.ext.ParamConverter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link ParamConverter} implementation for {@link Enumerable}, {@link Enumerable#parse(Class, CharSequence) parsing}
 * and {@link Enumerable#print(Enumerable) printing} values.
 *
 * @author Sjoerd Talsma
 */
final class EnumerableParamConverter<E extends Enumerable> implements ParamConverter<E> {
    private static final Logger LOGGER = Logger.getLogger(EnumerableParamConverter.class.getName());

    private final Class<E> enumerableType;

    EnumerableParamConverter(Class<E> enumerableType) {
        if (enumerableType == null) throw new NullPointerException("Enumerable type is <null>.");
        this.enumerableType = enumerableType;
    }

    public E fromString(String value) {
        E parsedValue = Enumerable.parse(enumerableType, value);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, this + " converted value \"" + value + "\" to " + parsedValue + ".");
        }
        return parsedValue;
    }

    public String toString(E value) {
        String printed = Enumerable.print(value);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, this + " converted value " + value + " to \"" + printed + "\".");
        }
        return printed;
    }

    public String toString() {
        return getClass().getSimpleName() + '{' + enumerableType.getName() + '}';
    }

}
